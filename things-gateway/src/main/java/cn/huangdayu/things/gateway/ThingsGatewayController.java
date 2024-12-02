package cn.huangdayu.things.gateway;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.api.instances.ThingsInstanceManager;
import cn.huangdayu.things.api.restful.ThingsEndpoint;
import cn.huangdayu.things.api.session.ThingsSessions;
import cn.huangdayu.things.common.dto.ThingsInfo;
import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.common.utils.ThingsUtils.findFirst;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@RestController
@RequestMapping
public class ThingsGatewayController implements ThingsEndpoint {

    private final ThingsSessions thingsSessions;
    private final ThingsFrameworkProperties thingsFrameworkProperties;
    private final ThingsInstanceManager thingsInstanceManager;
    private final ThingsEndpointFactory thingsEndpointFactory;
    private final Map<String, ThingsEndpointGetter> thingsEndpointGetterMap;
    private Map<EndpointGetterType, ThingsEndpointGetter> typeThingsEndpointGetterMap;

    @PostConstruct
    private void init() {
        typeThingsEndpointGetterMap = thingsEndpointGetterMap.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(getterEntry -> getterEntry.getValue().type(), Map.Entry::getValue));
    }

    @Override
    public Set<ThingsInfo> getThingsDsl() {
        Set<ThingsInfo> thingsDsl = new HashSet<>();
        for (ThingsInstance instance : thingsInstanceManager.getAllThingsInstances()) {
            thingsDsl.addAll(thingsEndpointFactory.create(ThingsEndpoint.class, instance.getEndpointUri()).getThingsDsl());
        }
        return thingsDsl;
    }

    @Override
    public JsonThingsMessage send(JsonThingsMessage message) {
        String sendUri = findFirst(() -> typeThingsEndpointGetterMap.get(EndpointGetterType.SESSION).getSendUri(message),
                () -> typeThingsEndpointGetterMap.get(EndpointGetterType.DISCOVERY).getSendUri(message));
        if (StrUtil.isNotBlank(sendUri)) {
            return thingsEndpointFactory.create(ThingsEndpoint.class, sendUri).send(message);
        }
        throw new ThingsException(message, BAD_REQUEST, "Things message error.");
    }

    @Override
    public void publish(JsonThingsMessage message) {
        Set<String> publishUris = typeThingsEndpointGetterMap.get(EndpointGetterType.DISCOVERY).getPublishUris(message);
        if (CollUtil.isNotEmpty(publishUris)) {
            for (String publishUri : publishUris) {
                thingsEndpointFactory.create(ThingsEndpoint.class, publishUri).publish(message);
            }
        }
    }

    @Override
    public ThingsInstance exchange(ThingsInstance thingsInstance) {
        return thingsInstanceManager.exchangeInstance(thingsInstance);
    }
}
