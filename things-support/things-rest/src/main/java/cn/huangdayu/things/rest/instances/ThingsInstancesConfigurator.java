package cn.huangdayu.things.rest.instances;

import cn.huangdayu.things.api.infrastructure.ThingsPropertiesService;
import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsInstanceType;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsInstancesUpdatedEvent;
import cn.huangdayu.things.common.wrapper.ThingsConfiguration;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.huangdayu.things.rest.endpoint.ThingsEndpointFactory;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsInstancesConfigurator {

    private final ThingsInstancesManager thingsInstancesManager;
    private final ThingsEventObserver thingsEventObserver;
    private final ThingsPropertiesService thingsConfigService;
    private final ThingsEndpointFactory thingsEndpointFactory;

    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsInstancesUpdatedEvent.class, engineEvent -> setUpstreamUri());
    }


    /**
     * fix 暂时由网关负责上游地址的配置逻辑
     */
    private void setUpstreamUri() {
        Set<ThingsInstance> thingsInstances = thingsInstancesManager.getAllInstances();
        thingsInstances.parallelStream().filter(v -> {
            if (v.getTypes().contains(ThingsInstanceType.GATEWAY)) {
                return false;
            }
            if (StrUtil.isBlank(v.getUpstreamUri())) {
                return true;
            }
            return !v.getUpstreamUri().equals(thingsConfigService.getProperties().getInstance().getUpstreamUri()) &&
                    thingsInstances.stream().noneMatch(w -> w.getEndpointUri().equals(v.getUpstreamUri()));
        }).forEach(v -> {
            ThingsConfiguration thingsConfiguration = new ThingsConfiguration();
            thingsConfiguration.setInstanceCode(v.getCode());
            thingsConfiguration.setUpstreamUri(thingsConfigService.getProperties().getInstance().getEndpointUri());
            thingsEndpointFactory.create(v.getEndpointUri()).configuration(thingsConfiguration);
        });
    }
}
