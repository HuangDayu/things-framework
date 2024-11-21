package cn.huangdayu.things.common.event;

/**
 * @author huangdayu
 */
public interface ThingsEventConsumer<T extends ThingsEngineEvent> {

    void accept(T t);

}
