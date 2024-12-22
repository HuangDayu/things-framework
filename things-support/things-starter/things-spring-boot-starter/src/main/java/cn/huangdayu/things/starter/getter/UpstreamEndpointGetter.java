package cn.huangdayu.things.starter.getter;

import cn.huangdayu.things.api.infrastructure.ThingsConfigService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.starter.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.starter.endpoint.ThingsEndpointGetter;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.starter.enums.EndpointGetterType.UPSTREAM;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class UpstreamEndpointGetter implements ThingsEndpointGetter {

    private final ThingsConfigService thingsConfigService;


    @Override
    public EndpointGetterType type() {
        return UPSTREAM;
    }

    @Override
    public String getEndpointUri(JsonThingsMessage jtm) {
        return thingsConfigService.getProperties().getInstance().getUpstreamUri();
    }
}
