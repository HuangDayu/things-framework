package cn.huangdayu.things.discovery;

import cn.huangdayu.things.api.instances.ThingsInstancesProvider;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;

import java.util.Set;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsInstancesProvideDiscoverer implements ThingsInstancesProvider {

    @Override
    public Set<ThingsInstance> getProvides(JsonThingsMessage jtm) {
        return Set.of();
    }
}
