package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.properties.ThingsSofaBusProperties;
import lombok.Getter;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.camel.support.DefaultComponent;

import static cn.huangdayu.things.common.enums.ThingsSofaBusType.AMQP;

/**
 * @author huangdayu
 */
@Getter
public class AmqpSofaBus extends AbstractCamelSofaBus implements ThingsSofaBus {


    public AmqpSofaBus(CamelSofaBusConstructor constructor) {
        super(constructor);
    }

    @Override
    public ThingsSofaBusType getType() {
        return AMQP;
    }

    @Override
    public DefaultComponent buildComponent(ThingsSofaBusProperties properties) {
        return AMQPComponent.amqpComponent(properties.getServer(), properties.getUserName(), properties.getPassword());
    }


}
