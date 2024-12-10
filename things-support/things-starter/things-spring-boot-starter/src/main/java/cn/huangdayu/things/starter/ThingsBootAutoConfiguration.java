package cn.huangdayu.things.starter;

import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.api.register.ThingsRegister;
import cn.huangdayu.things.common.factory.ThreadPoolFactory;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author huangdayu
 */
@Slf4j
@Configuration
@EnableCaching
@EnableScheduling
@ComponentScan(value = "cn.huangdayu.things")
@ConfigurationPropertiesScan("cn.huangdayu.things")
public class ThingsBootAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean("thingsTaskScheduler")
    public TaskScheduler thingsTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadFactory(ThreadPoolFactory.tryGetVirtualThreadFactory());
        scheduler.initialize();
        return scheduler;
    }


    @ConditionalOnMissingBean
    @Bean
    @ConfigurationProperties("things")
    public ThingsFrameworkProperties thingsEngineProperties() {
        return new ThingsFrameworkProperties();
    }

    @ConditionalOnMissingBean
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @ConditionalOnBean(ThingsRegister.class)
    @ConditionalOnMissingBean
    @Bean
    public ThingsContainerRegister thingsContainerRegister(ThingsRegister thingsRegister) {
        return new ThingsContainerRegister(thingsRegister);
    }

    @ConditionalOnBean(ThingsEndpoint.class)
    @ConditionalOnMissingBean
    @Bean
    public ThingsEndpointController thingsEndpointController(ThingsEndpoint thingsEndpoint) {
        return new ThingsEndpointController(thingsEndpoint);
    }
}
