package cn.huangdayu.things.camel;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusCreator;
import cn.huangdayu.things.camel.components.AmqpSofaBus;
import cn.huangdayu.things.camel.components.KafkaSofaBus;
import cn.huangdayu.things.camel.components.MqttSofaBus;
import cn.huangdayu.things.camel.components.RocketSofaBus;
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
public class CamelSofaBusCreator implements ThingsSofaBusCreator {
    private final CamelContext camelContext;
    public static final Map<ThingsComponentType, BiFunction<CamelContext, ProducerTemplate, ThingsSofaBus>> componentsMap = new ConcurrentHashMap<>();
    private volatile static ProducerTemplate producerTemplate;

    @PostConstruct
    public void init() {
        componentsMap.put(ThingsComponentType.KAFKA, KafkaSofaBus::new);
        componentsMap.put(ThingsComponentType.AMQP, AmqpSofaBus::new);
        componentsMap.put(ThingsComponentType.MQTT, MqttSofaBus::new);
        componentsMap.put(ThingsComponentType.ROCKETMQ, RocketSofaBus::new);
    }

    @Override
    public Set<ThingsComponentType> supports() {
        return Arrays.stream(ThingsComponentType.values()).collect(Collectors.toSet());
    }

    @Override
    public ThingsSofaBus create(ThingsComponentProperties property) {
        BiFunction<CamelContext, ProducerTemplate, ThingsSofaBus> function = componentsMap.get(property.getType());
        ThingsSofaBus thingsSofaBus = function.apply(camelContext, getProducerTemplate());
        thingsSofaBus.init(property);
        return thingsSofaBus;
    }

    public ProducerTemplate getProducerTemplate() {
        if (producerTemplate == null) {
            synchronized (CamelSofaBusCreator.class) {
                if (producerTemplate == null) {
                    producerTemplate = camelContext.createProducerTemplate();
                }
            }
        }
        return producerTemplate;
    }
}
