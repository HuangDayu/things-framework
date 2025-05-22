package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.properties.ThingsSofaBusProperties;
import lombok.Getter;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.support.DefaultComponent;

import static cn.huangdayu.things.common.enums.ThingsSofaBusType.KAFKA;

/**
 * @author huangdayu
 */
@Getter
public class KafkaSofaBus extends AbstractSofaBus implements ThingsSofaBus {

    public KafkaSofaBus(CamelSofaBusConstructor constructor) {
        super(constructor);
    }

    @Override
    public ThingsSofaBusType getType() {
        return KAFKA;
    }

    @Override
    public DefaultComponent buildComponent(ThingsSofaBusProperties properties) {
        return ComponentsBuilderFactory.kafka()
                .brokers(properties.getServer())
                .clientId(properties.getClientId())
                .groupId(properties.getGroupId())
                .autoCommitEnable(true)
                .autoCommitIntervalMs(1000).build();
    }

}
