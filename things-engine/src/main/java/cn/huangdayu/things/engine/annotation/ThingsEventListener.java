package cn.huangdayu.things.engine.annotation;

import cn.huangdayu.things.engine.common.ThingsConstants;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThingsEventListener {


    /**
     * 产品标识
     *
     * @return
     */
    String productCode();

    /**
     * 事件唯一标识符
     *
     * @return
     * @see ThingsConstants.Events
     */
    String identifier();


    /**
     * 事件类型： alarm：告警事件，info：基本事件，other：其他事件
     *
     * @return
     */
    String type() default "info";


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
