package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dto.ThingsInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.huangdayu.things.engine.core.ThingsChaining;
import cn.huangdayu.things.engine.core.ThingsDescriber;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor
@ThingsBean
public class ThingsEngineEndpoint implements ThingsEndpoint {

    private final ThingsChaining thingsChaining;
    private final ThingsDescriber thingsDescriber;
    private final ThingsInstancesManager thingsInstancesManager;

    @Override
    public JsonThingsMessage handleMessage(JsonThingsMessage message) {
        return thingsChaining.doReceive(message);
    }

    @Override
    public Mono<JsonThingsMessage> asyncMessage(JsonThingsMessage message) {
        return thingsChaining.asyncMessage(message);
    }

    @Override
    public void handleEvent(JsonThingsMessage message) {
        thingsChaining.doSubscribe(message);
    }

    public Set<ThingsInfo> getThingsDsl() {
        return thingsDescriber.getThingsDsl();
    }

    @Override
    public ThingsInstance exchange(ThingsInstance thingsInstance) {
        return thingsInstancesManager.exchangeInstance(thingsInstance);
    }


}
