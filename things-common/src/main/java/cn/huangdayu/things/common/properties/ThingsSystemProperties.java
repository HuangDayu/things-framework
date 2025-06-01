package cn.huangdayu.things.common.properties;

import lombok.Data;

import java.util.Set;

/**
 * @author huangdayu
 */
@Data
public class ThingsSystemProperties {

    /**
     * 软总线服务列表
     */
    private Set<ThingsSofaBusProperties> sofaBus;

}
