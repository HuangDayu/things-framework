package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import reactor.core.publisher.Mono;

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
    Mono<JsonThingsMessage> reactorInvoker(JsonThingsMessage message);

}
