package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.*;
import cn.huangdayu.things.common.event.ThingsContainerUpdateEvent;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.engine.chaining.filters.ThingsFilter;
import cn.huangdayu.things.engine.chaining.interceptor.ThingsInterceptor;
import cn.huangdayu.things.engine.container.ThingsContainer;
import cn.huangdayu.things.engine.core.ThingsContainerEngine;
import cn.huangdayu.things.engine.wrapper.*;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.map.multi.Table;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.utils.ThingsUtils.*;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsContainerExecutor extends ThingsEngineBaseExecutor implements ThingsContainerEngine {

    private final ThingsEventObserver thingsEventObserver;

    @Override
    public void register(ThingsContainer thingsContainer) {
        long start = System.currentTimeMillis();
        findBeans(thingsContainer, Things.class, this::findThingsFunctions);
        findBeans(thingsContainer, ThingsProperty.class, this::findThingsProperties);
        findBeans(thingsContainer, ThingsEvent.class, this::findThingsEvents);
        findBeans(thingsContainer, ThingsListener.class, this::findThingsListener);
        findBeans(thingsContainer, ThingsFiltering.class, this::findThingsFilters);
        findBeans(thingsContainer, ThingsIntercepting.class, this::findThingsInterceptors);
        thingsEventObserver.notifyObservers(new ThingsContainerUpdateEvent(thingsContainer));
        log.info("Started ThingsEngine in {} milliseconds with context {}.", System.currentTimeMillis() - start, thingsContainer.name());
    }

    @Override
    public void cancel(ThingsContainer thingsContainer) {
        deleteTable(THINGS_SERVICES_TABLE, v -> v.getThingsContainer() == thingsContainer);
        deleteTable(DEVICE_PROPERTY_MAP, v -> v.getThingsContainer() == thingsContainer);
        deleteTable(THINGS_EVENTS_TABLE, v -> v.getThingsContainer() == thingsContainer);
        cancelEventListener(thingsContainer);
        deleteMap(PRODUCT_PROPERTY_MAP, v -> v.getThingsContainer() == thingsContainer);
        thingsEventObserver.notifyObservers(new ThingsContainerUpdateEvent(thingsContainer));
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
            PRODUCT_PROPERTY_MAP.put(thingsProperty.productCode(), new ThingsProperties(thingsContainer, thingsProperty, bean));
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
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        Arrays.asList(methods).parallelStream().forEach(method -> {
            try {
                findFirst(() -> findThingsService(thingsContainer, things, bean, method),
                        () -> findThingsEventListener(thingsContainer, things, bean, method),
                        () -> findThingsPropertyListener(thingsContainer, things, things.productCode(), bean, method));
            } catch (Exception e) {
                log.error("Things engine scan service {}.{} exception : {}", bean.getClass().getSimpleName(), method.getName(), e.getMessage());
            }
        });
    }


    private boolean findThingsService(ThingsContainer thingsContainer, Things things, Object bean, Method method) {
        ThingsService thingsService = AnnotationUtils.findAnnotation(method, ThingsService.class);
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
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
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
        ThingsEventListener thingsEventListener = AnnotationUtils.findAnnotation(method, ThingsEventListener.class);
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
        ThingsPropertyListener thingsPropertyListener = AnnotationUtils.findAnnotation(method, ThingsPropertyListener.class);
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


    private void findThingsFilters(ThingsContainer thingsContainer, ThingsFiltering thingsFiltering, Object bean) {
        if (!thingsFiltering.enabled()) {
            return;
        }
        if (!(bean instanceof ThingsFilter)) {
            log.error("Things bean is not Filter : {}", bean.getClass().getName());
        }
        String identifier = StrUtil.isNotBlank(thingsFiltering.identifier()) ? thingsFiltering.identifier() : THINGS_WILDCARD;
        String productCode = StrUtil.isNotBlank(thingsFiltering.productCode()) ? thingsFiltering.productCode() : THINGS_WILDCARD;
        Set<ThingsFilters> filters = THINGS_FILTERS_TABLE.get(identifier, productCode);
        if (filters == null) {
            filters = new ConcurrentHashSet<>();
        }
        filters.add(new ThingsFilters(thingsFiltering, (ThingsFilter) bean));
        THINGS_FILTERS_TABLE.put(identifier, productCode, filters);
    }

    private void findThingsInterceptors(ThingsContainer thingsContainer, ThingsIntercepting thingsIntercepting, Object bean) {
        if (!thingsIntercepting.enabled()) {
            return;
        }
        if (!(bean instanceof ThingsInterceptor)) {
            log.error("Things bean is not Filter : {}", bean.getClass().getName());
        }
        String identifier = StrUtil.isNotBlank(thingsIntercepting.identifier()) ? thingsIntercepting.identifier() : THINGS_WILDCARD;
        String productCode = StrUtil.isNotBlank(thingsIntercepting.productCode()) ? thingsIntercepting.productCode() : THINGS_WILDCARD;
        Table<String, String, Set<ThingsInterceptors>> table = thingsIntercepting.request() ? THINGS_REQUEST_INTERCEPTORS_TABLE : THINGS_RESPONSE_INTERCEPTORS_TABLE;
        Set<ThingsInterceptors> interceptors = table.get(identifier, productCode);
        if (interceptors == null) {
            interceptors = new ConcurrentHashSet<>();
        }
        interceptors.add(new ThingsInterceptors(thingsIntercepting, (ThingsInterceptor) bean));
        table.put(identifier, productCode, interceptors);
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
