package cn.huangdayu.things.camel;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusCreator;
import cn.huangdayu.things.camel.components.AmqpSofaBus;
import cn.huangdayu.things.camel.components.KafkaSofaBus;
import cn.huangdayu.things.camel.components.MqttSofaBus;
import cn.huangdayu.things.camel.components.RocketSofaBus;
import cn.huangdayu.things.camel.converter.AbstractTypeConverter;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class CamelSofaBusCreator implements ThingsSofaBusCreator {
    private final CamelContext camelContext;
    public static final Map<ThingsSofaBusType, Function<CamelSofaBusConstructor, ThingsSofaBus>> componentsMap = new ConcurrentHashMap<>();
    private volatile static ProducerTemplate producerTemplate;
    private final Map<String, AbstractTypeConverter> typeConverterMap;


    @PostConstruct
    public void init() {
        componentsMap.put(ThingsSofaBusType.KAFKA, KafkaSofaBus::new);
        componentsMap.put(ThingsSofaBusType.AMQP, AmqpSofaBus::new);
        componentsMap.put(ThingsSofaBusType.MQTT, MqttSofaBus::new);
        componentsMap.put(ThingsSofaBusType.ROCKETMQ, RocketSofaBus::new);
        registryConverter();
    }

    private void registryConverter() {
        if (typeConverterMap != null) {
            for (Map.Entry<String, AbstractTypeConverter> entry : typeConverterMap.entrySet()) {
                AbstractTypeConverter converter = entry.getValue();
                camelContext.getTypeConverterRegistry().addTypeConverter(converter.toType(), converter.fromType(), converter.typeConverter());
            }
        }
    }

    @Override
    public Set<ThingsSofaBusType> supports() {
        return Arrays.stream(ThingsSofaBusType.values()).collect(Collectors.toSet());
    }

    @Override
    public ThingsSofaBus create(ThingsEngineProperties.ThingsSofaBusProperties property) {
        Function<CamelSofaBusConstructor, ThingsSofaBus> function = componentsMap.get(property.getType());
        return function.apply(new CamelSofaBusConstructor(camelContext, getProducerTemplate(), property));
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
