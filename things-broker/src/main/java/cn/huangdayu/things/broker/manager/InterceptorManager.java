package cn.huangdayu.things.broker.manager;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.broker.interceptor.ThingsMessageInterceptor;
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
