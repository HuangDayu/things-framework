package cn.huangdayu.things.sofabiz.condition;

import cn.huangdayu.things.sofabiz.EnableThingsSofaBiz;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

public class ThingsSofaBizSingletonCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return Objects.requireNonNull(context.getBeanFactory()).getBeansWithAnnotation(SpringBootApplication.class).values().stream().map(Object::getClass)
                .anyMatch(clazz -> clazz.isAnnotationPresent(EnableThingsSofaBiz.class) && clazz.getAnnotation(EnableThingsSofaBiz.class).singletonMode());
    }
}
