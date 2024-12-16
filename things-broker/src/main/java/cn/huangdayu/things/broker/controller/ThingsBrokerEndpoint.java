package cn.huangdayu.things.broker.controller;


import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.api.infrastructure.ThingsSessionService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.DslInfo;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsConfiguration;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsBrokerEndpoint implements ThingsEndpoint {

    private final ThingsSessionService thingsSessionService;
    private final ThingsFrameworkProperties thingsFrameworkProperties;

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
    public void configuration(ThingsConfiguration thingsConfiguration) {

    }


}
