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
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsBean
public class ThingsPublishExecutor implements ThingsPublisher {
    private final ThingsChaining thingsChaining;

    public void publishEvent(ThingsEventMessage tem) {
        publishEvent(ThingsUtils.covertEventMessage(tem));
    }

    public void publishEvent(JsonThingsMessage jtm) {
        thingsChaining.output(new ThingsRequest(jtm), new ThingsResponse());
    }

    @SneakyThrows
    public JsonThingsMessage syncSendMessage(JsonThingsMessage jtm) {
        ThingsRequest thingsRequest = new ThingsRequest(jtm);
        CompletableFuture<ThingsResponse> future = new CompletableFuture<>();
        thingsRequest.setResponseFuture(future);
        thingsChaining.output(thingsRequest, new ThingsResponse());
        try {
            return future.get(jtm.getTimeout(), TimeUnit.MILLISECONDS).getJtm();
        } catch (Exception e) {
            log.error("Things publish message [{}] error: {} ", jtm, e.getMessage());
            return jtm.timeout();
        }
    }

    @Override
    public void asyncSendMessage(JsonThingsMessage jtm, Consumer<JsonThingsMessage> consumer) {
        ThingsRequest thingsRequest = new ThingsRequest(jtm);
        if (consumer != null) {
            thingsRequest.setResponseConsumer(response -> consumer.accept(response.getJtm()));
        }
        thingsChaining.output(thingsRequest, new ThingsResponse());
    }

    @SneakyThrows
    public Mono<JsonThingsMessage> reactorSendMessage(JsonThingsMessage jtm) {
        return Mono.just(syncSendMessage(jtm));
    }

}
