package cn.huangdayu.things.client.exchange;

import cn.huangdayu.things.api.instances.ThingsInstancesGetter;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsProperties;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */

@RequiredArgsConstructor
@ThingsBean
public class DefaultInstancesGetter implements ThingsInstancesGetter {

    private final ThingsProperties thingsProperties;

    @Override
    public String getInstanceCode() {
        if (StrUtil.isNotBlank(thingsProperties.getInstance().getCode())) {
            return thingsProperties.getInstance().getCode();
        }
        return null;
    }
}
