package cn.huangdayu.things.starter;

import cn.huangdayu.things.api.container.ThingsRegister;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.factory.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Set;

/**
 * @author huangdayu
 */
@Slf4j
@Configuration
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties(ThingsBootAutoProperties.class)
@ComponentScan(value = "cn.huangdayu.things", includeFilters = @ComponentScan.Filter(ThingsBean.class))
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
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @ConditionalOnBean(ThingsRegister.class)
    @ConditionalOnMissingBean
    @Bean
    public ThingsContainerRegister thingsContainerRegister(ThingsRegister thingsRegister) {
        return new ThingsContainerRegister(thingsRegister);
    }


}
