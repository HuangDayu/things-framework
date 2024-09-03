package cn.huangdayu.things.engine.async;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.core.ThingsObserverEngine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static cn.huangdayu.things.engine.async.ThreadPoolFactory.THINGS_EXECUTOR;


/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsObserverExecutor implements ThingsObserverEngine {
    private final List<ThingsEventSubscriber> consumers = new ArrayList<>();

    @Override
    public <T extends ThingsEngineEvent> ThingsEventSubscriber<T> registerObserver(Class<T> tClass, ThingsEventConsumer<T> consumer) {
        ThingsEventSubscriber<T> eventSubscriber = new ThingsEventSubscriber<>(tClass, consumer);
        consumers.add(eventSubscriber);
        return eventSubscriber;
    }

    @Override
    public <T extends ThingsEngineEvent> void removeObserver(ThingsEventSubscriber<T> eventSubscriber) {
        consumers.remove(eventSubscriber);
    }

    @Override
    public <T extends ThingsEngineEvent> void notifyObservers(T engineEvent) {
        for (ThingsEventSubscriber consumer : consumers) {
            if (consumer.getType().isAssignableFrom(engineEvent.getClass())) {
                THINGS_EXECUTOR.execute(() -> consumer.getSubscriber().accept(engineEvent));
            }
        }
    }
}
