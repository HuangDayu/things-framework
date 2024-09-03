package cn.huangdayu.things.boot.configuration;

import cn.huangdayu.things.engine.configuration.ThingsEngineAutoConfiguration;
import cn.huangdayu.things.engine.configuration.ThingsEngineProperties;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author huangdayu
 */
@EnableCaching
@ComponentScan("cn.huangdayu.things.boot")
@ImportAutoConfiguration(ThingsEngineAutoConfiguration.class)
public class ThingsBootAutoConfiguration {

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
