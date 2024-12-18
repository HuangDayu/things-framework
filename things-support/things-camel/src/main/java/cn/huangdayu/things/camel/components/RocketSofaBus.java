package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.common.enums.ThingsComponentType;
import cn.huangdayu.things.common.properties.ThingsComponentProperties;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.support.DefaultComponent;

import static cn.huangdayu.things.common.enums.ThingsComponentType.ROCKETMQ;

/**
 * @author huangdayu
 */
public class RocketSofaBus extends AbstractCamelSofaBus implements ThingsSofaBus {

    public RocketSofaBus(CamelContext camelContext, ProducerTemplate producerTemplate) {
        super(camelContext, producerTemplate);
    }

    @Override
    public ThingsComponentType getType() {
        return ROCKETMQ;
    }


    @Override
    public DefaultComponent buildComponent(ThingsComponentProperties properties) {
        return ComponentsBuilderFactory.rocketmq()
                .namesrvAddr(properties.getServer())
                .accessKey(properties.getUserName())
                .secretKey(properties.getPassword())
                .consumerGroup(properties.getGroupId()).build();
    }
}
