package cn.huangdayu.things.sofabiz;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author huangdayu
 */
@Configuration
@ComponentScan(value = "cn.huangdayu.things.sofabiz", includeFilters = @ComponentScan.Filter(ThingsBean.class))
public class ThingsSofaBizAutoConfiguration {


    @ConditionalOnMissingBean(name = "thingsSofaArkAutoConfiguration")
    @Bean
    public ThingsSofaBizBeanPostProcessor thingsSofaBizBeanPostProcessor(Environment environment) {
        return new ThingsSofaBizBeanPostProcessor(Binder.get(environment).bind("things", ThingsEngineProperties.class).orElseGet(ThingsEngineProperties::new));
    }


}
