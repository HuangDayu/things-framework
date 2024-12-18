package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.api.message.ThingsSender;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import cn.huangdayu.things.common.utils.ThingsUtils;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsPublishExecutor implements ThingsPublisher, ThingsSender {
    private final ThingsChaining thingsChaining;

    public void publishEvent(ThingsEventMessage tem) {
        sendMessage(ThingsUtils.covertEventMessage(tem));
    }

    public void publishEvent(JsonThingsMessage jtm) {
        sendMessage(jtm);
    }

    public JsonThingsMessage sendMessage(JsonThingsMessage jtm) {
        ThingsResponse thingsResponse = new ThingsResponse();
        thingsChaining.output(new ThingsRequest(jtm), thingsResponse);
        return thingsResponse.getJtm();
    }

    public Mono<JsonThingsMessage> sendReactorMessage(JsonThingsMessage jtm) {
        return Mono.just(sendMessage(jtm));
    }

}
