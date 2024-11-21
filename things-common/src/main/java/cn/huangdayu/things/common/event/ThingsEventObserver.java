package cn.huangdayu.things.common.event;

import cn.huangdayu.things.common.annotation.ThingsBean;

import java.util.ArrayList;
import java.util.List;

import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;


/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsEventObserver {
    private final List<ThingsEventSubscriber> consumers = new ArrayList<>();

    public <T extends ThingsEngineEvent> ThingsEventSubscriber<T>   registerObserver(Class<T> tClass, ThingsEventConsumer<T> consumer) {
        ThingsEventSubscriber<T> eventSubscriber = new ThingsEventSubscriber<>(tClass, consumer);
        consumers.add(eventSubscriber);
        return eventSubscriber;
    }

    public <T extends ThingsEngineEvent> void removeObserver(ThingsEventSubscriber<T> eventSubscriber) {
        consumers.remove(eventSubscriber);
    }

    public <T extends ThingsEngineEvent> void notifyObservers(T engineEvent) {
        for (ThingsEventSubscriber consumer : consumers) {
            if (consumer.getType().isAssignableFrom(engineEvent.getClass())) {
                THINGS_EXECUTOR.execute(() -> consumer.getSubscriber().accept(engineEvent));
            }
        }
    }
}
