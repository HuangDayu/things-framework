package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.engine.container.ThingsContainer;

/**
 * 物模型注册引擎
 *
 * @author huangdayu
 */
public interface ThingsContainerEngine {

    /**
     * 注册
     *
     * @param thingsContainer
     */
    void register(ThingsContainer thingsContainer);

    /**
     * 注销
     *
     * @param thingsContainer
     */
    void cancel(ThingsContainer thingsContainer);
}
