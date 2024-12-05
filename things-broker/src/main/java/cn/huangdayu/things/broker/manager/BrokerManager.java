package cn.huangdayu.things.broker.manager;

import cn.huangdayu.things.api.instances.ThingsInstancesTypeFinder;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsInstanceType;

/**
 * @author huangdayu
 */
@ThingsBean
public class BrokerManager implements ThingsInstancesTypeFinder {
    @Override
    public ThingsInstanceType type() {
        return ThingsInstanceType.BROKER;
    }
}
