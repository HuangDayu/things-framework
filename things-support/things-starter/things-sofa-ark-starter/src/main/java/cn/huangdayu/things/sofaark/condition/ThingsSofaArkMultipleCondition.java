package cn.huangdayu.things.sofaark.condition;

import cn.huangdayu.things.sofaark.EnableThingsSofaArk;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

public class ThingsSofaArkMultipleCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return Objects.requireNonNull(context.getBeanFactory()).getBeansWithAnnotation(SpringBootApplication.class).values().stream().map(Object::getClass)
                .noneMatch(clazz -> clazz.isAnnotationPresent(EnableThingsSofaArk.class) && clazz.getAnnotation(EnableThingsSofaArk.class).singletonMode());
    }
}
