package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author huangdayu
 */
@Conditional(EnableThingsSofaArkCondition.class)
@Configuration
@ComponentScan(value = "cn.huangdayu.things.sofaark", includeFilters = @ComponentScan.Filter(ThingsBean.class))
public class ThingsSofaArkAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public ThingsEventObserver thingsEventObserver() {
        return new ThingsEventObserver();
    }

}
