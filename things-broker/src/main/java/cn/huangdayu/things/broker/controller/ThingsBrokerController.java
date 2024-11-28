package cn.huangdayu.things.broker.controller;

import cn.huangdayu.things.api.instances.ThingsInstances;
import cn.huangdayu.things.api.restful.ThingsEndpoint;
import cn.huangdayu.things.api.session.ThingsSessions;
import cn.huangdayu.things.common.dto.ThingsInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@RestController
@RequestMapping
public class ThingsBrokerController implements ThingsEndpoint {

    private final ThingsSessions thingsSessions;
    private final ThingsProperties thingsProperties;
    private final ThingsInstances thingsInstances;

    @Override
    public Set<ThingsInfo> getThingsDsl() {
        return null;
    }

    @Override
    public JsonThingsMessage handler(JsonThingsMessage message) {
        return null;
    }

    @Override
    public ThingsInstance exchangeInstance(ThingsInstance thingsInstance) {
        return null;
    }
}
