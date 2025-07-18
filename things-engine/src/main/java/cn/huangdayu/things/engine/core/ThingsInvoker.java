package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
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
     * @param trm
     * @return
     */
    boolean canInvoke(ThingsRequestMessage trm);

    /**
     * 同步调用
     *
     * @param trm
     * @return
     */
    ThingsResponseMessage syncInvoke(ThingsRequestMessage trm);


    /**
     * 异步调用
     *
     * @param trm
     * @return
     */
    Mono<ThingsResponseMessage> reactorInvoke(ThingsRequestMessage trm);

}
