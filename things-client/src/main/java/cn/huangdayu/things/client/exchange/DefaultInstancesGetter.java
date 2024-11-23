package cn.huangdayu.things.client.exchange;

import cn.huangdayu.things.api.instances.ThingsInstancesGetter;
import cn.huangdayu.things.client.ThingsClientContext;
import cn.huangdayu.things.client.ThingsClientProperties;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */

@RequiredArgsConstructor
@ThingsBean
public class DefaultInstancesGetter implements ThingsInstancesGetter {

    private final ThingsClientProperties thingsClientProperties;

    @Override
    public String getInstanceId() {
        if (StrUtil.isNotBlank(thingsClientProperties.getInstanceId())) {
            return thingsClientProperties.getInstanceId();
        }
        return ThingsClientContext.getContext().getApplicationName();
    }
}
