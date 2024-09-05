package cn.huangdayu.things.gateway.components;

import cn.huangdayu.things.engine.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public abstract class AbstractComponent<T extends ComponentProperty> {

    abstract void start(T property);


    abstract void output(JsonThingsMessage jsonThingsMessage);

}
