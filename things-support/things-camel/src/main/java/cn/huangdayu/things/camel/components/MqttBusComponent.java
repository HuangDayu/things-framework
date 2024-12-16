package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.component.ThingsBusComponent;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsComponentType;
import cn.huangdayu.things.common.properties.ThingsComponentProperties;
import lombok.Getter;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.support.DefaultComponent;

import static cn.huangdayu.things.common.enums.ThingsComponentType.MQTT;

/**
 * @author huangdayu
 */
@Getter
public class MqttBusComponent extends AbstractCamelComponent implements ThingsBusComponent {


    public MqttBusComponent(CamelContext camelContext, ProducerTemplate producerTemplate) {
        super(camelContext, producerTemplate);
    }

    @Override
    public ThingsComponentType getType() {
        return MQTT;
    }


    @Override
    public DefaultComponent buildComponent(ThingsComponentProperties properties) {
        return ComponentsBuilderFactory.pahoMqtt5()
                .brokerUrl(properties.getServer())
                .userName(properties.getUserName())
                .password(properties.getPassword())
                .clientId(properties.getClientId())
                .automaticReconnect(true)
                .qos(2)
                .keepAliveInterval(60).build();
    }
}
