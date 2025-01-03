package cn.huangdayu.things.api.infrastructure;

import cn.huangdayu.things.common.properties.ThingsInstanceProperties;

/**
 * @author huangdayu
 */
public interface ThingsConfigService {

    ThingsInstanceProperties getProperties();

    void updateProperties(ThingsInstanceProperties properties);

}
