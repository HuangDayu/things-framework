package cn.huangdayu.things.rest.getter;

import cn.huangdayu.things.rest.instances.ThingsInstancesProvider;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.rest.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.huangdayu.things.rest.endpoint.ThingsEndpointGetter;
import cn.hutool.core.collection.CollUtil;
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

    private final ThingsInstancesProvider thingsInstancesProvider;

    @Override
    public EndpointGetterType type() {
        return EndpointGetterType.SERVICE_PROVIDE;
    }


    @Override
    public String getEndpointUri(JsonThingsMessage jtm) {
        Set<ThingsInstance> instances = thingsInstancesProvider.getProvides(jtm);
        if (CollUtil.isNotEmpty(instances)) {
            if (instances.size() > 1) {
                return getRandomElement(instances).getEndpointUri();
            }
            return instances.iterator().next().getEndpointUri();
        }
        return null;
    }


    private static <E> E getRandomElement(Set<E> set) {
        List<E> list = new ArrayList<>(set);
        Random random = new Random();
        int index = random.nextInt(list.size());
        return list.get(index);
    }
}
