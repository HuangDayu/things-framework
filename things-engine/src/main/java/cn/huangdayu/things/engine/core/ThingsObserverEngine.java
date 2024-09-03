package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.engine.async.ThingsEngineEvent;
import cn.huangdayu.things.engine.async.ThingsEventConsumer;
import cn.huangdayu.things.engine.async.ThingsEventSubscriber;

/**
 * @author huangdayu
 */
public interface ThingsObserverEngine {

    <T extends ThingsEngineEvent> ThingsEventSubscriber<T> registerObserver(Class<T> tClass, ThingsEventConsumer<T> consumer);


    <T extends ThingsEngineEvent> void removeObserver(ThingsEventSubscriber<T> eventSubscriber);


    <T extends ThingsEngineEvent> void notifyObservers(T engineEvent);


}
