package cn.huangdayu.things.starter.client;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static cn.huangdayu.things.starter.utils.ThingsStarterUtils.isAnnotationPresent;

public class EnableThingsClientCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return isAnnotationPresent(context, EnableThingsClients.class);
    }
}
