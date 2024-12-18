package cn.huangdayu.things.starter.endpoint.getter;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.starter.endpoint.EndpointGetterType;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.starter.endpoint.ThingsEndpointGetter;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.starter.endpoint.EndpointGetterType.UPSTREAM;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class UpstreamEndpointGetter implements ThingsEndpointGetter {

    private final ThingsFrameworkProperties thingsFrameworkProperties;


    @Override
    public EndpointGetterType type() {
        return UPSTREAM;
    }

    @Override
    public String getEndpointUri(JsonThingsMessage jtm) {
        return thingsFrameworkProperties.getInstance().getUpstreamUri();
    }
}
