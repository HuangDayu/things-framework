package cn.huangdayu.things.starter.engine;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static cn.huangdayu.things.starter.utils.ThingsStarterUtils.isAnnotationPresent;

public class EnableThingsEngineCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return isAnnotationPresent(context, EnableThingsEngine.class);
    }


}
