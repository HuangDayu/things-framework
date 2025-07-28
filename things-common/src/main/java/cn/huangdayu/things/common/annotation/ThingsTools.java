package cn.huangdayu.things.common.annotation;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ThingsBean
public @interface ThingsTools {

    boolean enabled() default true;

}
