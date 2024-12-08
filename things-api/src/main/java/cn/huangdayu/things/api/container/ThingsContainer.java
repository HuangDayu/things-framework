package cn.huangdayu.things.api.container;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author huangdayu
 */
public interface ThingsContainer {

    String name();

    Map<String, Object> getBeans(Class<? extends Annotation> annotationType);

    <T> T getBean(Class<T> requiredType);
}
