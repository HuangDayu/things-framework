package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.message.JsonThingsMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * 物模型执行引擎
 *
 * @author huangdayu
 */
public interface ThingsInvoker {

    /**
     * 同步调用
     *
     * @param message
     * @return
     */
    JsonThingsMessage syncInvoker(JsonThingsMessage message);


    /**
     * 异步调用
     *
     * @param message
     * @return
     */
    CompletableFuture<JsonThingsMessage> asyncInvoker(JsonThingsMessage message);

}
