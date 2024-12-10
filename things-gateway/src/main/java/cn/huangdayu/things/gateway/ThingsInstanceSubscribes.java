package cn.huangdayu.things.gateway;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstancesSubscriber;
import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.utils.ThingsUtils.covertEventMessage;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsInstanceSubscribes implements ThingsPublisher {

    private final ThingsInstancesSubscriber thingsInstancesSubscriber;
    private final ThingsEndpointFactory thingsEndpointFactory;
    private final ThingsFrameworkProperties thingsFrameworkProperties;

    @Override
    public void publishEvent(ThingsEventMessage tem) {
        publishEvent(covertEventMessage(tem));
    }

    @Override
    public void publishEvent(JsonThingsMessage jtm) {
        Set<String> publishUris = getConsumeEndpointUris(jtm);
        if (CollUtil.isNotEmpty(publishUris)) {
            for (String publishUri : publishUris) {
                thingsEndpointFactory.create(publishUri).handleEvent(jtm);
            }
        }
    }

    private Set<String> getConsumeEndpointUris(JsonThingsMessage jtm) {
        Set<String> set = new LinkedHashSet<>();
        Set<ThingsInstance> consumeInstances = thingsInstancesSubscriber.getSubscribes(jtm);
        if (CollUtil.isNotEmpty(consumeInstances)) {
            set.addAll(consumeInstances.stream().map(v -> StrUtil.isNotBlank(v.getUpstreamUri()) && !v.getUpstreamUri().equals(thingsFrameworkProperties.getInstance().getEndpointUri())
                    ? v.getUpstreamUri() : v.getEndpointUri()).collect(Collectors.toSet()));
        }
        return set;
    }
}
