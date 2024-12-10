package cn.huangdayu.things.engine.getter;

import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.EVENT_LISTENER_START_WITH;
import static cn.huangdayu.things.common.enums.EndpointGetterType.EVENT_UPSTREAM;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class EventUpstreamEndpointGetter implements ThingsEndpointGetter {

    private final ThingsFrameworkProperties thingsFrameworkProperties;


    @Override
    public EndpointGetterType type() {
        return EVENT_UPSTREAM;
    }

    @Override
    public String getEndpointUri(JsonThingsMessage jtm) {
        return jtm.getMethod().startsWith(EVENT_LISTENER_START_WITH) ? thingsFrameworkProperties.getInstance().getUpstreamUri() : null;
    }
}
