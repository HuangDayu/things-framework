package cn.huangdayu.things.session;

import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.api.infrastructure.ThingsSessionService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsSession;
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
