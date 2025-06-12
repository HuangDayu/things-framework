package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.api.container.ThingsRegister;
import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.api.message.ThingsIntercepting;
import cn.huangdayu.things.common.annotation.*;
import cn.huangdayu.things.common.events.ThingsContainerCancelledEvent;
import cn.huangdayu.things.common.events.ThingsContainerRegisteredEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.engine.wrapper.*;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;
import static cn.huangdayu.things.common.utils.ThingsUtils.*;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsRegisterExecutor extends ThingsContainerManager implements ThingsRegister {

    private final ThingsEventObserver thingsEventObserver;

    @Override
    public void register(ThingsContainer thingsContainer) {
        long start = System.currentTimeMillis();
        AtomicInteger sum = new AtomicInteger();
        sum.addAndGet(findBeans(thingsContainer, Things.class, this::findThingsServices).get());
        sum.addAndGet(findBeans(thingsContainer, ThingsPropertyEntity.class, this::findThingsProperties).get());
        sum.addAndGet(findBeans(thingsContainer, ThingsEventEntity.class, this::findThingsEvents).get());
        sum.addAndGet(findBeans(thingsContainer, ThingsListener.class, this::findThingsListener).get());
        sum.addAndGet(findBeans(thingsContainer, ThingsInterceptor.class, this::findThingsInterceptors).get());
        sum.addAndGet(findBeans(thingsContainer, ThingsHandler.class, this::findThingsHandlers).get());
        sum.addAndGet(findBeans(thingsContainer, ThingsClient.class, this::findThingsClients).get());
        thingsContainers.add(thingsContainer);
        thingsEventObserver.notifyObservers(new ThingsContainerRegisteredEvent(this, thingsContainer));
        log.info("ThingsEngine register {} beans for container [{}] takes {} milliseconds.", sum.get(), thingsContainer.name(), System.currentTimeMillis() - start);
    }

    @Override
    public void cancel(ThingsContainer thingsContainer) {
        long start = System.currentTimeMillis();
        AtomicInteger sum = new AtomicInteger();
        sum.addAndGet(deleteTable(thingsEntityTable, v -> v.getThingsContainer() == thingsContainer).get());
        sum.addAndGet(deleteTable(thingsFunctionTable, v -> v.getThingsContainer() == thingsContainer).get());
        sum.addAndGet(deleteMap(thingsPropertyMap, v -> v.getThingsContainer() == thingsContainer).get());
        sum.addAndGet(deleteTable(devicePropertyMap, v -> v.getThingsContainer() == thingsContainer).get());
        sum.addAndGet(deleteTable(thingsEventsTable, v -> v.getThingsContainer() == thingsContainer).get());
        sum.addAndGet(deleteTableForSet(thingsEventsListenerTable, v -> v.getThingsContainer() == thingsContainer).get());
        sum.addAndGet(deleteTableForSet(thingsPropertyListenerTable, v -> v.getThingsContainer() == thingsContainer).get());
        sum.addAndGet(deleteTableForSet(thingsInterceptorsTable, v -> v.getThingsContainer() == thingsContainer).get());
        sum.addAndGet(deleteTableForSet(thingsHandlersTable, v -> v.getThingsContainer() == thingsContainer).get());
        sum.addAndGet(deleteTable(thingsClientTable, v -> v.getThingsContainer() == thingsContainer).get());
        thingsContainers.remove(thingsContainer);
        thingsEventObserver.notifyObservers(new ThingsContainerCancelledEvent(this, thingsContainer));
        log.info("ThingsEngine cancel {} methods or beans for container [{}] takes {} milliseconds.", sum.get(), thingsContainer.name(), System.currentTimeMillis() - start);
    }

    @Override
    public void register(String containerName, Object bean) {
        ThingsFunctionContainer thingsFunctionContainer = new ThingsFunctionContainer(containerName, bean);
        register(thingsFunctionContainer);
        thingsFunctionMap.put(bean, thingsFunctionContainer);
    }

    @Override
    public void cancel(String containerName, Object bean) {
        ThingsContainer thingsContainer = thingsFunctionMap.get(bean);
        if (thingsContainer != null) {
            cancel(thingsContainer);
            thingsFunctionMap.remove(bean);
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
    private <T extends Annotation> AtomicInteger findBeans(ThingsContainer thingsContainer, Class<T> annotationType, BeanConsumer<ThingsContainer, T, Object> beanConsumer) {
        AtomicInteger sumBeans = new AtomicInteger();
        Map<String, Object> thingsBeans = thingsContainer.getBeans(annotationType);
        if (CollUtil.isNotEmpty(thingsBeans)) {
            thingsBeans.forEach((key, value) -> {
                T beanAnnotation = findBeanAnnotation(value, annotationType);
                if (beanAnnotation != null) {
                    beanConsumer.accept(thingsContainer, beanAnnotation, value);
                    sumBeans.addAndGet(1);
                }
            });
        }
        return sumBeans;
    }

    private void findThingsProperties(ThingsContainer thingsContainer, ThingsPropertyEntity thingsPropertyEntity, Object bean) {
        if (!thingsPropertyEntity.enabled()) {
            return;
        }
        if (thingsPropertyMap.get(thingsPropertyEntity.productCode()) == null) {
            thingsPropertyMap.put(thingsPropertyEntity.productCode(), new ThingsProperty(thingsContainer, thingsPropertyEntity, bean));
        } else {
            log.error("Duplicate registration ThingsProperty ({}), only effective once, effective ThingsProperty {} , invalid ThingsProperty : {}",
                    thingsPropertyEntity.productCode(), thingsPropertyMap.get(thingsPropertyEntity.productCode()).getBean().getClass(), bean.getClass());
        }
    }

    private void findThingsEvents(ThingsContainer thingsContainer, ThingsEventEntity thingsEventEntity, Object bean) {
        if (!thingsEventEntity.enabled()) {
            return;
        }
        thingsEventsTable.put(thingsEventEntity.identifier(), thingsEventEntity.productCode(), new ThingsEvents(thingsContainer, thingsEventEntity, bean));
    }

    private void findThingsServices(ThingsContainer thingsContainer, Things things, Object bean) {
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
        thingsEntityTable.put(things.productCode(), bean.getClass(), new ThingsEntity(thingsContainer, things.productCode(), bean, things));
    }


    private boolean findThingsService(ThingsContainer thingsContainer, Things things, Object bean, Method method) {
        ThingsService thingsService = AnnotationUtil.getAnnotation(method, ThingsService.class);
        if (thingsService != null) {
            String identifier = StrUtil.isNotBlank(thingsService.identifier()) ? thingsService.identifier() : method.getName();
            method.trySetAccessible();
            ThingsFunction thingsFunction = new ThingsFunction(thingsContainer, things, bean, method, thingsService.async(), thingsService, scanParameter(method));
            thingsFunctionTable.put(identifier, things.productCode(), thingsFunction);
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
            Set<ThingsFunction> thingsFunctions = thingsEventsListenerTable.get(thingsEventListener.identifier(), thingsEventListener.productCode());
            if (thingsFunctions == null) {
                thingsFunctions = new ConcurrentHashSet<>();
            }
            thingsFunctions.add(thingsServices);
            thingsEventsListenerTable.put(thingsEventListener.identifier(), thingsEventListener.productCode(), thingsFunctions);
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
            Set<ThingsFunction> thingsFunctions = thingsPropertyListenerTable.get(identifier, productCode);
            if (thingsFunctions == null) {
                thingsFunctions = new ConcurrentHashSet<>();
            }
            thingsFunctions.add(thingsServices);
            thingsPropertyListenerTable.put(identifier, productCode, thingsFunctions);
            return true;
        }
        return false;
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
        Set<ThingsInterceptors> interceptors = thingsInterceptorsTable.get(identifier, productCode);
        if (interceptors == null) {
            interceptors = new ConcurrentHashSet<>();
        }
        interceptors.add(new ThingsInterceptors(thingsContainer, thingsInterceptor, (ThingsIntercepting) bean, thingsInterceptor.chainingType()));
        thingsInterceptorsTable.put(identifier, productCode, interceptors);
    }


    private void findThingsHandlers(ThingsContainer thingsContainer, ThingsHandler thingsHandler, Object bean) {
        if (!thingsHandler.enabled()) {
            return;
        }
        if (!(bean instanceof ThingsHandling)) {
            log.error("Things bean is not ThingsHandling : {}", bean.getClass().getName());
            return;
        }
        String identifier = thingsHandler.method() + THINGS_SEPARATOR + thingsHandler.identifier();
        String productCode = thingsHandler.productCode();
        Set<ThingsHandlers> thingsHandlers = thingsHandlersTable.get(identifier, productCode);
        if (thingsHandlers == null) {
            thingsHandlers = new ConcurrentHashSet<>();
        }
        thingsHandlers.add(new ThingsHandlers(thingsContainer, thingsHandler, (ThingsHandling) bean, thingsHandler.chainingType()));
        thingsHandlersTable.put(identifier, productCode, thingsHandlers);
    }

    private void findThingsClients(ThingsContainer thingsContainer, ThingsClient thingsClient, Object bean) {
        if (!thingsClient.enabled()) {
            return;
        }
        for (Class<?> aClass : ClassUtils.getAllInterfaces(bean)) {
            Method[] methods = ReflectUtil.getMethods(aClass);
            for (Method method : methods) {
                ThingsService thingsService = method.getAnnotation(ThingsService.class);
                if (thingsService != null) {
                    String productCode = StrUtil.isNotBlank(thingsClient.productCode()) ? thingsClient.productCode() : thingsService.productCode();
                    String identifier = StrUtil.isNotBlank(thingsService.identifier()) ? thingsService.identifier() : method.getName();
                    if (StrUtil.isAllNotBlank(identifier, productCode)) {
                        ThingsFunction thingsFunction = new ThingsFunction(thingsContainer, thingsClient, bean, method, thingsService.async(), thingsService, scanParameter(method));
                        thingsClientTable.put(identifier, productCode, thingsFunction);
                    }
                }
            }
        }
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
