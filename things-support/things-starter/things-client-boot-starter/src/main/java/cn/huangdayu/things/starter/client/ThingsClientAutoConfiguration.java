package cn.huangdayu.things.starter.client;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.api.message.ThingsSender;
import cn.huangdayu.things.client.proxy.ThingsClientsPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author huangdayu
 */
@Configuration
public class ThingsClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public ThingsPublisher thingsPublisher(ThingsEndpointFactory thingsEndpointFactory) {
        return new ThingsClientsPublisher(thingsEndpointFactory);
    }

    @ConditionalOnMissingBean
    @Bean
    public ThingsSender thingsSender(ThingsEndpointFactory thingsEndpointFactory) {
        return new ThingsClientsPublisher(thingsEndpointFactory);
    }

}
