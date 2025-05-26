package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.properties.ThingsSofaBusProperties;
import lombok.Getter;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.camel.support.DefaultComponent;

import static cn.huangdayu.things.common.enums.ThingsSofaBusType.AMQP;

/**
 * @author huangdayu
 */
@Getter
public class AmqpSofaBus extends AbstractSofaBus implements ThingsSofaBus {


    public AmqpSofaBus(CamelSofaBusConstructor constructor) {
        super(constructor);
    }

    @Override
    public ThingsSofaBusType getType() {
        return AMQP;
    }

    @Override
    public DefaultComponent buildComponent() {
        ThingsSofaBusProperties properties = constructor.getProperties();
        return ComponentsBuilderFactory.amqp()
                .clientId(properties.getClientId())
                .host(properties.getServer().split(":")[0])
                .port(Integer.parseInt(properties.getServer().split(":")[1]))
                .username(properties.getUserName())
                .password(properties.getPassword()).build();
    }


}
