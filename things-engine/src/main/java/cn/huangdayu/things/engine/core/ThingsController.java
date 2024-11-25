package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.api.receiver.ThingsReceiver;
import cn.huangdayu.things.api.restful.ThingsRestfulEndpoint;
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
public class ThingsController implements ThingsRestfulEndpoint {

    private final ThingsReceiver thingsReceiver;
    private final ThingsDescriber thingsDescriber;
    private final ThingsInstancesManager thingsInstancesManager;

    @Override
    public JsonThingsMessage handler(JsonThingsMessage message) {
        return thingsReceiver.doReceive(message);
    }

    public Set<ThingsInfo> getThingsDsl() {
        return thingsDescriber.getThingsDsl();
    }

    @Override
    public ThingsInstance exchangeInstance(ThingsInstance thingsInstance) {
        return thingsInstancesManager.exchangeInstance(thingsInstance);
    }


}
