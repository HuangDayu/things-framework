package cn.huangdayu.things.gateway.manager;

import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.gateway.handler.ThingsMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Component
public class HandlerManager {

    private final Map<String, ThingsMessageHandler> thingsMessageFilterMap;


    void handler(JsonThingsMessage jsonThingsMessage) {
        for (Map.Entry<String, ThingsMessageHandler> entry : thingsMessageFilterMap.entrySet()) {
            entry.getValue().handler(jsonThingsMessage);
        }
    }

}
