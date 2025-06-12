package cn.huangdayu.things.sofabiz;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableThingsSofaBiz {

    /**
     * sofabiz是单例模式，每个biz都直接注册到ark的容器中，默认是多例模式
     *
     * @return
     */
    boolean singletonMode() default false;


}
