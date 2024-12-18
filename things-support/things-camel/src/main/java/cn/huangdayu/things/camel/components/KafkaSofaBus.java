package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.common.enums.ThingsComponentType;
import cn.huangdayu.things.common.properties.ThingsComponentProperties;
import lombok.Getter;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.support.DefaultComponent;

import static cn.huangdayu.things.common.enums.ThingsComponentType.KAFKA;

/**
 * @author huangdayu
 */
@Getter
public class KafkaSofaBus extends AbstractCamelSofaBus implements ThingsSofaBus {

    public KafkaSofaBus(CamelContext camelContext, ProducerTemplate producerTemplate) {
        super(camelContext, producerTemplate);
    }

    @Override
    public ThingsComponentType getType() {
        return KAFKA;
    }

    @Override
    public DefaultComponent buildComponent(ThingsComponentProperties properties) {
        return ComponentsBuilderFactory.kafka()
                .brokers(properties.getServer())
                .clientId(properties.getClientId())
                .groupId(properties.getGroupId())
                .autoCommitEnable(true)
                .autoCommitIntervalMs(1000).build();
    }

}
