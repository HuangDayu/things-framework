package cn.huangdayu.things.cloud.configuration;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author huangdayu
 */
@EnableScheduling
@ComponentScan("cn.huangdayu.things.cloud")
@ConfigurationPropertiesScan("cn.huangdayu.things.cloud")
public class ThingsCloudAutoConfiguration {


}
