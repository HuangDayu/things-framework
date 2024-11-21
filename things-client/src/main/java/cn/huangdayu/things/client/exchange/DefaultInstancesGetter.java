package cn.huangdayu.things.client.exchange;

import cn.huangdayu.things.api.instances.ThingsInstancesGetter;
import cn.huangdayu.things.client.ThingsClientContext;
import cn.huangdayu.things.common.annotation.ThingsBean;

/**
 * @author huangdayu
 */
@ThingsBean
public class DefaultInstancesGetter implements ThingsInstancesGetter {


    @Override
    public String getInstanceId() {
        return ThingsClientContext.getContext().getApplicationName();
    }
}
