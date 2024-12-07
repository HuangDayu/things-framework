package cn.huangdayu.things.starter.client;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.api.message.ThingsSender;
import cn.huangdayu.things.client.proxy.ThingsClientPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huangdayu
 */
@Configuration
public class ThingsClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public ThingsPublisher thingsPublisher(ThingsEndpointFactory thingsEndpointFactory) {
        return new ThingsClientPublisher(thingsEndpointFactory);
    }

    @ConditionalOnMissingBean
    @Bean
    public ThingsSender thingsSender(ThingsEndpointFactory thingsEndpointFactory) {
        return new ThingsClientPublisher(thingsEndpointFactory);
    }

}
