package cn.huangdayu.things.sofabiz;

import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import cn.hutool.core.collection.CollUtil;
import com.alipay.sofa.koupleless.common.BizRuntimeContextRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public class ThingsSofaBizBeanPostProcessor implements BeanFactoryPostProcessor {


    private static final Set<String> THINGS_SERVICES = new HashSet<>() {
        {
            add("cn.huangdayu.things.api.message.ThingsPublisher");
        }
    };

    private final ThingsEngineProperties thingsEngineProperties;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ApplicationContext applicationContext = (ApplicationContext) BizRuntimeContextRegistry.getMasterBizRuntimeContext().getApplicationContext().get();
        Set<String> sharedBeanClasses = thingsEngineProperties.getSofaBiz().getSharedBeanClasses();
        if (CollUtil.isNotEmpty(sharedBeanClasses)) {
            THINGS_SERVICES.addAll(sharedBeanClasses);
        }
        registerBeanForClasses(THINGS_SERVICES, beanFactory, applicationContext);
        registerBeanForNames(thingsEngineProperties, beanFactory, applicationContext);
    }


    private void registerBeanForNames(ThingsEngineProperties thingsEngineProperties, ConfigurableListableBeanFactory beanFactory, ApplicationContext applicationContext) {
        ThingsEngineProperties.ThingsSofaBizProperties sofaBiz = thingsEngineProperties.getSofaBiz();
        if (CollUtil.isNotEmpty(sofaBiz.getSharedBeanNames())) {
            for (String sharedBeansName : sofaBiz.getSharedBeanNames()) {
                Object bean = applicationContext.getBean(sharedBeansName);
                beanFactory.registerResolvableDependency(bean.getClass(), bean);
            }
        }
    }

    private void registerBeanForClasses(Set<String> sharedBeanClasses, ConfigurableListableBeanFactory beanFactory, ApplicationContext applicationContext) {
        if (CollUtil.isNotEmpty(sharedBeanClasses)) {
            for (String sharedBeansClass : sharedBeanClasses) {
                Class<?> aClass = findClass(sharedBeansClass, applicationContext);
                beanFactory.registerResolvableDependency(aClass, getBean(aClass, applicationContext));
            }
        }
    }

    private Object getBean(Class<?> requiredType, ApplicationContext applicationContext) {
        try {
            return applicationContext.getBean(requiredType);
        } catch (Exception e1) {
            try {
                return applicationContext.getBean(applicationContext.getClassLoader().loadClass(requiredType.getName()));
            } catch (Exception e2) {
                throw e1;
            }
        }
    }

    @SneakyThrows
    private Class<?> findClass(String className, ApplicationContext applicationContext) {
        try {
            return ThingsSofaBizAutoConfiguration.class.getClassLoader().loadClass(className);
        } catch (Exception e1) {
            try {
                return applicationContext.getClassLoader().loadClass(className);
            } catch (Exception e2) {
                throw e1;
            }
        }
    }
}
