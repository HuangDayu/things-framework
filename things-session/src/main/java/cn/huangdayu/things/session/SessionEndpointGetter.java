package cn.huangdayu.things.session;

import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
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

    private final ThingsSessionManager thingsSessionManager;

    @Override
    public EndpointGetterType type() {
        return EndpointGetterType.SESSION;
    }

    @Override
    public Set<String> getPublishUris(JsonThingsMessage thingsMessage) {
        BaseThingsMetadata baseMetadata = thingsMessage.getBaseMetadata();
        ThingsSession session = thingsSessionManager.getSession(baseMetadata.getProductCode(), baseMetadata.getDeviceCode());
        if (session != null) {
            return Set.of(session.getEndpointUri());
        }
        return Set.of();
    }

    @Override
    public String getInvokeUri(JsonThingsMessage thingsMessage) {
        BaseThingsMetadata baseMetadata = thingsMessage.getBaseMetadata();
        ThingsSession session = thingsSessionManager.getSession(baseMetadata.getProductCode(), baseMetadata.getDeviceCode());
        if (session != null) {
            return session.getEndpointUri();
        }
        return null;
    }
}
