package cn.huangdayu.things.starter.utils;

import cn.hutool.core.collection.CollUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ConditionContext;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

/**
 * @author huangdayu
 */
public class ThingsStarterUtils {


    public static boolean isAnnotationPresent(ConditionContext context, Class<? extends Annotation> annotationClass) {
        Map<String, Object> beans = Objects.requireNonNull(context.getBeanFactory()).getBeansWithAnnotation(SpringBootApplication.class);
        return CollUtil.isNotEmpty(beans) && beans.values().stream()
                .map(Object::getClass)
                .anyMatch(clazz -> clazz.isAnnotationPresent(annotationClass));
    }
}
