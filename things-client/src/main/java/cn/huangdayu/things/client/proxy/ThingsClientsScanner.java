package cn.huangdayu.things.client.proxy;

import cn.huangdayu.things.common.annotation.ThingsClient;
import cn.hutool.core.collection.ConcurrentHashSet;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author huangdayu
 */
public class ThingsClientsScanner {

    public static Set<Class<?>> getAnnotatedClasses(String basePackage, Map<String, Object> attributes) {
        return getAnnotatedClasses(getBasePackages(basePackage, attributes));
    }

    private static Set<Class<?>> getAnnotatedClasses(Set<String> packageNames) {
        Set<Class<?>> classSet = new ConcurrentHashSet<>();
        packageNames.forEach(packageName -> classSet.addAll(getAnnotatedClasses(packageName)));
        return classSet;
    }

    private static Set<Class<?>> getAnnotatedClasses(String packageName) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))
                .filterInputsBy(new FilterBuilder().includePackage(packageName))
                .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()));
        return reflections.getTypesAnnotatedWith(ThingsClient.class);
    }

    private static Set<String> getBasePackages(String basePackage, Map<String, Object> attributes) {
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
            basePackages.add(basePackage);
        }
        return basePackages;
    }

}
