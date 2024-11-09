package cn.huangdayu.things.common.annotation;

import cn.huangdayu.things.common.constants.ThingsConstants;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * @author huangdayu
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(value = SCOPE_PROTOTYPE)
@ThingsBean
public @interface ThingsEvent {

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
     * 事件的Qos级别
     * QoS 0 - At most once（至多一次）
     * QoS 1 - At least once（至少一次）
     * QoS 2 - Exactly once（确保只有一次）
     *
     * @return
     */
    int qos() default 1;


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
