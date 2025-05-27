package cn.huangdayu.things.starter.client;

import cn.huangdayu.things.client.proxy.ThingsClientsProxy;
import cn.huangdayu.things.common.annotation.ThingsClient;
import cn.hutool.core.collection.CollUtil;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static cn.huangdayu.things.client.proxy.ThingsClientsScanner.getAnnotatedClasses;

/**
 * @author huangdayu
 */
@Component
public class ThingsClientsRegistrar implements ImportBeanDefinitionRegistrar, ApplicationContextAware {

    @Getter
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ThingsClientsRegistrar.context = applicationContext;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableThingsClients.class.getCanonicalName());
        if (CollUtil.isNotEmpty(attributes)) {
            Set<Class<?>> annotatedClasses = getAnnotatedClasses(ClassUtils.getPackageName(annotationMetadata.getClassName()), attributes);
            annotatedClasses.parallelStream().forEach(beanClass -> registerBeanDefinition(beanDefinitionRegistry, beanClass));
        }
    }

    private void registerBeanDefinition(BeanDefinitionRegistry beanDefinitionRegistry, Class<?> beanClass) {
        if (!beanClass.getAnnotation(ThingsClient.class).enabled()) {
            return;
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
        MutablePropertyValues propertyValues = definition.getPropertyValues();
        propertyValues.add("interfaceClass", beanClass);
        propertyValues.add("thingsClientsProxySupplier", (Supplier<ThingsClientsProxy>) () -> getContext().getBean(ThingsClientsProxy.class));
        definition.setBeanClass(ThingsClientsFactoryBean.class);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinitionRegistry.registerBeanDefinition(beanClass.getSimpleName(), definition);
    }
}
