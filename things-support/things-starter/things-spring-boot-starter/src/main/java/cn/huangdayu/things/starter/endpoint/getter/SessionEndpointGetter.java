package cn.huangdayu.things.starter.endpoint.getter;

import cn.huangdayu.things.api.infrastructure.ThingsSessionService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.starter.endpoint.EndpointGetterType;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsSession;
import cn.huangdayu.things.starter.endpoint.ThingsEndpointGetter;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class SessionEndpointGetter implements ThingsEndpointGetter {

    private final ThingsSessionService thingsSessionService;

    @Override
    public EndpointGetterType type() {
        return EndpointGetterType.SESSION;
    }

    @Override
    public String getEndpointUri(JsonThingsMessage jtm) {
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        ThingsSession session = thingsSessionService.getSession(baseMetadata.getProductCode(), baseMetadata.getDeviceCode());
        if (session != null) {
            return session.getEndpointUri();
        }
        return null;
    }
}
