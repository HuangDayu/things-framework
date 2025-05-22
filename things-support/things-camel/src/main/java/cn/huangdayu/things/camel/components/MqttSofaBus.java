package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.properties.ThingsSofaBusProperties;
import lombok.Getter;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.support.DefaultComponent;

import static cn.huangdayu.things.common.enums.ThingsSofaBusType.MQTT;

/**
 * @author huangdayu
 */
@Getter
public class MqttSofaBus extends AbstractSofaBus implements ThingsSofaBus {


    public MqttSofaBus(CamelSofaBusConstructor constructor) {
        super(constructor);
    }

    @Override
    public ThingsSofaBusType getType() {
        return MQTT;
    }


    @Override
    public DefaultComponent buildComponent(ThingsSofaBusProperties properties) {
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
