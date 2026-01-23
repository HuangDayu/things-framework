package cn.huangdayu.things.rules.test;

import cn.huangdayu.things.rules.ThingsRulesProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author huangdayu
 */
@SpringBootApplication(scanBasePackages = "cn.huangdayu.things")
public class ThingsRulesTestApplication {


    @Bean
    public ThingsRulesProperties thingsRulesProperties(){
        return new ThingsRulesProperties();
    }

    public static void main(String[] args) {
        SpringApplication.run(ThingsRulesTestApplication.class, args);
    }

}
