package cn.huangdayu.things.rest.getter;

import cn.huangdayu.things.api.infrastructure.ThingsPropertiesService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.rest.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.rest.endpoint.ThingsEndpointGetter;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.rest.enums.EndpointGetterType.UPSTREAM;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class UpstreamEndpointGetter implements ThingsEndpointGetter {

    private final ThingsPropertiesService thingsConfigService;


    @Override
    public EndpointGetterType type() {
        return UPSTREAM;
    }

    @Override
    public String getEndpointUri(JsonThingsMessage jtm) {
        return thingsConfigService.getProperties().getInstance().getUpstreamUri();
    }
}
