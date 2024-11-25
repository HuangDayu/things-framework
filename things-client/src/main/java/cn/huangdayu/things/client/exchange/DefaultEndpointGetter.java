package cn.huangdayu.things.client.exchange;

import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.client.ThingsClientProperties;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class DefaultEndpointGetter implements ThingsEndpointGetter {

    private final ThingsClientProperties thingsClientProperties;

    @Override
    public EndpointGetterType type() {
        return EndpointGetterType.DEFAULT;
    }

    @Override
    public Set<String> getPublishUris(JsonThingsMessage thingsMessage) {
        return Set.of(thingsClientProperties.getGatewayUri());
    }

    @Override
    public String getInvokeUri(JsonThingsMessage thingsMessage) {
        return thingsClientProperties.getGatewayUri();
    }
}
