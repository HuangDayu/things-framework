package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.filters.ThingsFilter;
import cn.huangdayu.things.common.annotation.ThingsFiltering;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.event.ThingsEventObserver;

/**
 * @author huangdayu
 */
@ThingsFiltering(identifier = ThingsConstants.Events.DEVICE_OFFLINE)
public class DeviceOfflineThingsFilter extends DeviceStatusThingsFilter implements ThingsFilter {


    public DeviceOfflineThingsFilter(ThingsEventObserver thingsEventObserver) {
        super(thingsEventObserver);
    }

    @Override
    boolean status() {
        return false;
    }
}
