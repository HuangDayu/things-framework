package cn.huangdayu.things.common.annotation;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThingsMessage {

}
