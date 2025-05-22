package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.infrastructure.ThingsPropertiesService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsInstanceProperties;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsPropertiesManager implements ThingsPropertiesService {

    private ThingsInstanceProperties thingsInstanceProperties;

    public ThingsPropertiesManager(ThingsInstanceProperties thingsInstanceProperties) {
        this.thingsInstanceProperties = thingsInstanceProperties;
    }

    @Override
    public ThingsInstanceProperties getProperties() {
        return thingsInstanceProperties;
    }

    @Override
    public void updateProperties(ThingsInstanceProperties properties) {
        this.thingsInstanceProperties = properties;
    }
}
