package cn.huangdayu.things.api.infrastructure;

import cn.huangdayu.things.common.properties.ThingsSystemProperties;

/**
 * @author huangdayu
 */
public interface ThingsConfigurator {

    ThingsSystemProperties getProperties();

    void updateProperties(ThingsSystemProperties properties);

}
