package cn.huangdayu.things.api.infrastructure;

import cn.huangdayu.things.common.properties.ThingsEngineProperties;

/**
 * @author huangdayu
 */
public interface ThingsConfigurator {

    ThingsEngineProperties getProperties();

    void updateProperties(ThingsEngineProperties properties);

}
