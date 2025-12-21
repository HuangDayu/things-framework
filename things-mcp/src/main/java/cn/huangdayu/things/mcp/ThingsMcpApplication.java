package cn.huangdayu.things.mcp;

import cn.huangdayu.things.mcp.properties.ThingsMcpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author huangdayu
 */
@EnableConfigurationProperties(value = ThingsMcpProperties.class)
@SpringBootApplication(scanBasePackages = "cn.huangdayu.things")
public class ThingsMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThingsMcpApplication.class, args);
    }

}
