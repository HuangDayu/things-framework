package cn.huangdayu.things.engine.exchange.receive.stream;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.core.ThingsChainingEngine;
import cn.huangdayu.things.engine.exchange.receive.ThingsMessageReceiver;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsStreamMessageReceiver implements ThingsMessageReceiver {

    private final ThingsChainingEngine thingsChainingEngine;

    @Override
    public JsonThingsMessage handler(JsonThingsMessage message) {
        return thingsChainingEngine.handler(message);
    }
}
