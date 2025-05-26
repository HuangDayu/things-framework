package cn.huangdayu.things.rest.discovery;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsInstancesChangedEvent;
import cn.huangdayu.things.common.observer.event.ThingsInstancesUpdatedEvent;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.huangdayu.things.rest.instances.ThingsInstancesManager;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsBean
public class ThingsInstancesExecutor implements ThingsInstancesManager {

    private final ThingsEventObserver thingsEventObserver;

    private static final Set<ThingsInstance> THINGS_INSTANCES = new ConcurrentHashSet<>();


    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsInstancesChangedEvent.class, engineEvent -> {
            addInstances(engineEvent.getAddedInstances());
            removeInstancesByCodes(engineEvent.getRemovedInstanceCodes());
            thingsEventObserver.notifyObservers(new ThingsInstancesUpdatedEvent(this));
        });
    }


    @Override
    public Set<ThingsInstance> addInstances(Set<ThingsInstance> thingsInstances) {
        if (CollUtil.isNotEmpty(thingsInstances)) {
            THINGS_INSTANCES.addAll(thingsInstances);
        }
        return THINGS_INSTANCES;
    }

    @Override
    public Set<ThingsInstance> addAllInstances(Set<ThingsInstance> thingsInstances) {
        THINGS_INSTANCES.clear();
        if (CollUtil.isNotEmpty(thingsInstances)) {
            THINGS_INSTANCES.addAll(thingsInstances);
        }
        thingsEventObserver.notifyObservers(new ThingsInstancesUpdatedEvent(this));
        return THINGS_INSTANCES;
    }

    @Override
    public Set<ThingsInstance> removeInstances(Set<ThingsInstance> thingsInstances) {
        if (CollUtil.isNotEmpty(thingsInstances)) {
            THINGS_INSTANCES.removeAll(thingsInstances);
        }
        return THINGS_INSTANCES;
    }


    @Override
    public Set<ThingsInstance> removeInstancesByCodes(Set<String> thingsInstanceCodes) {
        if (CollUtil.isNotEmpty(thingsInstanceCodes)) {
            THINGS_INSTANCES.removeIf(instance -> thingsInstanceCodes.contains(instance.getCode()));
        }
        return THINGS_INSTANCES;
    }

    @Override
    public Set<ThingsInstance> getAllInstances() {
        return THINGS_INSTANCES;
    }

    @Override
    public int getInstancesSize() {
        return THINGS_INSTANCES.size();
    }


}
