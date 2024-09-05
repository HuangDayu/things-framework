package cn.huangdayu.things.engine.annotation;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ThingsBean
public @interface ThingsIntercepting {

    /**
     * 是否启用
     *
     * @return
     */
    boolean enabled() default true;


    /**
     * 优先级
     *
     * @return
     */
    int order() default 0;

    /**
     * 是否是拦截请求，否则拦截响应
     *
     * @return
     */
    boolean request() default true;

    /**
     * 产品标识
     *
     * @return
     */
    String productCode() default "";

    /**
     * 唯一标识符
     *
     * @return
     */
    String identifier() default "";


    /**
     * 事件名称
     *
     * @return
     */
    String name() default "";

    /**
     * 事件的注释说明
     *
     * @return
     */
    String desc() default "";

}
