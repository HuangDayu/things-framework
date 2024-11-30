package cn.huangdayu.things.api.container;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author huangdayu
 */
public abstract class ThingsContainer {

    public abstract String name();

    public abstract Map<String, Object> getBeans(Class<? extends Annotation> annotationType);

    public abstract <T> T getBean(Class<T> requiredType);
}
