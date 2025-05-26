package cn.huangdayu.things.rest.type;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsInstanceType;
import cn.huangdayu.things.rest.instances.ThingsInstancesTypeFinder;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsBrokerTypeExecutor implements ThingsInstancesTypeFinder {
    @Override
    public ThingsInstanceType type() {
        return ThingsInstanceType.BROKER;
    }
}
