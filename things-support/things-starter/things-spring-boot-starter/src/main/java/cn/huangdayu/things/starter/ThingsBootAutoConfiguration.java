package cn.huangdayu.things.starter;

import cn.huangdayu.things.api.container.ThingsRegister;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.factory.ThreadPoolFactory;
import cn.huangdayu.things.starter.client.EnableThingsClientCondition;
import cn.huangdayu.things.starter.client.ThingsClientsRegistrar;
import cn.huangdayu.things.starter.engine.EnableThingsEngineCondition;
import cn.huangdayu.things.starter.engine.ThingsContainerRegister;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

    @Conditional(EnableThingsEngineCondition.class)
    @Bean
    public ThingsContainerRegister thingsContainerRegister(ThingsRegister thingsRegister) {
        return new ThingsContainerRegister(thingsRegister);
    }

    @Conditional(EnableThingsClientCondition.class)
    @Bean
    public ThingsClientsRegistrar thingsClientsRegistrar() {
        return new ThingsClientsRegistrar();
    }

    public static boolean isAnnotationPresent(ConditionContext context, Class<? extends Annotation> annotationClass) {
        Map<String, Object> beans = Objects.requireNonNull(context.getBeanFactory()).getBeansWithAnnotation(SpringBootApplication.class);
        return CollUtil.isNotEmpty(beans) && beans.values().stream()
                .map(Object::getClass)
                .anyMatch(clazz -> clazz.isAnnotationPresent(annotationClass));
    }
}
