package cn.huangdayu.things.engine.chaining.handler;

import cn.huangdayu.things.engine.chaining.filters.FilterChain;
import cn.huangdayu.things.engine.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface Handler {


    JsonThingsMessage doHandler(JsonThingsMessage jsonThingsMessage);

}
