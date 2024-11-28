package cn.huangdayu.things.discovery;

import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.api.instances.ThingsInstances;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
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
public class DiscoveryEndpointGetter implements ThingsEndpointGetter {

    private final ThingsInstances thingsInstances;

    @Override
    public EndpointGetterType type() {
        return EndpointGetterType.DISCOVERY;
    }

    @Override
    public Set<String> getPublishUris(JsonThingsMessage thingsMessage) {
        Set<String> set = new LinkedHashSet<>();
        BaseThingsMetadata baseMetadata = thingsMessage.getBaseMetadata();
        if (thingsMessage.getMethod().startsWith(ThingsConstants.Methods.EVENT_LISTENER_START_WITH)) {
            Set<ThingsInstance> consumeInstances = thingsInstances.getConsumeInstances(baseMetadata.getProductCode(), baseMetadata.getDeviceCode(), thingsMessage.getMethod());
            if (CollUtil.isNotEmpty(consumeInstances)) {
                set.addAll(consumeInstances.stream().map(v -> StrUtil.isNotBlank(v.getBrokerUri()) ? v.getBrokerUri() : v.getEndpointUri()).collect(Collectors.toSet()));
            }
        }
        if (StrUtil.isNotBlank(thingsInstances.getThingsInstance().getBrokerUri())) {
            set.add(thingsInstances.getThingsInstance().getBrokerUri());
        }
        return set;
    }


    @Override
    public String getInvokeUri(JsonThingsMessage thingsMessage) {
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
        Set<ThingsInstance> instances = thingsInstances.getProvideInstances(baseMetadata.getProductCode(), baseMetadata.getDeviceCode(), thingsMessage.getMethod());
        if (CollUtil.isNotEmpty(instances)) {
            if (instances.size() > 1) {
                // TODO huangdayu 应当判断是否存在session，否则随机
                return getRandomElement(instances).getEndpointUri();
            }
            return instances.iterator().next().getEndpointUri();
        }
        return thingsInstances.getThingsInstance().getBrokerUri();
    }


    private static <E> E getRandomElement(Set<E> set) {
        List<E> list = new ArrayList<>(set);
        Random random = new Random();
        int index = random.nextInt(list.size());
        return list.get(index);
    }
}
