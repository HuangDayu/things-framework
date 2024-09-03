package cn.huangdayu.things.engine.async;

/**
 * @author huangdayu
 */
public interface ThingsEventConsumer<T extends ThingsEngineEvent> {

    void accept(T t);

}
