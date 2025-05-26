package cn.huangdayu.things.common.annotation;

import cn.huangdayu.things.common.constants.ThingsConstants;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThingsSystem {

    /**
     * 服务唯一标识符
     *
     * @return
     * @see ThingsConstants.Services
     */
    String identifier() default "";

    /**
     * 产品标识
     *
     * @return
     */
    String productCode() default "";

    /**
     * 服务名称
     *
     * @return
     */
    String name() default "";


    /**
     * 服务的注释说明
     *
     * @return
     */
    String desc() default "";

    /**
     * 是否支持异步调用
     *
     * @return
     */
    boolean async() default true;
}
