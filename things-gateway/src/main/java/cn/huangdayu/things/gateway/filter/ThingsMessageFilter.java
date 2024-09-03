package cn.huangdayu.things.gateway.filter;

import cn.huangdayu.things.engine.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsMessageFilter {

    boolean handler(JsonThingsMessage jsonThingsMessage);

}
