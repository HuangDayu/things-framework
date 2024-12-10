package cn.huangdayu.things.broker.controller;

import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.api.session.ThingsSessions;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.DslInfo;
import cn.huangdayu.things.common.dsl.ThingsInfo;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsBrokerEndpoint implements ThingsEndpoint {

    private final ThingsSessions thingsSessions;
    private final ThingsFrameworkProperties thingsFrameworkProperties;
    private final ThingsInstancesManager thingsInstancesManager;

    @Override
    public DslInfo getDsl() {
        return null;
    }

    @Override
    public JsonThingsMessage handleMessage(JsonThingsMessage message) {
        return null;
    }

    @Override
    public Mono<JsonThingsMessage> reactorMessage(JsonThingsMessage message) {
        return null;
    }

    @Override
    public void handleEvent(JsonThingsMessage message) {

    }

    @Override
    public ThingsInstance exchangeInstance(ThingsInstance thingsInstance) {
        return null;
    }
}
