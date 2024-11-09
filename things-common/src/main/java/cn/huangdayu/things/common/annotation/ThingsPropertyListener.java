package cn.huangdayu.things.common.annotation;

import cn.huangdayu.things.common.constants.ThingsConstants;

import java.lang.annotation.*;

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
    String identifier() default ThingsConstants.THINGS_WILDCARD;


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
