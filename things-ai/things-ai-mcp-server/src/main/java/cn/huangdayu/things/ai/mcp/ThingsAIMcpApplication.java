package cn.huangdayu.things.ai.mcp;

import cn.huangdayu.things.ai.mcp.properties.ThingsAiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author huangdayu
 */
@EnableConfigurationProperties(value = ThingsAiProperties.class)
@SpringBootApplication
public class ThingsAIMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThingsAIMcpApplication.class, args);
    }

}
