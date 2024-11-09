package cn.huangdayu.things.client;

import cn.huangdayu.things.client.proxy.ThingsClientsRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author huangdayu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ThingsClientsRegistrar.class})
public @interface EnableThingsClients {

    /**
     * 包扫描路径
     *
     * @return
     */
    String[] basePackages() default {};

    /**
     * 包扫描路径
     *
     * @return
     */
    Class<?>[] basePackageClasses() default {};

}
