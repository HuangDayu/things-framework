package cn.huangdayu.things.starter.engine;

import cn.huangdayu.things.api.container.ThingsRegister;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.factory.ThreadPoolFactory;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

/**
 * @author huangdayu
 */
@Slf4j
@Conditional(EnableThingsEngineCondition.class)
@Configuration
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties
@ConfigurationPropertiesScan(value = {"cn.huangdayu.things.engine", "cn.huangdayu.things.starter.engine"})
@ComponentScan(value = {"cn.huangdayu.things.engine", "cn.huangdayu.things.starter.engine"}, includeFilters = @ComponentScan.Filter(ThingsBean.class))
public class ThingsEngineAutoConfiguration {

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

    @Bean
    public ThingsContainerRegister thingsContainerRegister(ThingsRegister thingsRegister) {
        return new ThingsContainerRegister(thingsRegister);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "things")
    public ThingsEngineProperties thingsEngineProperties() {
        return new ThingsEngineProperties();
    }

    @Bean
    public ThingsBeanFactoryPostProcessor thingsBeanFactoryPostProcessor() {
        return new ThingsBeanFactoryPostProcessor();
    }

    @ConditionalOnMissingBean
    @Bean
    public ThingsEventObserver thingsEventObserver() {
        return new ThingsEventObserver();
    }
}
