package cn.huangdayu.things.broker.controller;

import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.api.infrastructure.SessionService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.DslInfo;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsBrokerEndpoint implements ThingsEndpoint {

    private final SessionService sessionService;
    private final ThingsFrameworkProperties thingsFrameworkProperties;
    private final ThingsInstancesManager thingsInstancesManager;

    @Override
    public DslInfo getDsl() {
        return null;
    }

    @Override
    public JsonThingsMessage handleMessage(JsonThingsMessage jtm) {
        return null;
    }

    @Override
    public Mono<JsonThingsMessage> reactorMessage(JsonThingsMessage jtm) {
        return null;
    }

    @Override
    public void handleEvent(JsonThingsMessage jtm) {

    }

    @Override
    public ThingsInstance exchangeInstance(ThingsInstance thingsInstance) {
        return null;
    }
}
