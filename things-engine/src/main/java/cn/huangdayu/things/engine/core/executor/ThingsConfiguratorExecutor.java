package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.infrastructure.ThingsConfigurator;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsPropertiesUpdatedEvent;
import cn.huangdayu.things.common.properties.ThingsSystemProperties;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsConfiguratorExecutor implements ThingsConfigurator {

    private volatile ThingsSystemProperties thingsSystemProperties;
    private final ThingsEventObserver thingsEventObserver;

    public ThingsConfiguratorExecutor(ThingsSystemProperties thingsSystemProperties, ThingsEventObserver thingsEventObserver) {
        this.thingsSystemProperties = thingsSystemProperties;
        this.thingsEventObserver = thingsEventObserver;
    }

    @Override
    public ThingsSystemProperties getProperties() {
        return thingsSystemProperties;
    }

    @Override
    public void updateProperties(ThingsSystemProperties properties) {
        thingsEventObserver.notifyObservers(new ThingsPropertiesUpdatedEvent(this, this.thingsSystemProperties, properties));
        this.thingsSystemProperties = properties;
    }
}
