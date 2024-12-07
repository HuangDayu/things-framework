package cn.huangdayu.things.gateway;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
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

    private final ThingsInstancesManager thingsInstancesManager;
    private final ThingsEndpointFactory thingsEndpointFactory;

    @Override
    public void publishEvent(ThingsEventMessage thingsEventMessage) {
        publishEvent(covertEventMessage(thingsEventMessage));
    }

    @Override
    public void publishEvent(JsonThingsMessage jsonThingsMessage) {
        Set<String> publishUris = getConsumeEndpointUris(jsonThingsMessage);
        if (CollUtil.isNotEmpty(publishUris)) {
            for (String publishUri : publishUris) {
                thingsEndpointFactory.create(publishUri).handleEvent(jsonThingsMessage);
            }
        }
    }

    private Set<String> getConsumeEndpointUris(JsonThingsMessage message) {
        Set<String> set = new LinkedHashSet<>();
        BaseThingsMetadata baseMetadata = message.getBaseMetadata();
        if (message.getMethod().startsWith(ThingsConstants.Methods.EVENT_LISTENER_START_WITH)) {
            Set<ThingsInstance> consumeInstances = thingsInstancesManager.getSubscribeInstances(baseMetadata.getProductCode(), baseMetadata.getDeviceCode(), message.getMethod());
            if (CollUtil.isNotEmpty(consumeInstances)) {
                set.addAll(consumeInstances.stream().map(v -> StrUtil.isNotBlank(v.getUpstreamUri()) ? v.getUpstreamUri() : v.getEndpointUri()).collect(Collectors.toSet()));
            }
        }
        return set;
    }
}
