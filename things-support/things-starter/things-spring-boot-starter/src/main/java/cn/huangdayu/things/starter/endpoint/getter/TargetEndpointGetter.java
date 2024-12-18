package cn.huangdayu.things.starter.endpoint.getter;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.starter.endpoint.EndpointGetterType;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.huangdayu.things.starter.endpoint.ThingsEndpointGetter;
import cn.hutool.core.util.StrUtil;

import static cn.huangdayu.things.starter.endpoint.EndpointGetterType.TARGET;

/**
 * @author huangdayu
 */
@ThingsBean
public class TargetEndpointGetter implements ThingsEndpointGetter {
    @Override
    public EndpointGetterType type() {
        return TARGET;
    }

    @Override
    public String getEndpointUri(JsonThingsMessage jtm) {
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        if (StrUtil.isNotBlank(baseMetadata.getTargetCode())) {
            return ThingsInstance.valueOf(baseMetadata.getTargetCode()).getEndpointUri();
        }
        return null;
    }
}
