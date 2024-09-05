package cn.huangdayu.things.engine.endpoint;

import cn.huangdayu.things.engine.core.*;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.engine.wrapper.ThingsInfo;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;
import cn.huangdayu.things.engine.wrapper.ThingsSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;


@RequiredArgsConstructor
@RestController
@RequestMapping
public class ThingsRestfulController implements ThingsRestfulEndpoint {

    private final ThingsChainingEngine thingsChainingEngine;
    private final ThingsDocumentEngine thingsDocumentEngine;
    private final ThingsSessionEngine thingsSessionEngine;
    private final ThingsInstancesEngine thingsInstancesEngine;

    @Override
    public JsonThingsMessage handler(JsonThingsMessage message) {
        return thingsChainingEngine.handler(message);
    }

    public Set<ThingsInfo> getThings() {
        return thingsDocumentEngine.getThings();
    }

    @Override
    public ThingsSession getSession(String productCode, String deviceCode) {
        return thingsSessionEngine.getSession(productCode, deviceCode);
    }

    @Override
    public ThingsInstance exchangeInstance(ThingsInstance thingsInstance) {
        return thingsInstancesEngine.exchangeInstance(thingsInstance);
    }


}
