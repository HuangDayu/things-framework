package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.async.ThingsContainerUpdateEvent;
import cn.huangdayu.things.engine.async.ThingsEngineEvent;
import cn.huangdayu.things.engine.async.ThingsInstancesChangeEvent;
import cn.huangdayu.things.engine.common.ThingsConstants;
import cn.huangdayu.things.engine.configuration.ThingsEngineProperties;
import cn.huangdayu.things.engine.core.ThingsInstancesEngine;
import cn.huangdayu.things.engine.core.ThingsObserverEngine;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.huangdayu.things.engine.common.ThingsConstants.THINGS_SEPARATOR;
import static cn.huangdayu.things.engine.common.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.engine.common.ThingsUtils.subIdentifies;
import static cn.huangdayu.things.engine.core.executor.ThingsEngineBaseExecutor.THINGS_EVENTS_LISTENER_TABLE;
import static cn.huangdayu.things.engine.core.executor.ThingsEngineBaseExecutor.THINGS_SERVICES_TABLE;
import static cn.hutool.core.text.CharSequenceUtil.firstNonBlank;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsBean
public class ThingsInstancesExecutor implements ThingsInstancesEngine {

    private final Environment environment;
    private final ThingsObserverEngine thingsObserverEngine;
    private final ThingsEngineProperties thingsEngineProperties;
    private static final Set<ThingsInstance> THINGS_INSTANCES = new ConcurrentHashSet<>();
    private static volatile ThingsInstance thingsInstance;

    @PostConstruct
    public void init() {
        updateThingsInstance();
        thingsObserverEngine.registerObserver(ThingsEngineEvent.class, engineEvent -> {
            if (engineEvent instanceof ThingsContainerUpdateEvent) {
                updateThingsInstance();
            }
            if (engineEvent instanceof ThingsInstancesChangeEvent thingsInstancesChangeEvent) {
                addInstances(thingsInstancesChangeEvent.getAddedInstances());
                removeInstancesByCodes(thingsInstancesChangeEvent.getRemovedInstanceCodes());
                updateThingsInstance();
            }
        });
    }

    @Override
    public ThingsInstance getThingsInstance() {
        return thingsInstance;
    }

    private void updateThingsInstance() {
        thingsInstance = createThingsInstance();
        THINGS_INSTANCES.add(thingsInstance);
    }


    private ThingsInstance createThingsInstance() {
        ThingsInstance thingsInstance = new ThingsInstance();
        thingsInstance.setName(firstNonBlank(thingsEngineProperties.getInstance().getName(), environment.getProperty("spring.application.name")));
        thingsInstance.setEndpointUri(firstNonBlank(thingsEngineProperties.getInstance().getEndpointUri(), getEndpointUri()));
        thingsInstance.setProvides(THINGS_SERVICES_TABLE.columnKeySet());
        thingsInstance.setConsumes(THINGS_EVENTS_LISTENER_TABLE.columnKeySet());
        thingsInstance.setSubscribes(THINGS_EVENTS_LISTENER_TABLE.cellSet()
                .stream().map(cell -> cell.getColumnKey() + THINGS_SEPARATOR + cell.getRowKey()).collect(Collectors.toSet()));
        return thingsInstance;
    }

    private String getEndpointUri() {
        return ThingsConstants.Protocol.REST + "://" + getIp() + ":" + environment.getProperty("server.port");
    }

    @Override
    public Set<ThingsInstance> getProvideInstances(String productCode, String deviceCode, String identifier) {
        return THINGS_INSTANCES.parallelStream().filter(instance -> instance.getProvides().contains(productCode)).collect(Collectors.toSet());
    }

    @Override
    public Set<ThingsInstance> getConsumeInstances(String productCode, String deviceCode, String identifier) {
        return THINGS_INSTANCES.parallelStream().filter(instance -> {
            if (instance.getConsumes().contains(productCode)) {
                if (StrUtil.isNotBlank(identifier)) {
                    String identifier1 = subIdentifies(identifier);
                    return instance.getSubscribes().contains(THINGS_WILDCARD + THINGS_SEPARATOR + identifier1) ||
                            instance.getSubscribes().contains(productCode + THINGS_SEPARATOR + identifier1);
                }
                return instance.getSubscribes().contains(productCode + THINGS_SEPARATOR + THINGS_WILDCARD);
            }
            return false;
        }).collect(Collectors.toSet());
    }

    private String getIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return NetUtil.getIpByHost(Ipv4Util.LOCAL_IP);
        }
    }


    @Override
    public Set<ThingsInstance> addInstances(Set<ThingsInstance> thingsInstances) {
        if (CollUtil.isNotEmpty(thingsInstances)) {
            THINGS_INSTANCES.addAll(thingsInstances);
        }
        return THINGS_INSTANCES;
    }

    @Override
    public Set<ThingsInstance> syncAllInstances(Set<ThingsInstance> thingsInstances) {
        THINGS_INSTANCES.clear();
        if (CollUtil.isNotEmpty(thingsInstances)) {
            THINGS_INSTANCES.addAll(thingsInstances);
        }
        THINGS_INSTANCES.add(thingsInstance);
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
    public ThingsInstance exchangeInstance(ThingsInstance instance) {
        THINGS_INSTANCES.add(instance);
        return thingsInstance;
    }

    public static int getInstancesSize() {
        return THINGS_INSTANCES.size();
    }

}
