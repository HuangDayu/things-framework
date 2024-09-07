package cn.huangdayu.things.boot;

import cn.huangdayu.things.engine.async.ThreadPoolFactory;
import cn.huangdayu.things.engine.configuration.ThingsEngineProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
public class ThingsBootAutoConfiguration {


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
    @ConfigurationProperties("things-framework.things-engine")
    public ThingsEngineProperties thingsEngineProperties() {
        return new ThingsEngineProperties();
    }

    @ConditionalOnMissingBean
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }


}
