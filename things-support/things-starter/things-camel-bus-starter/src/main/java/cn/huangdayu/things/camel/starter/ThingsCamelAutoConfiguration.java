package cn.huangdayu.things.camel.starter;

import cn.huangdayu.things.api.container.ThingsDescriber;
import cn.huangdayu.things.api.infrastructure.ThingsConfigurator;
import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusDescriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusSubscriber;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import cn.huangdayu.things.engine.core.executor.ThingsConfiguratorExecutor;
import cn.huangdayu.things.sofabus.ThingsSofaBusSubscribing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author huangdayu
 */
@Slf4j
@Conditional(EnableThingsCamelCondition.class)
@Configuration
@ComponentScan(value = {"cn.huangdayu.things.camel", "cn.huangdayu.things.sofabus", "cn.huangdayu.things.camel.starter"}, includeFilters = @ComponentScan.Filter(ThingsBean.class))
public class ThingsCamelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ThingsSofaBusSubscriber thingsSubscriberCreator(ThingsChaining thingsChaining, ThingsDescriber thingsDescriber) {
        return new ThingsSofaBusSubscribing(thingsChaining, thingsDescriber);
    }

    @Bean
    @ConditionalOnMissingBean
    public ThingsSofaBusDescriber thingsSofaBusDescriber(ThingsDescriber thingsDescriber) {
        return thingsDescriber::getDSL;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "things")
    public ThingsEngineProperties thingsEngineProperties() {
        return new ThingsEngineProperties();
    }

    @ConditionalOnMissingBean
    @Bean
    public ThingsEventObserver thingsEventObserver() {
        return new ThingsEventObserver();
    }

    @ConditionalOnMissingBean
    @Bean
    public ThingsConfigurator thingsConfigurator(ThingsEngineProperties thingsEngineProperties, ThingsEventObserver thingsEventObserver) {
        return new ThingsConfiguratorExecutor(thingsEngineProperties, thingsEventObserver);
    }
}
