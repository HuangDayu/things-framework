package cn.huangdayu.things.common.annotation;

import cn.huangdayu.things.common.constants.ThingsConstants;

import java.lang.annotation.*;

import static cn.huangdayu.things.common.annotation.ThingsParam.BodyType.PAYLOAD;

/**
 * @author huangdayu
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThingsParam {

    /**
     * 哪一个消息体中的字段，默认是payload
     *
     * @return
     */
    BodyType bodyType() default PAYLOAD;

    /**
     * 入参名称。
     *
     * @return
     * @see ThingsConstants.Properties
     */
    String identifier() default "";


    /**
     * 服务名称
     *
     * @return
     */
    String name() default "";


    /**
     * 服务的注释说明
     *
     * @return
     */
    String desc() default "";

    /**
     * 属性类型，基本数据类型有: int, text, bool, date, enum；复杂数据类型有：struct, array
     *
     * @return
     */
    String type() default "";


    /**
     * 读写模式： rw，属性读写类型：读（r）写（w），只读为r，读写为rw
     *
     * @return
     */
    String accessMode() default "rw";

    /**
     * 是否必须，默认为true。
     */
    boolean required() default true;

    /**
     * 参数最小值（int、float、double类型特有）
     *
     * @return
     */
    String min() default "";


    /**
     * 参数最大值（int、float、double类型特有）
     *
     * @return
     */
    String max() default "";


    /**
     * 属性单位（int、float、double类型特有，非必填）
     *
     * @return
     */
    String unit() default "";


    /**
     * 单位名称（int、float、double类型特有，非必填）
     *
     * @return
     */
    String unitName() default "";


    /**
     * 数组元素的个数，最大512（array类型特有）
     *
     * @return
     */
    String size() default "";


    /**
     * 步长（text、enum类型无此参数）
     *
     * @return
     */
    String step() default "";


    public static enum BodyType {
        METADATA,
        PAYLOAD,
        HEADER
    }

}
