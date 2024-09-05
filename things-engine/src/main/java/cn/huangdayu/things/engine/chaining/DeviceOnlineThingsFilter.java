package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.engine.annotation.ThingsFiltering;
import cn.huangdayu.things.engine.chaining.filters.ThingsFilter;
import cn.huangdayu.things.engine.common.ThingsConstants;
import cn.huangdayu.things.engine.core.ThingsObserverEngine;

/**
 * @author huangdayu
 */
@ThingsFiltering(identifier = ThingsConstants.Events.DEVICE_ONLINE)
public class DeviceOnlineThingsFilter extends DeviceStatusThingsFilter implements ThingsFilter {


    public DeviceOnlineThingsFilter(ThingsObserverEngine thingsObserverEngine) {
        super(thingsObserverEngine);
    }

    @Override
    boolean status() {
        return true;
    }

}
