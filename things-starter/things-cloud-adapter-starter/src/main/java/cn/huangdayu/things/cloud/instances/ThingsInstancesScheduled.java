package cn.huangdayu.things.cloud.instances;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.huangdayu.things.engine.core.ThingsInstancesEngine;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.Set;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsInstancesScheduled {


    private final Map<String, ThingsInstancesGetter> thingsInstancesGetterMap;
    private final ThingsInstancesEngine thingsInstancesEngine;


    @Scheduled(initialDelay = 10, fixedDelay = 60_000, scheduler = "thingsTaskScheduler")
    public void syncInstances() {
        Set<ThingsInstance> allInstances = new ConcurrentHashSet<>();
        for (ThingsInstancesGetter thingsInstancesGetter : thingsInstancesGetterMap.values()) {
            Set<ThingsInstance> allInstance = thingsInstancesGetter.getAllInstance();
            if (CollUtil.isNotEmpty(allInstance)) {
                allInstances.addAll(allInstance);
            }
        }
        thingsInstancesEngine.syncAllInstances(allInstances);
    }

}
