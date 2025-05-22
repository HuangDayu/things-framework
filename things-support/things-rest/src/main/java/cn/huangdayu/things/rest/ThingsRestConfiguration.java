package cn.huangdayu.things.rest;

import cn.huangdayu.things.rest.endpoint.ThingsEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author huangdayu
 */
public class ThingsRestConfiguration {


    @ConditionalOnBean(ThingsEndpoint.class)
    @ConditionalOnMissingBean
    @Bean
    public ThingsEndpointController thingsEndpointController(ThingsEndpoint thingsEndpoint) {
        return new ThingsEndpointController(thingsEndpoint);
    }

}
