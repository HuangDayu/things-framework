package cn.huangdayu.things.engine.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 数据映射注解
 *
 * @author huangdayu
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ThingsBean
public @interface ThingsMapping {

    /**
     * 是否启用
     *
     * @return
     */
    boolean enabled() default true;


    @AliasFor(annotation = ThingsBean.class, attribute = "value")
    String value() default "";


    /**
     * 产品标识
     *
     * @return
     */
    String productCode();

}
