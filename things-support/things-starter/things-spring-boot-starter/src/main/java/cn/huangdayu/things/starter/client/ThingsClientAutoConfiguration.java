package cn.huangdayu.things.starter.client;

import cn.huangdayu.things.common.annotation.ThingsBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author huangdayu
 */
@Conditional(EnableThingsClientCondition.class)
@Configuration
@EnableConfigurationProperties
@ConfigurationPropertiesScan(value = {"cn.huangdayu.things.client", "cn.huangdayu.things.starter.client"})
@ComponentScan(value = {"cn.huangdayu.things.client", "cn.huangdayu.things.starter.client"}, includeFilters = @ComponentScan.Filter(ThingsBean.class))
public class ThingsClientAutoConfiguration {

    @Bean
    public ThingsClientsRegistrar thingsClientsRegistrar() {
        return new ThingsClientsRegistrar();
    }

}
