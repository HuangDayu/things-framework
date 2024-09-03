package cn.huangdayu.things.gateway.handler;

import cn.huangdayu.things.engine.message.JsonThingsMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Component
public class ThingsMessageHandlerFactory {

    private final Map<String, ThingsMessageHandler> thingsMessageFilterMap;


    void handler(JsonThingsMessage jsonThingsMessage) {
        for (Map.Entry<String, ThingsMessageHandler> entry : thingsMessageFilterMap.entrySet()) {
            entry.getValue().handler(jsonThingsMessage);
        }
    }

}
