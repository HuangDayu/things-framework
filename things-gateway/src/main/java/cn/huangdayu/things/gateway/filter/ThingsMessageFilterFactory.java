package cn.huangdayu.things.gateway.filter;

import cn.huangdayu.things.engine.message.JsonThingsMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Component
public class ThingsMessageFilterFactory {

    private final Map<String, ThingsMessageFilter> thingsMessageFilterMap;


    boolean handler(JsonThingsMessage jsonThingsMessage) {
        for (Map.Entry<String, ThingsMessageFilter> entry : thingsMessageFilterMap.entrySet()) {
            if (!entry.getValue().handler(jsonThingsMessage)) {
                return false;
            }
        }
        return true;
    }

}
