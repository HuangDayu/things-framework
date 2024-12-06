package cn.huangdayu.things.discovery;

import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.api.instances.ThingsInstancesProvider;
import cn.huangdayu.things.api.instances.ThingsInstancesTypeFinder;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.enums.ThingsInstanceType;
import cn.huangdayu.things.common.event.ThingsContainerUpdateEvent;
import cn.huangdayu.things.common.event.ThingsEngineEvent;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.common.event.ThingsInstancesChangeEvent;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

import static cn.huangdayu.things.common.enums.ThingsInstanceType.GATEWAY;
import static cn.hutool.core.text.CharSequenceUtil.firstNonBlank;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsInstanceUpdater {
    private final Environment environment;
    private final ThingsFrameworkProperties thingsFrameworkProperties;
    private final ThingsEventObserver thingsEventObserver;
    private final ThingsInstancesProvider thingsInstancesProvider;
    private final ThingsInstancesManager thingsInstancesManager;
    private final Map<String, ThingsInstancesTypeFinder> thingsInstancesTypeFinderMap;


    @PostConstruct
    public void init() {
        updateThingsInstance();
        thingsEventObserver.registerObserver(ThingsEngineEvent.class, engineEvent -> {
            if (engineEvent instanceof ThingsContainerUpdateEvent) {
                updateThingsInstance();
            }
            if (engineEvent instanceof ThingsInstancesChangeEvent thingsInstancesChangeEvent) {
                updateThingsInstance();
            }
        });
    }

    private void updateThingsInstance() {
        ThingsInstance thingsInstance = thingsFrameworkProperties.getInstance() == null ? new ThingsInstance() : thingsFrameworkProperties.getInstance();
        thingsInstance.setName(firstNonBlank(thingsFrameworkProperties.getInstance().getName(), environment.getProperty("spring.application.name")));
        thingsInstance.setEndpointUri(firstNonBlank(thingsFrameworkProperties.getInstance().getEndpointUri(), getEndpointUri()));
        thingsInstance.setProvides(thingsInstancesProvider.getProvides());
        thingsInstance.setConsumes(thingsInstancesProvider.getConsumes());
        thingsInstance.setSubscribes(thingsInstancesProvider.getSubscribes());
        Set<ThingsInstanceType> thingsInstanceType = findThingsInstanceType(thingsInstance);
        if (StrUtil.isBlank(thingsInstance.getUpstreamUri())) {
            String upstreamUri = upstreamUri(thingsInstanceType);
            if (StrUtil.isNotBlank(upstreamUri)) {
                thingsInstance.setUpstreamUri(upstreamUri);
            }
        }
        if (CollUtil.isNotEmpty(thingsInstanceType)) {
            thingsInstance.setType(thingsInstanceType);
        }
        thingsFrameworkProperties.setInstance(thingsInstance);
    }

    private String upstreamUri(Set<ThingsInstanceType> thingsInstanceTypes) {
        if (thingsInstanceTypes.contains(GATEWAY)) {
            return null;
        }
        return thingsInstancesManager.getAllThingsInstances().stream().filter(v -> v.getType().contains(GATEWAY))
                .findFirst().map(ThingsInstance::getUpstreamUri).orElse(null);
    }

    private Set<ThingsInstanceType> findThingsInstanceType(ThingsInstance thingsInstance) {
        Set<ThingsInstanceType> type = thingsInstance.getType();
        for (Map.Entry<String, ThingsInstancesTypeFinder> entry : thingsInstancesTypeFinderMap.entrySet()) {
            type.add(entry.getValue().type());
        }
        return type;
    }


    private String getEndpointUri() {
        return ThingsConstants.Protocol.RESTFUL + "://" + getIp() + ":" + environment.getProperty("server.port");
    }

    private String getIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return NetUtil.getIpByHost(Ipv4Util.LOCAL_IP);
        }
    }

}
