package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.camel.CamelSofaBusConstructor;

/**
 * @author huangdayu
 */
public abstract class ProducerSofaBus extends AbstractSofaBus {

    public ProducerSofaBus(CamelSofaBusConstructor constructor) {
        super(constructor);
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

}
