package cn.huangdayu.things.common.annotation;

import cn.huangdayu.things.common.enums.ThingsMethodType;
import cn.huangdayu.things.common.enums.ThingsChainingType;

import java.lang.annotation.*;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.enums.ThingsMethodType.ALL_METHOD;

/**
 * @author huangdayu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ThingsBean
public @interface ThingsInterceptor {

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
     * 消息流类型，输入/输出
     *
     * @return
     */
    ThingsChainingType chainingType();

    /**
     * 产品标识
     *
     * @return
     */
    String productCode() default THINGS_WILDCARD;


    /**
     * 方法类型
     *
     * @return
     */
    ThingsMethodType method() default ALL_METHOD;

    /**
     * 唯一标识符（method）
     *
     * @return
     */
    String identifier() default THINGS_WILDCARD;


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
