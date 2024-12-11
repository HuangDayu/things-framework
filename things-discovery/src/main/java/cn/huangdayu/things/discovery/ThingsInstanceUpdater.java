package cn.huangdayu.things.discovery;

import cn.huangdayu.things.api.instances.ThingsInstancesDiscoverer;
import cn.huangdayu.things.api.instances.ThingsInstancesRegister;
import cn.huangdayu.things.api.instances.ThingsInstancesServer;
import cn.huangdayu.things.api.instances.ThingsInstancesTypeFinder;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.enums.ThingsInstanceType;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsContainerUpdatedEvent;
import cn.huangdayu.things.common.observer.event.ThingsInstancesUpdatedEvent;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
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
    private final ThingsFrameworkProperties thingsFrameworkProperties;
    private final ThingsEventObserver thingsEventObserver;
    private final Map<String, ThingsInstancesRegister> thingsInstancesRegisterMap;
    private final Map<String, ThingsInstancesTypeFinder> thingsInstancesTypeFinderMap;


    @PostConstruct
    public void init() {
        updateThingsInstance();
        thingsEventObserver.registerObserver(ThingsContainerUpdatedEvent.class, engineEvent -> updateThingsInstance());
        thingsEventObserver.registerObserver(ThingsInstancesUpdatedEvent.class, engineEvent -> updateThingsInstance());
    }

    private void updateThingsInstance() {
        ThingsInstance thingsInstance = thingsFrameworkProperties.getInstance() == null ? new ThingsInstance() : thingsFrameworkProperties.getInstance();
        thingsInstance.setName(firstNonBlank(thingsFrameworkProperties.getInstance().getName(), thingsInstancesServer.getServerName()));
        thingsInstance.setEndpointUri(firstNonBlank(thingsFrameworkProperties.getInstance().getEndpointUri(), getEndpointUri()));
        Set<ThingsInstanceType> thingsInstanceType = findThingsInstanceType(thingsInstance);
        if (CollUtil.isNotEmpty(thingsInstanceType)) {
            thingsInstance.setTypes(thingsInstanceType);
        }
        thingsFrameworkProperties.setInstance(thingsInstance);
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
