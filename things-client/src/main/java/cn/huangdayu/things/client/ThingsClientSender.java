package cn.huangdayu.things.client;

import cn.huangdayu.things.api.sender.ThingsSender;
import cn.huangdayu.things.client.factory.ThingsClientFactory;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ThingsClientSender implements ThingsSender {

    public final String gatewayUrl;

    @Override
    public boolean canSend(JsonThingsMessage message) {
        return true;
    }

    @Override
    public JsonThingsMessage doSend(JsonThingsMessage message) {
        return ThingsClientFactory.createRestClient(ThingsInstanceEndpoint.class, gatewayUrl).handler(message);
    }

    @Override
    public void doPublish(JsonThingsMessage message) {
        ThingsClientFactory.createRestClient(ThingsInstanceEndpoint.class, gatewayUrl).handler(message);
    }
}
