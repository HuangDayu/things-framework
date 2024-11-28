package cn.huangdayu.things.gateway;

import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.api.instances.ThingsInstances;
import cn.huangdayu.things.api.restful.ThingsEndpoint;
import cn.huangdayu.things.api.session.ThingsSessions;
import cn.huangdayu.things.common.dto.ThingsInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static cn.huangdayu.things.common.factory.RestfulClientFactory.createRestClient;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@RestController
@RequestMapping
public class ThingsGatewayController implements ThingsEndpoint {

    private final ThingsSessions thingsSessions;
    private final ThingsProperties thingsProperties;
    private final ThingsInstances thingsInstances;
    private final Map<String, ThingsEndpointGetter> thingsEndpointGetterMap;

    @Override
    public Set<ThingsInfo> getThingsDsl() {
        Set<ThingsInfo> thingsDsl = new HashSet<>();
        for (ThingsInstance instance : thingsInstances.getAllThingsInstances()) {
            thingsDsl.addAll(createRestClient(ThingsEndpoint.class, instance.getEndpointUri()).getThingsDsl());
        }
        return thingsDsl;
    }

    @Override
    public JsonThingsMessage handler(JsonThingsMessage message) {

        return null;
    }

    @Override
    public ThingsInstance exchangeInstance(ThingsInstance thingsInstance) {
        return thingsInstances.exchangeInstance(thingsInstance);
    }
}
