package cn.huangdayu.things.component;

import cn.huangdayu.things.api.component.ThingsBusComponent;
import cn.huangdayu.things.api.component.ThingsBusComponentCreator;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsComponentProperties;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsBusComponentFactory {

    private final Map<String, ThingsBusComponentCreator> thingsBusComponentCreators;
    private static final Map<ThingsComponentProperties, ThingsBusComponent> propertiesComponentsMap = new ConcurrentHashMap<>();


    public ThingsBusComponent create(ThingsComponentProperties property) {
        return propertiesComponentsMap.computeIfAbsent(property, k -> {
            for (Map.Entry<String, ThingsBusComponentCreator> entry : thingsBusComponentCreators.entrySet()) {
                if (entry.getValue().supports().contains(property.getType())) {
                    return entry.getValue().create(property);
                }
            }
            return null;
        });
    }


    public boolean destroy(ThingsComponentProperties property) {
        ThingsBusComponent thingsBusComponent = propertiesComponentsMap.get(property);
        if (thingsBusComponent != null) {
            return thingsBusComponent.stop();
        }
        return false;
    }

    @PreDestroy
    public void destroy() {
        propertiesComponentsMap.forEach((key, value) -> value.stop());
    }
}
