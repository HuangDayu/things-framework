package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.container.ThingsContainer;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public class ThingsFunctionContainer implements ThingsContainer {

    private final String name;
    private final Object bean;

    @Override
    public String name() {
        return name;
    }

    @Override
    public Map<String, Object> getBeans(Class<? extends Annotation> annotationType) {
        return Map.of(name, bean);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        if (requiredType.isInstance(bean)) {
            return (T) bean;
        }
        return null;
    }
}
