package cn.huangdayu.things.broker.interceptor;

import cn.huangdayu.things.common.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsMessageInterceptor {

    boolean handler(JsonThingsMessage jtm);

}
