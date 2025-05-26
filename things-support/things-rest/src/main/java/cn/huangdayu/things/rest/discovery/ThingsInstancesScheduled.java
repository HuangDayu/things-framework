package cn.huangdayu.things.rest.discovery;

import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.huangdayu.things.rest.instances.ThingsInstancesDiscoverer;
import cn.huangdayu.things.rest.instances.ThingsInstancesManager;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * @author huangdayu
 */
//@ThingsBean
@RequiredArgsConstructor
public class ThingsInstancesScheduled {


    private final Map<String, ThingsInstancesDiscoverer> thingsInstancesGetterMap;
    private final ThingsInstancesManager thingsInstancesManager;


//    @PostConstruct
//    public void init() {
//        CronUtil.schedule("0 * * * * ?", (Task) this::syncInstances);
//        // 支持秒级别定时任务
//        CronUtil.setMatchSecond(true);
//        CronUtil.start();
//    }
//
//    @PreDestroy
//    public void destroy() {
//        CronUtil.stop();
//    }

    public void syncInstances() {
        Set<ThingsInstance> allInstances = new ConcurrentHashSet<>();
        for (ThingsInstancesDiscoverer thingsInstancesDiscoverer : thingsInstancesGetterMap.values()) {
            Set<ThingsInstance> allInstance = thingsInstancesDiscoverer.allInstance();
            if (CollUtil.isNotEmpty(allInstance)) {
                allInstances.addAll(allInstance);
            }
        }
        thingsInstancesManager.addAllInstances(allInstances);
    }


}
