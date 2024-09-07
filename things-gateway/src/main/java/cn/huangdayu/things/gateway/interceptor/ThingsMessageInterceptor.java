package cn.huangdayu.things.gateway.interceptor;

import cn.huangdayu.things.engine.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsMessageInterceptor {

    boolean handler(JsonThingsMessage jsonThingsMessage);

}
