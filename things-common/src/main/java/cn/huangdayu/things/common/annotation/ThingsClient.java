package cn.huangdayu.things.common.annotation;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ThingsClient {

    /**
     * 版本
     *
     * @return
     */
    String schema() default "1.0";

    /**
     * 目标服务地址
     *
     * @return
     */
    String targetCode() default "";

    /**
     * 产品标识
     *
     * @return
     */
    String productCode();

    /**
     * 是否启用
     *
     * @return
     */
    boolean enabled() default true;


    /**
     * 重试次数
     *
     * @return
     */
    int retry() default 0;

    /**
     * 名称
     *
     * @return
     */
    String name() default "";

    /**
     * 描述
     *
     * @return
     */
    String desc() default "";

}
