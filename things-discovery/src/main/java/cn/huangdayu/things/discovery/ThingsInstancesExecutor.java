package cn.huangdayu.things.discovery;

import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.common.event.ThingsInstancesChangeEvent;
import cn.huangdayu.things.common.event.ThingsInstancesUpdateEvent;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.utils.ThingsUtils.subIdentifies;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsBean
public class ThingsInstancesExecutor implements ThingsInstancesManager {

    private final ThingsEventObserver thingsEventObserver;
    private final ThingsFrameworkProperties thingsFrameworkProperties;

    private static final Set<ThingsInstance> THINGS_INSTANCES = new ConcurrentHashSet<>();


    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsInstancesChangeEvent.class, engineEvent -> {
            addInstances(engineEvent.getAddedInstances());
            removeInstancesByCodes(engineEvent.getRemovedInstanceCodes());
            thingsEventObserver.notifyObservers(new ThingsInstancesUpdateEvent(this));
        });
    }

    @Override
    public Set<ThingsInstance> getProvideInstances(String productCode, String deviceCode, String identifier) {
        return THINGS_INSTANCES.parallelStream().filter(instance -> instance.getProvides().contains(productCode)).collect(Collectors.toSet());
    }

    @Override
    public Set<ThingsInstance> getSubscribeInstances(String productCode, String deviceCode, String identifier) {
        return THINGS_INSTANCES.parallelStream().filter(instance -> {
            if (instance.getConsumes().contains(productCode)) {
                if (StrUtil.isNotBlank(identifier)) {
                    String identifier1 = subIdentifies(identifier);
                    return instance.getSubscribes().contains(THINGS_WILDCARD + THINGS_SEPARATOR + identifier1) || instance.getSubscribes().contains(productCode + THINGS_SEPARATOR + identifier1);
                }
                return instance.getSubscribes().contains(productCode + THINGS_SEPARATOR + THINGS_WILDCARD);
            }
            return false;
        }).collect(Collectors.toSet());
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
        THINGS_INSTANCES.add(thingsFrameworkProperties.getInstance());
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
        return thingsFrameworkProperties.getInstance();
    }

    @Override
    public Set<ThingsInstance> getAllThingsInstances() {
        return THINGS_INSTANCES;
    }

    @Override
    public int getInstancesSize() {
        return THINGS_INSTANCES.size();
    }

}
