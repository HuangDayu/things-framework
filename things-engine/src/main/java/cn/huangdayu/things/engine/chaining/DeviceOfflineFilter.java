package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.engine.annotation.ThingsFilter;
import cn.huangdayu.things.engine.chaining.filters.Filter;
import cn.huangdayu.things.engine.common.ThingsConstants;
import cn.huangdayu.things.engine.core.ThingsObserverEngine;

/**
 * @author huangdayu
 */
@ThingsFilter(identifier = ThingsConstants.Events.DEVICE_OFFLINE)
public class DeviceOfflineFilter extends DeviceStatusFilter implements Filter {


    public DeviceOfflineFilter(ThingsObserverEngine thingsObserverEngine) {
        super(thingsObserverEngine);
    }

    @Override
    boolean status() {
        return false;
    }
}
