package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.DslInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.huangdayu.things.engine.core.ThingsChaining;
import cn.huangdayu.things.engine.core.ThingsDescriber;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@ThingsBean
public class ThingsEngineEndpoint implements ThingsEndpoint {

    private final ThingsChaining thingsChaining;
    private final ThingsDescriber thingsDescriber;
    private final ThingsInstancesManager thingsInstancesManager;

    @Override
    public JsonThingsMessage handleMessage(JsonThingsMessage jtm) {
        return thingsChaining.doReceive(jtm);
    }

    @Override
    public Mono<JsonThingsMessage> reactorMessage(JsonThingsMessage jtm) {
        return thingsChaining.doReactorReceive(jtm);
    }

    @Override
    public void handleEvent(JsonThingsMessage jtm) {
        thingsChaining.doSubscribe(jtm);
    }

    public DslInfo getDsl() {
        return thingsDescriber.getDsl();
    }

    @Override
    public ThingsInstance exchangeInstance(ThingsInstance thingsInstance) {
        return thingsInstancesManager.exchangeInstance(thingsInstance);
    }


}
