package cn.huangdayu.things.session;

import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.api.session.ThingsSessions;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsSession;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class SessionEndpointGetter implements ThingsEndpointGetter {

    private final ThingsSessions thingsSessions;

    @Override
    public EndpointGetterType type() {
        return EndpointGetterType.SESSION;
    }

    @Override
    public String getEndpointUri(JsonThingsMessage jtm) {
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        ThingsSession session = thingsSessions.getSession(baseMetadata.getProductCode(), baseMetadata.getDeviceCode());
        if (session != null) {
            return session.getEndpointUri();
        }
        return null;
    }
}
