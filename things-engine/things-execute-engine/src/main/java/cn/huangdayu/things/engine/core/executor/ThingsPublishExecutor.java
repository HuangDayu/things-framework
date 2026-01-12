package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
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

    public void publishEvent(ThingsRequestMessage trm) {
        thingsChaining.output(new ThingsRequest(trm), new ThingsResponse());
    }

    @SneakyThrows
    public ThingsResponseMessage syncSendMessage(ThingsRequestMessage trm) {
        ThingsRequest thingsRequest = new ThingsRequest(trm);
        CompletableFuture<ThingsResponse> future = new CompletableFuture<>();
        thingsRequest.setResponseFuture(future);
        thingsChaining.output(thingsRequest, new ThingsResponse());
        try {
            return future.get(trm.getTimeout(), TimeUnit.MILLISECONDS).getTrm();
        } catch (Exception e) {
            log.error("Things publish message [{}] error: {} ", trm, e.getMessage());
            return trm.timeout();
        }
    }

    @Override
    public void asyncSendMessage(ThingsRequestMessage trm, Consumer<ThingsResponseMessage> consumer) {
        ThingsRequest thingsRequest = new ThingsRequest(trm);
        if (consumer != null) {
            thingsRequest.setResponseConsumer(response -> consumer.accept(response.getTrm()));
        }
        thingsChaining.output(thingsRequest, new ThingsResponse());
    }

    @SneakyThrows
    public Mono<ThingsResponseMessage> reactorSendMessage(ThingsRequestMessage trm) {
        return Mono.just(syncSendMessage(trm));
    }

}
