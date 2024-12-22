package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.infrastructure.ThingsConfigService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsInstanceProperties;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsConfigExecutor implements ThingsConfigService {

    private ThingsInstanceProperties thingsInstanceProperties;

    public ThingsConfigExecutor(ThingsInstanceProperties thingsInstanceProperties) {
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
