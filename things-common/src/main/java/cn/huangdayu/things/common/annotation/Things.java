package cn.huangdayu.things.common.annotation;

import cn.huangdayu.things.common.constants.ThingsConstants;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ThingsBean
public @interface Things {

    /**
     * 是否启用
     *
     * @return
     */
    boolean enabled() default true;


    @AliasFor(annotation = ThingsBean.class, attribute = "value")
    String value() default "";

    /**
     * 版本
     *
     * @return
     */
    String schema() default "1.0";

    /**
     * 产品标识
     *
     * @return
     */
    String productCode();


    /**
     * @return
     * @see ThingsConstants.Products
     */
    String productType() default "";

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
