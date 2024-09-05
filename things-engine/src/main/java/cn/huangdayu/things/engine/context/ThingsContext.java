package cn.huangdayu.things.engine.context;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.core.ThingsContainerEngine;
import cn.huangdayu.things.engine.wrapper.ThingsContainer;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsContext {

    private final ThingsContainerEngine thingsContainerEngine;

    private final static Map<String, ThingsContainer> THINGS_CONTAINERS = new ConcurrentHashMap<>();

    public void register(ThingsContainer thingsContainer) {
        thingsContainerEngine.register(thingsContainer);
        THINGS_CONTAINERS.put(thingsContainer.name(), thingsContainer);
    }

    public void cancel(ThingsContainer thingsContainer) {
        thingsContainerEngine.cancel(thingsContainer);
        THINGS_CONTAINERS.remove(thingsContainer.name());
    }

    public static <T> T getBean(Class<T> requiredType) {
        for (Map.Entry<String, ThingsContainer> entry : THINGS_CONTAINERS.entrySet()) {
            try {
                return entry.getValue().getBean(requiredType);
            } catch (Exception ignored) {
            }
        }
        throw new RuntimeException("No bean found for type " + requiredType);
    }

}
