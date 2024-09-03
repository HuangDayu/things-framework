package cn.huangdayu.things.gateway.components;

/**
 * @author huangdayu
 */
public abstract class AbstractComponent<T extends ComponentProperty> {

    abstract void start(T property);

}
