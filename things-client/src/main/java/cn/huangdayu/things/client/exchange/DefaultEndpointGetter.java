package cn.huangdayu.things.client.exchange;

import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.client.ThingsClientProperties;
import cn.huangdayu.things.common.annotation.ThingsBean;
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
    public Set<String> getUris(JsonThingsMessage thingsMessage) {
        return Set.of(thingsClientProperties.getGatewayUri());
    }

    @Override
    public String getUri(JsonThingsMessage thingsMessage) {
        return thingsClientProperties.getGatewayUri();
    }
}
