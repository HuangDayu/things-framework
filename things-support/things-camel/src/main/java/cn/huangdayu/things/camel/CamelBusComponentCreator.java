package cn.huangdayu.things.camel;

import cn.huangdayu.things.api.component.ThingsBusComponent;
import cn.huangdayu.things.api.component.ThingsBusComponentCreator;
import cn.huangdayu.things.camel.components.AmqpBusComponent;
import cn.huangdayu.things.camel.components.KafkaBusComponent;
import cn.huangdayu.things.camel.components.MqttBusComponent;
import cn.huangdayu.things.camel.components.RocketBusComponent;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsComponentType;
import cn.huangdayu.things.common.properties.ThingsComponentProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class CamelBusComponentCreator implements ThingsBusComponentCreator {
    private final CamelContext camelContext;
    public static final Map<ThingsComponentType, BiFunction<CamelContext, ProducerTemplate, ThingsBusComponent>> componentsMap = new ConcurrentHashMap<>();
    private volatile static ProducerTemplate producerTemplate;

    @PostConstruct
    public void init() {
        componentsMap.put(ThingsComponentType.KAFKA, KafkaBusComponent::new);
        componentsMap.put(ThingsComponentType.AMQP, AmqpBusComponent::new);
        componentsMap.put(ThingsComponentType.MQTT, MqttBusComponent::new);
        componentsMap.put(ThingsComponentType.ROCKETMQ, RocketBusComponent::new);
    }

    @Override
    public Set<ThingsComponentType> supports() {
        return Arrays.stream(ThingsComponentType.values()).collect(Collectors.toSet());
    }

    @Override
    public ThingsBusComponent create(ThingsComponentProperties property) {
        BiFunction<CamelContext, ProducerTemplate, ThingsBusComponent> function = componentsMap.get(property.getType());
        ThingsBusComponent thingsBusComponent = function.apply(camelContext, getProducerTemplate());
        thingsBusComponent.init(property);
        return thingsBusComponent;
    }

    public ProducerTemplate getProducerTemplate() {
        if (producerTemplate == null) {
            synchronized (CamelBusComponentCreator.class) {
                if (producerTemplate == null) {
                    producerTemplate = camelContext.createProducerTemplate();
                }
            }
        }
        return producerTemplate;
    }
}
