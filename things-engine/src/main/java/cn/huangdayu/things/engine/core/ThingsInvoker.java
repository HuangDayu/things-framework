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
     * 是否可以处理
     *
     * @param jtm
     * @return
     */
    boolean canInvoke(JsonThingsMessage jtm);

    /**
     * 同步调用
     *
     * @param jtm
     * @return
     */
    JsonThingsMessage syncInvoke(JsonThingsMessage jtm);


    /**
     * 异步调用
     *
     * @param jtm
     * @return
     */
    Mono<JsonThingsMessage> reactorInvoke(JsonThingsMessage jtm);

}
