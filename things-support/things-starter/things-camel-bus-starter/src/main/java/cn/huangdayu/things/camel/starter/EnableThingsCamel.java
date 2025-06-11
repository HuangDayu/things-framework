package cn.huangdayu.things.camel.starter;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableThingsCamel {
}
