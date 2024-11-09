package cn.huangdayu.things.broker.components;

import cn.huangdayu.things.common.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public abstract class AbstractComponent<T extends ComponentProperties> {

    public static final String TARGET_ROUTER = "direct:things-message-router";

    abstract void start(T property);

    abstract void stop();

    abstract void output(JsonThingsMessage jsonThingsMessage);

}
