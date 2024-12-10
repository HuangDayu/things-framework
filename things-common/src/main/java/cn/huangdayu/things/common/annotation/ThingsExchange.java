package cn.huangdayu.things.common.annotation;

import cn.huangdayu.things.common.constants.ThingsConstants;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThingsExchange {


    /**
     * 标识
     *
     * @return
     * @see ThingsConstants.Services
     */
    String identifier() default "";

}
