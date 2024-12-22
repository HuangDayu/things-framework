package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import cn.huangdayu.things.common.utils.ThingsUtils;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsPublishExecutor implements ThingsPublisher {
    private final ThingsChaining thingsChaining;

    public void publishEvent(ThingsEventMessage tem) {
        syncSendMessage(ThingsUtils.covertEventMessage(tem));
    }

    public void publishEvent(JsonThingsMessage jtm) {
        thingsChaining.output(new ThingsRequest(jtm), new ThingsResponse());
    }

    @SneakyThrows
    public JsonThingsMessage syncSendMessage(JsonThingsMessage jtm) {
        ThingsResponse thingsResponse = new ThingsResponse();
        CompletableFuture<ThingsResponse> future = new CompletableFuture<>();
        thingsResponse.setFuture(future);
        thingsChaining.output(new ThingsRequest(jtm), thingsResponse);
        return future.get(jtm.getTimeout(), TimeUnit.MILLISECONDS).getJtm();
    }

    @Override
    public void asyncSendMessage(JsonThingsMessage jtm, Consumer<JsonThingsMessage> consumer) {
        ThingsResponse thingsResponse = new ThingsResponse();
        thingsResponse.setConsumer(response -> consumer.accept(response.getJtm()));
        thingsChaining.output(new ThingsRequest(jtm), thingsResponse);
    }

    @SneakyThrows
    public Mono<JsonThingsMessage> reactorSendMessage(JsonThingsMessage jtm) {
        return Mono.just(syncSendMessage(jtm));
    }

}
