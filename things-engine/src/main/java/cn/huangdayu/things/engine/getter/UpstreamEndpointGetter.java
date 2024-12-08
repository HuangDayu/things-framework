package cn.huangdayu.things.engine.getter;

import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.enums.EndpointGetterType.EVENT_UPSTREAM;
import static cn.huangdayu.things.common.enums.EndpointGetterType.UPSTREAM;

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
    public String getEndpointUri(JsonThingsMessage thingsMessage) {
        return thingsFrameworkProperties.getInstance().getUpstreamUri();
    }
}
