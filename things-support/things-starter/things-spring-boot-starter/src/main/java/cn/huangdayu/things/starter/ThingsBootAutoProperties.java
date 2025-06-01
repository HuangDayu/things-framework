package cn.huangdayu.things.starter;

import cn.huangdayu.things.common.properties.ThingsSystemProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author huangdayu
 */
@ConfigurationProperties(prefix = "things")
public class ThingsBootAutoProperties extends ThingsSystemProperties {
}
