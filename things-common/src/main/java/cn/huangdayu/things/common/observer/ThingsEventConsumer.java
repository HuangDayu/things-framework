package cn.huangdayu.things.common.observer;

/**
 * @author huangdayu
 */
public interface ThingsEventConsumer<T extends ThingsBaseEvent> {

    void accept(T t);

}
