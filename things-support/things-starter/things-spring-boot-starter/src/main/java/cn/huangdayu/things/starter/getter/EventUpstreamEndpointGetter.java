package cn.huangdayu.things.starter.getter;

import cn.huangdayu.things.api.infrastructure.ThingsConfigService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.starter.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.starter.endpoint.ThingsEndpointGetter;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.EVENT_LISTENER_START_WITH;
import static cn.huangdayu.things.starter.enums.EndpointGetterType.EVENT_UPSTREAM;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class EventUpstreamEndpointGetter implements ThingsEndpointGetter {

    private final ThingsConfigService thingsConfigService;


    @Override
    public EndpointGetterType type() {
        return EVENT_UPSTREAM;
    }

    @Override
    public String getEndpointUri(JsonThingsMessage jtm) {
        return jtm.getMethod().startsWith(EVENT_LISTENER_START_WITH) ? thingsConfigService.getProperties().getInstance().getUpstreamUri() : null;
    }
}
