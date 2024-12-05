package cn.huangdayu.things.gateway;

import cn.huangdayu.things.api.instances.ThingsInstancesTypeFinder;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsInstanceType;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsGatewayTypeExecutor implements ThingsInstancesTypeFinder {
    @Override
    public ThingsInstanceType type() {
        return ThingsInstanceType.GATEWAY;
    }
}
