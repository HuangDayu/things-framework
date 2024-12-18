package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.api.container.ThingsRegister;
import cn.huangdayu.things.api.message.ThingsFiltering;
import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.api.message.ThingsIntercepting;
import cn.huangdayu.things.common.annotation.*;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsContainerUpdatedEvent;
import cn.huangdayu.things.engine.wrapper.*;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.map.multi.Table;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.ERROR;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;
import static cn.huangdayu.things.common.utils.ThingsUtils.*;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsRegisterExecutor extends ThingsBaseExecutor implements ThingsRegister {

    private final ThingsEventObserver thingsEventObserver;

    @Override
    public void register(ThingsContainer thingsContainer) {
        if (THINGS_CONTAINERS.get(thingsContainer.name()) != null) {
            throw new ThingsException(ERROR, "Container name already exists.");
        }
        long start = System.currentTimeMillis();
        findBeans(thingsContainer, Things.class, this::findThingsFunctions);
        findBeans(thingsContainer, ThingsProperty.class, this::findThingsProperties);
        findBeans(thingsContainer, ThingsEvent.class, this::findThingsEvents);
        findBeans(thingsContainer, ThingsListener.class, this::findThingsListener);
        findBeans(thingsContainer, ThingsFilter.class, this::findThingsFilters);
        findBeans(thingsContainer, ThingsInterceptor.class, this::findThingsInterceptors);
        findBeans(thingsContainer, ThingsHandler.class, this::findThingsThingsHandlers);
        thingsEventObserver.notifyObservers(new ThingsContainerUpdatedEvent(thingsContainer));
        log.info("Started ThingsEngine in {} milliseconds with context {}.", System.currentTimeMillis() - start, thingsContainer.name());
        THINGS_CONTAINERS.put(thingsContainer.name(), thingsContainer);
    }

    @Override
    public void cancel(ThingsContainer thingsContainer) {
        deleteTable(THINGS_SERVICES_TABLE, v -> v.getThingsContainer() == thingsContainer);
        deleteTable(DEVICE_PROPERTY_MAP, v -> v.getThingsContainer() == thingsContainer);
        deleteTable(THINGS_EVENTS_TABLE, v -> v.getThingsContainer() == thingsContainer);
        cancelEventListener(thingsContainer);
        deleteMap(PRODUCT_PROPERTY_MAP, v -> v.getThingsContainer() == thingsContainer);
        thingsEventObserver.notifyObservers(new ThingsContainerUpdatedEvent(thingsContainer));
        THINGS_CONTAINERS.remove(thingsContainer.name());
    }

    @Override
    public void register(String containerName, Object bean) {
        register(new ThingsFunctionContainer(containerName, bean));
    }

    @Override
    public void cancel(String containerName, Object bean) {
        cancel(new ThingsFunctionContainer(containerName, bean));
    }

    private void cancelEventListener(ThingsContainer thingsContainer) {
        for (Table.Cell<String, String, Set<ThingsFunction>> cell : THINGS_EVENTS_LISTENER_TABLE.cellSet()) {
            Set<ThingsFunction> collect = cell.getValue().stream().filter(v -> v.getThingsContainer() == thingsContainer).collect(Collectors.toSet());
            if (CollUtil.isNotEmpty(collect)) {
                cell.getValue().removeAll(collect);
            }
            if (cell.getValue().isEmpty()) {
                THINGS_EVENTS_LISTENER_TABLE.remove(cell.getRowKey(), cell.getColumnKey());
            }
        }
    }


    /**
     * 查找bean
     * 不可以多线程并发执行，否则会诱发阻塞死锁
     * "ForkJoinPool.commonPool-worker-1" prio=0 tid=0x0 nid=0x0 blocked
     * java.lang.Thread.State: BLOCKED
     * on java.util.concurrent.ConcurrentHashMap@70e6329c owned by "main" Id=1
     * at app//org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:217)
     * at app//org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335)
     * at app//org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200)
     * at app//org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeansWithAnnotation(DefaultListableBeanFactory.java:713)
     * at app//org.springframework.context.support.AbstractApplicationContext.getBeansWithAnnotation(AbstractApplicationContext.java:1402)
     *
     * <a>https://github.com/spring-projects/spring-framework/issues/31181</a>
     *
     * @param thingsContainer
     * @param annotationType
     * @param beanConsumer
     * @param <T>
     */
    private <T extends Annotation> void findBeans(ThingsContainer thingsContainer, Class<T> annotationType, BeanConsumer<ThingsContainer, T, Object> beanConsumer) {
        Map<String, Object> thingsBeans = thingsContainer.getBeans(annotationType);
        if (CollUtil.isNotEmpty(thingsBeans)) {
            thingsBeans.entrySet().parallelStream().forEach(entry -> {
                T beanAnnotation = findBeanAnnotation(entry.getValue(), annotationType);
                if (beanAnnotation != null) {
                    beanConsumer.accept(thingsContainer, beanAnnotation, entry.getValue());
                }
            });
        }
    }

    private void findThingsProperties(ThingsContainer thingsContainer, ThingsProperty thingsProperty, Object bean) {
        if (!thingsProperty.enabled()) {
            return;
        }
        if (PRODUCT_PROPERTY_MAP.get(thingsProperty.productCode()) == null) {
            PRODUCT_PROPERTY_MAP.put(thingsProperty.productCode(), new ThingsPropertyWrapper(thingsContainer, thingsProperty, bean));
        } else {
            log.error("Duplicate registration ThingsProperty ({}), only effective once, effective ThingsProperty {} , invalid ThingsProperty : {}",
                    thingsProperty.productCode(), PRODUCT_PROPERTY_MAP.get(thingsProperty.productCode()).getBean().getClass(), bean.getClass());
        }
    }

    private void findThingsEvents(ThingsContainer thingsContainer, ThingsEvent thingsEvent, Object bean) {
        if (!thingsEvent.enabled()) {
            return;
        }
        THINGS_EVENTS_TABLE.put(thingsEvent.identifier(), thingsEvent.productCode(), new ThingsEvents(thingsContainer, thingsEvent, bean));
    }

    private void findThingsFunctions(ThingsContainer thingsContainer, Things things, Object bean) {
        if (!things.enabled()) {
            return;
        }
        Method[] methods = ReflectUtil.getMethods(bean.getClass());
        Arrays.asList(methods).parallelStream().forEach(method -> {
            try {
                findFirst(() -> findThingsService(thingsContainer, things, bean, method),
                        () -> findThingsEventListener(thingsContainer, things, bean, method),
                        () -> findThingsPropertyListener(thingsContainer, things, things.productCode(), bean, method));
            } catch (Exception e) {
                log.error("Things engine scan service {}.{} exception : {}", bean.getClass().getSimpleName(), method.getName(), e.getMessage());
            }
        });
        THINGS_ENTITY_TABLE.put(things.productCode(), bean.getClass(), new ThingsEntity(things.productCode(), bean, things));
    }


    private boolean findThingsService(ThingsContainer thingsContainer, Things things, Object bean, Method method) {
        ThingsService thingsService = AnnotationUtil.getAnnotation(method, ThingsService.class);
        if (thingsService != null) {
            String identifier = StrUtil.isNotBlank(thingsService.identifier()) ? thingsService.identifier() : method.getName();
            method.trySetAccessible();
            ThingsFunction thingsFunction = new ThingsFunction(thingsContainer, things, bean, method, thingsService.async(), thingsService, scanParameter(method));
            THINGS_SERVICES_TABLE.put(identifier, things.productCode(), thingsFunction);
            return true;
        }
        return false;
    }

    private void findThingsListener(ThingsContainer thingsContainer, ThingsListener thingsListener, Object bean) {
        if (!thingsListener.enabled()) {
            return;
        }
        Method[] methods = ReflectUtil.getMethods(bean.getClass());
        Arrays.asList(methods).parallelStream().forEach(method -> {
            try {
                findFirst(() -> findThingsEventListener(thingsContainer, thingsListener, bean, method),
                        () -> findThingsPropertyListener(thingsContainer, thingsListener, null, bean, method));
            } catch (Exception e) {
                log.error("Things engine scan event listener {}.{} exception : {}", bean.getClass().getSimpleName(), method.getName(), e.getMessage());
            }
        });
    }


    private boolean findThingsEventListener(ThingsContainer thingsContainer, Annotation beanAnnotation, Object bean, Method method) {
        ThingsEventListener thingsEventListener = AnnotationUtil.getAnnotation(method, ThingsEventListener.class);
        if (thingsEventListener != null) {
            method.trySetAccessible();
            ThingsFunction thingsServices = new ThingsFunction(thingsContainer, beanAnnotation, bean, method, true, thingsEventListener, scanParameter(method));
            Set<ThingsFunction> thingsFunctions = THINGS_EVENTS_LISTENER_TABLE.get(thingsEventListener.identifier(), thingsEventListener.productCode());
            if (thingsFunctions == null) {
                thingsFunctions = new ConcurrentHashSet<>();
            }
            thingsFunctions.add(thingsServices);
            THINGS_EVENTS_LISTENER_TABLE.put(thingsEventListener.identifier(), thingsEventListener.productCode(), thingsFunctions);
            return true;
        }
        return false;
    }

    private boolean findThingsPropertyListener(ThingsContainer thingsContainer, Annotation things, String productCode, Object bean, Method method) {
        ThingsPropertyListener thingsPropertyListener = AnnotationUtil.getAnnotation(method, ThingsPropertyListener.class);
        if (thingsPropertyListener != null) {
            String identifier = thingsPropertyListener.identifier();
            productCode = StrUtil.isNotBlank(thingsPropertyListener.productCode()) ? thingsPropertyListener.productCode() : productCode;
            if (StrUtil.isBlank(productCode)) {
                return false;
            }
            method.trySetAccessible();
            ThingsFunction thingsServices = new ThingsFunction(thingsContainer, things, bean, method, true, thingsPropertyListener, scanParameter(method));
            Set<ThingsFunction> thingsFunctions = THINGS_PROPERTY_LISTENER_TABLE.get(identifier, productCode);
            if (thingsFunctions == null) {
                thingsFunctions = new ConcurrentHashSet<>();
            }
            thingsFunctions.add(thingsServices);
            THINGS_PROPERTY_LISTENER_TABLE.put(identifier, productCode, thingsFunctions);
            return true;
        }
        return false;
    }


    private void findThingsFilters(ThingsContainer thingsContainer, ThingsFilter thingsFilter, Object bean) {
        if (!thingsFilter.enabled()) {
            return;
        }
        if (!(bean instanceof ThingsFiltering)) {
            log.error("Things bean is not ThingsFiltering : {}", bean.getClass().getName());
            return;
        }
        String identifier = thingsFilter.method() + THINGS_SEPARATOR + thingsFilter.identifier();
        String productCode = thingsFilter.productCode();
        Set<ThingsFilters> filters = THINGS_FILTERS_TABLE.get(identifier, productCode);
        if (filters == null) {
            filters = new ConcurrentHashSet<>();
        }
        filters.add(new ThingsFilters(thingsFilter, (ThingsFiltering) bean, thingsFilter.source()));
        THINGS_FILTERS_TABLE.put(identifier, productCode, filters);
    }

    private void findThingsInterceptors(ThingsContainer thingsContainer, ThingsInterceptor thingsInterceptor, Object bean) {
        if (!thingsInterceptor.enabled()) {
            return;
        }
        if (!(bean instanceof ThingsIntercepting)) {
            log.error("Things bean is not ThingsIntercepting : {}", bean.getClass().getName());
            return;
        }
        String identifier = thingsInterceptor.method() + THINGS_SEPARATOR + thingsInterceptor.identifier();
        String productCode = thingsInterceptor.productCode();
        Set<ThingsInterceptors> interceptors = THINGS_INTERCEPTORS_TABLE.get(identifier, productCode);
        if (interceptors == null) {
            interceptors = new ConcurrentHashSet<>();
        }
        interceptors.add(new ThingsInterceptors(thingsInterceptor, (ThingsIntercepting) bean, thingsInterceptor.source()));
        THINGS_INTERCEPTORS_TABLE.put(identifier, productCode, interceptors);
    }


    private void findThingsThingsHandlers(ThingsContainer thingsContainer, ThingsHandler thingsHandler, Object bean) {
        if (!thingsHandler.enabled()) {
            return;
        }
        if (!(bean instanceof ThingsHandling)) {
            log.error("Things bean is not ThingsHandling : {}", bean.getClass().getName());
            return;
        }
        String identifier = thingsHandler.method() + THINGS_SEPARATOR + thingsHandler.identifier();
        String productCode = thingsHandler.productCode();
        Set<ThingsHandlers> thingsHandlers = THINGS_HANDLERS_TABLE.get(identifier, productCode);
        if (thingsHandlers == null) {
            thingsHandlers = new ConcurrentHashSet<>();
        }
        thingsHandlers.add(new ThingsHandlers(thingsHandler, (ThingsHandling) bean, thingsHandler.source()));
        THINGS_HANDLERS_TABLE.put(identifier, productCode, thingsHandlers);
    }

    private ThingsParameter[] scanParameter(Method method) {
        ThingsParameter[] thingsParameters = new ThingsParameter[method.getParameters().length];
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            int finalI = i;
            thingsParameters[i] = findFirst(() -> scanAnnotation(parameter, finalI, ThingsParam.class),
                    () -> scanAnnotation(parameter, finalI, ThingsMessage.class),
                    () -> scanAnnotation(parameter, finalI, ThingsPayload.class),
                    () -> scanAnnotation(parameter, finalI, ThingsMetadata.class),
                    () -> scanAnnotation(parameter, finalI, ThingsInject.class),
                    () -> new ThingsParameter(parameter, finalI, parameter.getType(), parameter.getName(), null));
        }
        return thingsParameters;
    }


    private ThingsParameter scanAnnotation(Parameter parameter, int index, Class<? extends Annotation> annotationClass) {
        Annotation annotation = parameter.getAnnotation(annotationClass);
        if (annotation != null) {
            return new ThingsParameter(parameter, index, parameter.getType(), parameter.getName(), annotation);
        }
        return null;
    }

    public static interface BeanConsumer<A, T, U> {
        void accept(A a, T t, U u);
    }

}
