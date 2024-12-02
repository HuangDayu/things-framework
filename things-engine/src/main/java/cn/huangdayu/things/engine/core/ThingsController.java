package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.api.instances.ThingsInstanceManager;
import cn.huangdayu.things.api.receiver.ThingsReceiver;
import cn.huangdayu.things.api.restful.ThingsEndpoint;
import cn.huangdayu.things.common.dto.ThingsInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;


@RequiredArgsConstructor
@RestController
@RequestMapping
public class ThingsController implements ThingsEndpoint {

    private final ThingsReceiver thingsReceiver;
    private final ThingsDescriber thingsDescriber;
    private final ThingsInstanceManager thingsInstanceManager;

    @Override
    public JsonThingsMessage send(JsonThingsMessage message) {
        return thingsReceiver.doReceive(message);
    }

    @Override
    public void publish(JsonThingsMessage message) {
        thingsReceiver.doSubscribe(message);
    }

    public Set<ThingsInfo> getThingsDsl() {
        return thingsDescriber.getThingsDsl();
    }

    @Override
    public ThingsInstance exchange(ThingsInstance thingsInstance) {
        return thingsInstanceManager.exchangeInstance(thingsInstance);
    }


}
