package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.api.message.ThingsSender;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import cn.huangdayu.things.engine.core.ThingsChaining;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsPublisherExecutor implements ThingsPublisher, ThingsSender {
    private final ThingsChaining thingsChaining;


    @Override
    public void publishEvent(ThingsEventMessage tem) {
        THINGS_EXECUTOR.execute(() -> thingsChaining.doPublish(tem));
    }

    @Override
    public void publishEvent(JsonThingsMessage jtm) {
        THINGS_EXECUTOR.execute(() -> thingsChaining.doPublish(jtm));
    }

    @Override
    public JsonThingsMessage sendMessage(JsonThingsMessage jtm) {
        return thingsChaining.doSend(jtm);
    }

    @Override
    public Mono<JsonThingsMessage> sendReactorMessage(JsonThingsMessage jtm) {
        return thingsChaining.doReactorSend(jtm);
    }

}
