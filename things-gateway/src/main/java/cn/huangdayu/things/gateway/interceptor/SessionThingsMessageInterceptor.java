package cn.huangdayu.things.gateway.interceptor;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
@ThingsBean
public class SessionThingsMessageInterceptor implements ThingsMessageInterceptor {
    @Override
    public boolean handler(JsonThingsMessage jsonThingsMessage) {
        return true;
    }
}
