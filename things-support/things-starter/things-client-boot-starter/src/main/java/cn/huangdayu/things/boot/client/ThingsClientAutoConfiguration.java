package cn.huangdayu.things.boot.client;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstancesProvider;
import cn.huangdayu.things.api.publisher.ThingsPublisher;
import cn.huangdayu.things.client.proxy.ThingsClientPublisher;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @author huangdayu
 */
@Configuration
public class ThingsClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public ThingsPublisher thingsPublisher(ThingsEventObserver thingsEventObserver, ThingsEndpointFactory thingsEndpointFactory) {
        return new ThingsClientPublisher(thingsEventObserver, thingsEndpointFactory);
    }

    @ConditionalOnMissingBean
    @Bean
    public ThingsInstancesProvider ThingsInstancesProvider() {
        return new ThingsInstancesProvider() {
            @Override
            public Set<String> getProvides() {
                return Set.of();
            }

            @Override
            public Set<String> getConsumes() {
                return Set.of();
            }

            @Override
            public Set<String> getSubscribes() {
                return Set.of();
            }
        };
    }

}
