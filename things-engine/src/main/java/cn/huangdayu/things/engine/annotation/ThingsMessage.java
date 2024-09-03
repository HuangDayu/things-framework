package cn.huangdayu.things.engine.annotation;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThingsMessage {

}
