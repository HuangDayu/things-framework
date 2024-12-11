package cn.huangdayu.things.gateway;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstancesDiscoverer;
import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsInstanceType;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsInstancesSyncingEvent;
import cn.huangdayu.things.common.observer.event.ThingsInstancesUpdatedEvent;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsConfiguration;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
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
    private final ThingsFrameworkProperties thingsFrameworkProperties;
    private final ThingsEndpointFactory thingsEndpointFactory;


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
            Set<ThingsInstance> allInstance = thingsInstancesDiscoverer.allInstance();
            if (CollUtil.isNotEmpty(allInstance)) {
                allInstances.addAll(allInstance);
            }
        }
        thingsInstancesManager.addAllInstances(allInstances);
        thingsEventObserver.notifyObservers(new ThingsInstancesUpdatedEvent(this));
        setUpstreamUri(allInstances);
    }

    /**
     * fix 暂时由网关负责上游地址的配置逻辑
     * @param thingsInstances
     */
    private void setUpstreamUri(Set<ThingsInstance> thingsInstances) {
        thingsInstances.parallelStream().forEach(v -> {
            if (!v.getTypes().contains(ThingsInstanceType.GATEWAY)) {
                if (StrUtil.isNotBlank(v.getUpstreamUri())) {
                    if (v.getUpstreamUri().equals(thingsFrameworkProperties.getInstance().getUpstreamUri()) ||
                            thingsInstances.stream().anyMatch(w -> w.getEndpointUri().equals(v.getUpstreamUri()))) {
                        return;
                    }
                }
                ThingsConfiguration thingsConfiguration = new ThingsConfiguration();
                thingsConfiguration.setInstanceCode(v.getCode());
                thingsConfiguration.setUpstreamUri(thingsFrameworkProperties.getInstance().getUpstreamUri());
                thingsEndpointFactory.create(v.getEndpointUri()).configuration(thingsConfiguration);
            }
        });
    }

}
