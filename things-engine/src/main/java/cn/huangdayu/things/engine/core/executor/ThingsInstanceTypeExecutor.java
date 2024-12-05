package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.instances.ThingsInstancesTypeFinder;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsInstanceType;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsInstanceTypeExecutor implements ThingsInstancesTypeFinder {
    @Override
    public ThingsInstanceType type() {
        return ThingsInstanceType.ADAPTER;
    }
}
