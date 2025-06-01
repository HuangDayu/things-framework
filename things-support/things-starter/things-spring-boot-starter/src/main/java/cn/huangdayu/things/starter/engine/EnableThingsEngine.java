package cn.huangdayu.things.starter.engine;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableThingsEngine {
}
