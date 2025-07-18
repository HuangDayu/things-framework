package cn.huangdayu.things.common.annotation;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThingsMethod {
    /**
     * 物模型标识/产品标识
     */
    boolean productCode() default false;

    /**
     * 物标识/设备标识
     */
    boolean deviceCode() default false;

    /**
     * 类型： service/property/event
     */
    boolean type() default false;

    /**
     * 标识
     */
    boolean identifier() default false;

    /**
     * 动作：get/set/post/request/response
     */
    boolean action() default false;

}
