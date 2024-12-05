package cn.huangdayu.things.discovery;

import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class DiscoveryEndpointGetter implements ThingsEndpointGetter {

    private final ThingsInstancesManager thingsInstancesManager;
    private final ThingsFrameworkProperties thingsFrameworkProperties;

    @Override
    public EndpointGetterType type() {
        return EndpointGetterType.DISCOVERY;
    }


    @Override
    public String getEndpointUri(JsonThingsMessage thingsMessage) {
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
        Set<ThingsInstance> instances = thingsInstancesManager.getProvideInstances(baseMetadata.getProductCode(), baseMetadata.getDeviceCode(), thingsMessage.getMethod());
        if (CollUtil.isNotEmpty(instances)) {
            if (instances.size() > 1) {
                return getRandomElement(instances).getEndpointUri();
            }
            return instances.iterator().next().getEndpointUri();
        }
        return thingsFrameworkProperties.getInstance().getUpstreamUri();
    }


    private static <E> E getRandomElement(Set<E> set) {
        List<E> list = new ArrayList<>(set);
        Random random = new Random();
        int index = random.nextInt(list.size());
        return list.get(index);
    }
}
