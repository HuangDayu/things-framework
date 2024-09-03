package cn.huangdayu.things.engine.annotation;

import java.lang.annotation.*;

import static cn.huangdayu.things.engine.common.ThingsConstants.THINGS_WILDCARD;

/**
 * @author huangdayu
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThingsPropertyListener {

    /**
     * 产品标识
     *
     * @return
     */
    String productCode() default "";

    /**
     * 属性字段标识
     *
     * @return
     */
    String identifier() default THINGS_WILDCARD;


    /**
     * 配置名称
     *
     * @return
     */
    String name() default "";

    /**
     * 配置的注释说明
     *
     * @return
     */
    String desc() default "";
}
