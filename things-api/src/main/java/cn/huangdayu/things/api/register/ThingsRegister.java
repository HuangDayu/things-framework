package cn.huangdayu.things.api.register;

import cn.huangdayu.things.api.container.ThingsContainer;

/**
 * 物模型注册引擎
 *
 * @author huangdayu
 */
public interface ThingsRegister {

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