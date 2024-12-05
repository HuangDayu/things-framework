package cn.huangdayu.things.gateway;

import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.dto.ThingsInfo;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsGatewayEndpoint implements ThingsEndpoint {

    private final ThingsFrameworkProperties thingsFrameworkProperties;
    private final ThingsInstancesManager thingsInstancesManager;
    private final ThingsEndpointFactory thingsEndpointFactory;

    @Override
    public Set<ThingsInfo> getThingsDsl() {
        Set<ThingsInfo> thingsDsl = new HashSet<>();
        for (ThingsInstance instance : thingsInstancesManager.getAllThingsInstances()) {
            thingsDsl.addAll(thingsEndpointFactory.create(instance.getEndpointUri()).getThingsDsl());
        }
        return thingsDsl;
    }

    @Override
    public JsonThingsMessage send(JsonThingsMessage message) {
        return thingsEndpointFactory.create(message).send(message);
    }

    @Override
    public void publish(JsonThingsMessage message) {
        // TODO 外部订阅的事件消息也要推送出去
        Set<String> publishUris = getConsumeEndpointUris(message);
        if (CollUtil.isNotEmpty(publishUris)) {
            for (String publishUri : publishUris) {
                thingsEndpointFactory.create(publishUri).publish(message);
            }
        }
    }

    private Set<String> getConsumeEndpointUris(JsonThingsMessage message) {
        Set<String> set = new LinkedHashSet<>();
        BaseThingsMetadata baseMetadata = message.getBaseMetadata();
        if (message.getMethod().startsWith(ThingsConstants.Methods.EVENT_LISTENER_START_WITH)) {
            Set<ThingsInstance> consumeInstances = thingsInstancesManager.getConsumeInstances(baseMetadata.getProductCode(), baseMetadata.getDeviceCode(), message.getMethod());
            if (CollUtil.isNotEmpty(consumeInstances)) {
                set.addAll(consumeInstances.stream().map(v -> StrUtil.isNotBlank(v.getUpstreamUri()) ? v.getUpstreamUri() : v.getEndpointUri()).collect(Collectors.toSet()));
            }
        }
        return set;
    }

    @Override
    public ThingsInstance exchange(ThingsInstance thingsInstance) {
        return thingsInstancesManager.exchangeInstance(thingsInstance);
    }
}
