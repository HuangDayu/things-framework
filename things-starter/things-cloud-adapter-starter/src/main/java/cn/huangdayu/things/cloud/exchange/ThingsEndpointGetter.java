package cn.huangdayu.things.cloud.exchange;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.common.ThingsConstants;
import cn.huangdayu.things.engine.core.ThingsInstancesEngine;
import cn.huangdayu.things.engine.message.BaseThingsMetadata;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsEndpointGetter {

    private final ThingsInstancesEngine thingsInstancesEngine;

    public Set<String> getTargetEndpointUris(JsonThingsMessage thingsMessage) {
        Set<String> set = new LinkedHashSet<>();
        BaseThingsMetadata baseMetadata = thingsMessage.getBaseMetadata();
        if (thingsMessage.getMethod().startsWith(ThingsConstants.Methods.EVENT_LISTENER_START_WITH)) {
            Set<ThingsInstance> consumeInstances = thingsInstancesEngine.getConsumeInstances(baseMetadata.getProductCode(), baseMetadata.getDeviceCode(), thingsMessage.getMethod());
            if (CollUtil.isNotEmpty(consumeInstances)) {
                set.addAll(consumeInstances.stream().map(v -> StrUtil.isNotBlank(v.getBrokerUri()) || v.isUseBroker() ? v.getBrokerUri() : v.getEndpointUri()).collect(Collectors.toSet()));
            }
        }
        if (StrUtil.isNotBlank(thingsInstancesEngine.getThingsInstance().getBrokerUri())) {
            set.add(thingsInstancesEngine.getThingsInstance().getBrokerUri());
        }
        return set;
    }


    public String getTargetEndpointUri(JsonThingsMessage thingsMessage) {
        BaseThingsMetadata baseMetadata = thingsMessage.getBaseMetadata();
        if (thingsMessage.isResponse() && StrUtil.isNotBlank(baseMetadata.getSource())) {
            return ThingsInstance.valueOf(baseMetadata.getSource()).getEndpointUri();
        }
        if (StrUtil.isNotBlank(baseMetadata.getTarget())) {
            if (baseMetadata.getTarget().contains(ThingsConstants.THINGS_SEPARATOR)) {
                return ThingsInstance.valueOf(baseMetadata.getTarget()).getEndpointUri();
            }
            return baseMetadata.getTarget();
        }
        Set<ThingsInstance> instances = thingsInstancesEngine.getProvideInstances(baseMetadata.getProductCode(), baseMetadata.getDeviceCode(), thingsMessage.getMethod());
        if (CollUtil.isNotEmpty(instances)) {
            if (instances.size() > 1) {
                // TODO huangdayu 应当判断是否存在session，否则随机
                return getRandomElement(instances).getEndpointUri();
            }
            return instances.iterator().next().getEndpointUri();
        }
        return thingsInstancesEngine.getThingsInstance().getBrokerUri();
    }


    private static <E> E getRandomElement(Set<E> set) {
        List<E> list = new ArrayList<>(set);
        Random random = new Random();
        int index = random.nextInt(list.size());
        return list.get(index);
    }
}
