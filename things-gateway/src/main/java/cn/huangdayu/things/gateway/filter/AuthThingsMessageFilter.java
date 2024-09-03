package cn.huangdayu.things.gateway.filter;

import cn.huangdayu.things.engine.message.JsonThingsMessage;
import org.springframework.stereotype.Component;

/**
 * @author huangdayu
 */
@Component
public class AuthThingsMessageFilter implements ThingsMessageFilter {
    @Override
    public boolean handler(JsonThingsMessage jsonThingsMessage) {
        return true;
    }
}
