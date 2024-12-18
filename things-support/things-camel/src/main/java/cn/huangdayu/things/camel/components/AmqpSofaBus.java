package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.common.enums.ThingsComponentType;
import cn.huangdayu.things.common.properties.ThingsComponentProperties;
import lombok.Getter;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.camel.support.DefaultComponent;

import static cn.huangdayu.things.common.enums.ThingsComponentType.AMQP;

/**
 * @author huangdayu
 */
@Getter
public class AmqpSofaBus extends AbstractCamelSofaBus implements ThingsSofaBus {

    public AmqpSofaBus(CamelContext camelContext, ProducerTemplate producerTemplate) {
        super(camelContext, producerTemplate);
    }

    @Override
    public ThingsComponentType getType() {
        return AMQP;
    }

    @Override
    public DefaultComponent buildComponent(ThingsComponentProperties properties) {
        return AMQPComponent.amqpComponent(properties.getServer(), properties.getUserName(), properties.getPassword());
    }


}
