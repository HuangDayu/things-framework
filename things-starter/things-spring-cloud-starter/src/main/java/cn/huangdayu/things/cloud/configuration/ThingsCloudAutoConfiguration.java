package cn.huangdayu.things.cloud.configuration;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author huangdayu
 */
@ComponentScan("cn.huangdayu.things.cloud")
@ConfigurationPropertiesScan("cn.huangdayu.things.cloud")
public class ThingsCloudAutoConfiguration {


}
