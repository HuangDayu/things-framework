package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.engine.annotation.ThingsFilter;
import cn.huangdayu.things.engine.chaining.filters.Filter;
import cn.huangdayu.things.engine.common.ThingsConstants;
import cn.huangdayu.things.engine.core.ThingsObserverEngine;

/**
 * @author huangdayu
 */
@ThingsFilter(identifier = ThingsConstants.Events.DEVICE_ONLINE)
public class DeviceOnlineFilter extends DeviceStatusFilter implements Filter {


    public DeviceOnlineFilter(ThingsObserverEngine thingsObserverEngine) {
        super(thingsObserverEngine);
    }

    @Override
    boolean status() {
        return true;
    }

}
