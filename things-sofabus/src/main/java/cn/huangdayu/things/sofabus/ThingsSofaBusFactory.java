package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusCreator;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsSofaBusProperties;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsSofaBusFactory {

    private final Map<String, ThingsSofaBusCreator> thingsBusComponentCreators;
    private static final Map<ThingsSofaBusProperties, ThingsSofaBus> propertiesComponentsMap = new ConcurrentHashMap<>();


    public ThingsSofaBus create(ThingsSofaBusProperties property, ThingsChaining thingsChaining) {
        return propertiesComponentsMap.computeIfAbsent(property, k -> {
            for (Map.Entry<String, ThingsSofaBusCreator> entry : thingsBusComponentCreators.entrySet()) {
                if (entry.getValue().supports().contains(property.getType())) {
                    return entry.getValue().create(property, thingsChaining);
                }
            }
            return null;
        });
    }


    public boolean destroy(ThingsSofaBusProperties property) {
        ThingsSofaBus thingsSofaBus = propertiesComponentsMap.get(property);
        if (thingsSofaBus != null) {
            return thingsSofaBus.stop();
        }
        return false;
    }

    @PreDestroy
    public void destroy() {
        propertiesComponentsMap.forEach((key, value) -> value.stop());
    }

    public Set<ThingsSofaBus> getThingsSofaBus() {
        return new HashSet<>(propertiesComponentsMap.values());
    }
}
