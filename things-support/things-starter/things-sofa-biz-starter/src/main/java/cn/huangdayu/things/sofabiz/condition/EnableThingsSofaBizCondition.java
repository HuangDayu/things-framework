package cn.huangdayu.things.sofabiz.condition;

import cn.huangdayu.things.sofabiz.EnableThingsSofaBiz;
import cn.hutool.core.collection.CollUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;
import java.util.Objects;

public class EnableThingsSofaBizCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> beans = Objects.requireNonNull(context.getBeanFactory()).getBeansWithAnnotation(SpringBootApplication.class);
        return CollUtil.isNotEmpty(beans) && beans.values().stream()
                .map(Object::getClass)
                .anyMatch(clazz -> clazz.isAnnotationPresent(EnableThingsSofaBiz.class));
    }
}
