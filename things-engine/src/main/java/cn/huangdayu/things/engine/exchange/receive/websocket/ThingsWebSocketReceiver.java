package cn.huangdayu.things.engine.exchange.receive.websocket;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.exchange.receive.ThingsMessageReceiver;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsWebSocketReceiver implements ThingsMessageReceiver {
    @Override
    public JsonThingsMessage handler(JsonThingsMessage message) {
        return null;
    }
}
