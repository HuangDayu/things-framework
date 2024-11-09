package cn.huangdayu.things.common.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * TODO 应当是一个设备一个配置，或者一些设备公用产品配置
 *
 * @author huangdayu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ThingsBean
public @interface ThingsProperty {

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

    /**
     * 此配置是否产品共用
     *
     * @return
     */
    boolean productPublic() default false;

    /**
     * 名称
     *
     * @return
     */
    String name() default "";

    /**
     * 属性取值说明 1：打开 0：关闭
     *
     * @return
     */
    String desc() default "";

}
