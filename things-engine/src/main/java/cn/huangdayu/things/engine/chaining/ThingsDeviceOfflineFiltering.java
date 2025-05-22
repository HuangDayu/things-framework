package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsFiltering;
import cn.huangdayu.things.common.annotation.ThingsFilter;
import cn.huangdayu.things.common.observer.ThingsEventObserver;

import static cn.huangdayu.things.common.constants.ThingsConstants.Events.DEVICE_OFFLINE;
import static cn.huangdayu.things.common.enums.ThingsChainingType.INPUTTING;
import static cn.huangdayu.things.common.enums.ThingsChainingType.OUTPUTTING;

/**
 * @author huangdayu
 */
@ThingsFilter(identifier = DEVICE_OFFLINE, chainingType = OUTPUTTING)
public class ThingsDeviceOfflineFiltering extends ThingsDeviceStatusFiltering implements ThingsFiltering {


    public ThingsDeviceOfflineFiltering(ThingsEventObserver thingsEventObserver) {
        super(thingsEventObserver);
    }

    @Override
    boolean status() {
        return false;
    }
}
