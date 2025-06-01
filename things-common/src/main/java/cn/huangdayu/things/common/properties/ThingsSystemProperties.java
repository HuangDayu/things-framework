package cn.huangdayu.things.common.properties;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

/**
 * @author huangdayu
 */
@Data
public class ThingsSystemProperties {

    public ThingsSystemProperties() {
        this.code = UUID.randomUUID().toString().replace("-", "");
        this.configPath = "temp/things/config/" + code + "/config.json";
    }

    private String code;

    private String configPath;

    /**
     * 软总线服务列表
     */
    private Set<ThingsSofaBusProperties> sofaBus;

}
