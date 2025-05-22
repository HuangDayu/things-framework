package cn.huangdayu.things.rest.getter;

import cn.huangdayu.things.api.infrastructure.ThingsPropertiesService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.rest.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.rest.enums.EndpointGetterType;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.utils.ThingsUtils.isEventPost;
import static cn.huangdayu.things.rest.enums.EndpointGetterType.EVENT_UPSTREAM;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class EventUpstreamEndpointGetter implements ThingsEndpointGetter {

    private final ThingsPropertiesService thingsConfigService;


    @Override
    public EndpointGetterType type() {
        return EVENT_UPSTREAM;
    }

    @Override
    public String getEndpointUri(JsonThingsMessage jtm) {
        return isEventPost(jtm) ? thingsConfigService.getProperties().getInstance().getUpstreamUri() : null;
    }
}
