package cn.huangdayu.things.broker.manager;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.broker.handler.ThingsMessageHandler;
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
