package cn.huangdayu.things.discovery;

import cn.huangdayu.things.api.infrastructure.ThingsPropertiesService;
import cn.huangdayu.things.api.instances.ThingsInstancesRegister;
import cn.huangdayu.things.api.instances.ThingsInstancesServer;
import cn.huangdayu.things.api.instances.ThingsInstancesTypeFinder;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.enums.ThingsInstanceType;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsInstancesUpdatedEvent;
import cn.huangdayu.things.common.properties.ThingsInstanceProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

import static cn.hutool.core.text.CharSequenceUtil.firstNonBlank;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsInstanceUpdater {
    private final ThingsInstancesServer thingsInstancesServer;
    private final ThingsPropertiesService thingsConfigService;
    private final ThingsEventObserver thingsEventObserver;
    private final Map<String, ThingsInstancesRegister> thingsInstancesRegisterMap;
    private final Map<String, ThingsInstancesTypeFinder> thingsInstancesTypeFinderMap;


    @PostConstruct
    public void init() {
        updateThingsInstance();
        thingsEventObserver.registerObserver(ThingsInstancesUpdatedEvent.class, engineEvent -> updateThingsInstance());
    }

    private void updateThingsInstance() {
        ThingsInstanceProperties properties = thingsConfigService.getProperties();
        ThingsInstance thingsInstance = thingsConfigService.getProperties().getInstance() == null ? new ThingsInstance() : properties.getInstance();
        thingsInstance.setName(firstNonBlank(thingsConfigService.getProperties().getInstance().getName(), thingsInstancesServer.getServerName()));
        thingsInstance.setEndpointUri(firstNonBlank(thingsConfigService.getProperties().getInstance().getEndpointUri(), getEndpointUri()));
        Set<ThingsInstanceType> thingsInstanceType = findThingsInstanceType(thingsInstance);
        if (CollUtil.isNotEmpty(thingsInstanceType)) {
            thingsInstance.setTypes(thingsInstanceType);
        }
        properties.setInstance(thingsInstance);
        thingsConfigService.updateProperties(properties);
        thingsInstancesRegisterMap.forEach((k, v) -> v.register(thingsInstance));
    }

    private Set<ThingsInstanceType> findThingsInstanceType(ThingsInstance thingsInstance) {
        Set<ThingsInstanceType> type = thingsInstance.getTypes();
        for (Map.Entry<String, ThingsInstancesTypeFinder> entry : thingsInstancesTypeFinderMap.entrySet()) {
            type.add(entry.getValue().type());
        }
        return type;
    }


    private String getEndpointUri() {
        return ThingsConstants.Protocol.RESTFUL + "://" + thingsInstancesServer.getServerHost();
    }

}
