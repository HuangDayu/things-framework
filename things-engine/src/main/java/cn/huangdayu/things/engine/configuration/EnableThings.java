package cn.huangdayu.things.engine.configuration;

import cn.huangdayu.things.engine.proxy.ThingsClientsRegistrar;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
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
@ImportAutoConfiguration(ThingsEngineAutoConfiguration.class)
public @interface EnableThings {

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
