package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsFilter;
import cn.huangdayu.things.common.annotation.ThingsFiltering;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.event.ThingsEventObserver;

/**
 * @author huangdayu
 */
@ThingsFiltering(identifier = ThingsConstants.Events.DEVICE_ONLINE)
public class DeviceOnlineThingsFilter extends DeviceStatusThingsFilter implements ThingsFilter {


    public DeviceOnlineThingsFilter(ThingsEventObserver thingsEventObserver) {
        super(thingsEventObserver);
    }

    @Override
    boolean status() {
        return true;
    }

}
