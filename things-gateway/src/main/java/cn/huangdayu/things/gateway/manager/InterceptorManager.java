package cn.huangdayu.things.gateway.manager;

import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.gateway.interceptor.ThingsMessageInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Component
public class InterceptorManager {

    private final Map<String, ThingsMessageInterceptor> thingsMessageFilterMap;


    boolean handler(JsonThingsMessage jsonThingsMessage) {
        for (Map.Entry<String, ThingsMessageInterceptor> entry : thingsMessageFilterMap.entrySet()) {
            if (!entry.getValue().handler(jsonThingsMessage)) {
                return false;
            }
        }
        return true;
    }

}
