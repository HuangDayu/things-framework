package cn.huangdayu.things.discovery;

import cn.huangdayu.things.api.instances.ThingsInstancesDiscoverer;
import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsInstancesSyncingEvent;
import cn.huangdayu.things.common.observer.event.ThingsInstancesUpdatedEvent;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsInstancesScheduled {


    private final Map<String, ThingsInstancesDiscoverer> thingsInstancesGetterMap;
    private final ThingsInstancesManager thingsInstancesManager;
    private final ThingsEventObserver thingsEventObserver;


    @PostConstruct
    public void init() {
        CronUtil.schedule("0 * * * * ?", (Task) this::syncInstances);
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
        thingsEventObserver.registerObserver(ThingsInstancesSyncingEvent.class, engineEvent -> syncInstances());
    }

    @PreDestroy
    public void destroy() {
        CronUtil.stop();
    }

    public void syncInstances() {
        Set<ThingsInstance> allInstances = new ConcurrentHashSet<>();
        for (ThingsInstancesDiscoverer thingsInstancesDiscoverer : thingsInstancesGetterMap.values()) {
            Set<ThingsInstance> allInstance = thingsInstancesDiscoverer.getAllInstance();
            if (CollUtil.isNotEmpty(allInstance)) {
                allInstances.addAll(allInstance);
            }
        }
        thingsInstancesManager.syncAllInstances(allInstances);
        thingsEventObserver.notifyObservers(new ThingsInstancesUpdatedEvent(this));
    }

}
