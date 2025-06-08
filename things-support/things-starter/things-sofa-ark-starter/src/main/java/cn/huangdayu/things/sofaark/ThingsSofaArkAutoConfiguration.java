package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.common.annotation.ThingsBean;
import com.alipay.sofa.koupleless.plugin.manager.handler.BizUninstallEventHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author huangdayu
 */
@Configuration
@ComponentScan(value = "cn.huangdayu.things.sofaark", includeFilters = @ComponentScan.Filter(ThingsBean.class))
public class ThingsSofaArkAutoConfiguration {

}
