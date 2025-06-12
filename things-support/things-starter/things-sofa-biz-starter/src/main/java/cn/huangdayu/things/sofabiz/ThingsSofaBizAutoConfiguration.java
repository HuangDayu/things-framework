package cn.huangdayu.things.sofabiz;

import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import cn.huangdayu.things.sofabiz.condition.EnableThingsSofaBizCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import static cn.huangdayu.things.sofabiz.ThingsSofaBizUtils.getArkService;

/**
 * @author huangdayu
 */
@Conditional(EnableThingsSofaBizCondition.class)
@Configuration
@ComponentScan(value = "cn.huangdayu.things.sofabiz", includeFilters = @ComponentScan.Filter(ThingsBean.class))
public class ThingsSofaBizAutoConfiguration {


    @ConditionalOnMissingBean(name = "thingsSofaArkAutoConfiguration")
    @Bean
    public ThingsSofaBizBeanPostProcessor thingsSofaBizBeanPostProcessor(Environment environment) {
        return new ThingsSofaBizBeanPostProcessor(Binder.get(environment).bind("things", ThingsEngineProperties.class).orElseGet(ThingsEngineProperties::new));
    }

    @ConditionalOnMissingBean
    @Bean
    public ThingsPublisher thingsPublisher() {
        return getArkService(ThingsPublisher.class);
    }


}
