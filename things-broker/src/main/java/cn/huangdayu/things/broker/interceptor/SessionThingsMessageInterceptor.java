package cn.huangdayu.things.broker.interceptor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;

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
