package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.message.JsonThingsMessage;

/**
 * 物模型执行引擎
 *
 * @author huangdayu
 */
public interface ThingsInvokerEngine {

    /**
     * 执行物模型消息
     *
     * @param message
     * @return
     */
    JsonThingsMessage execute(JsonThingsMessage message);

}
