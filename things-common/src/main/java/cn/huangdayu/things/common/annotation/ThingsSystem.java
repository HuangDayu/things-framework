package cn.huangdayu.things.common.annotation;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ThingsBean
public @interface ThingsSystem {


    /**
     * 标识
     *
     * @return
     */
    String code() default "";

    /**
     * 务名称
     *
     * @return
     */
    String name() default "";


    /**
     * 注释说明
     *
     * @return
     */
    String desc() default "";

}
