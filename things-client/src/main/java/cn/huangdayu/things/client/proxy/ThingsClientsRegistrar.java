package cn.huangdayu.things.client.proxy;

import cn.huangdayu.things.client.EnableThingsClients;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.huangdayu.things.common.annotation.ThingsClient;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author huangdayu
 */
public class ThingsClientsRegistrar implements ImportBeanDefinitionRegistrar {


    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        Map<String, Object> attrs = annotationMetadata.getAnnotationAttributes(EnableThingsClients.class.getName());
        if (CollUtil.isNotEmpty(attrs)) {
            Set<String> basePackages = getBasePackages(annotationMetadata);
            Set<Class<?>> annotatedClasses = getAnnotatedClasses(basePackages);
            annotatedClasses.parallelStream().forEach(beanClass -> registerBeanDefinition(beanDefinitionRegistry, beanClass));
        }
    }

    private void registerBeanDefinition(BeanDefinitionRegistry beanDefinitionRegistry, Class<?> beanClass) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
        MutablePropertyValues propertyValues = definition.getPropertyValues();
        propertyValues.add("interfaceClass", beanClass);
        definition.setBeanClass(ThingsClientsFactoryBean.class);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinitionRegistry.registerBeanDefinition(beanClass.getSimpleName(), definition);
    }

    public Set<Class<?>> getAnnotatedClasses(Set<String> packageNames) {
        Set<Class<?>> classSet = new ConcurrentHashSet<>();
        packageNames.forEach(packageName -> classSet.addAll(getAnnotatedClasses(packageName)));
        return classSet;
    }

    public Set<Class<?>> getAnnotatedClasses(String packageName) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))
                .filterInputsBy(new FilterBuilder().includePackage(packageName))
                .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()));
        return reflections.getTypesAnnotatedWith(ThingsClient.class);
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableThingsClients.class.getCanonicalName());

        Set<String> basePackages = new ConcurrentHashSet<>();
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }
}
