package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.engine.container.ThingsContainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsFunction {

    private ThingsContainer thingsContainer;
    private Annotation beanAnnotation;
    private Object bean;
    private Method method;
    private boolean async;
    private Annotation methodAnnotation;
    private ThingsParameter[] thingsParameters;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThingsFunction that = (ThingsFunction) o;
        return Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(method);
    }
}
