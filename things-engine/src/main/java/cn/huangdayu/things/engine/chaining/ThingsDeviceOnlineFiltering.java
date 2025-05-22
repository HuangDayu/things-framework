package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsFiltering;
import cn.huangdayu.things.common.annotation.ThingsFilter;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.observer.ThingsEventObserver;

import static cn.huangdayu.things.common.enums.ThingsChainingType.INPUTTING;
import static cn.huangdayu.things.common.enums.ThingsChainingType.OUTPUTTING;

/**
 * @author huangdayu
 */
@ThingsFilter(identifier = ThingsConstants.Events.DEVICE_ONLINE, chainingType = OUTPUTTING)
public class ThingsDeviceOnlineFiltering extends ThingsDeviceStatusFiltering implements ThingsFiltering {


    public ThingsDeviceOnlineFiltering(ThingsEventObserver thingsEventObserver) {
        super(thingsEventObserver);
    }

    @Override
    boolean status() {
        return true;
    }

}
