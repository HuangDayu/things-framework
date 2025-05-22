package cn.huangdayu.things.starter;

import cn.huangdayu.things.common.properties.ThingsInstanceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author huangdayu
 */
@ConfigurationProperties(prefix = "things")
public class ThingsBootAutoProperties extends ThingsInstanceProperties {
}
