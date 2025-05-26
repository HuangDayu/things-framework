package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.properties.ThingsSofaBusProperties;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.component.rocketmq.RocketMQComponent;
import org.apache.camel.support.DefaultComponent;

import static cn.huangdayu.things.common.enums.ThingsSofaBusType.ROCKETMQ;

/**
 * @author huangdayu
 */
public class RocketSofaBus extends AbstractSofaBus implements ThingsSofaBus {

    public RocketSofaBus(CamelSofaBusConstructor constructor) {
        super(constructor);
    }

    @Override
    public ThingsSofaBusType getType() {
        return ROCKETMQ;
    }


    @Override
    public DefaultComponent buildComponent() {
        ThingsSofaBusProperties properties = constructor.getProperties();
        return ComponentsBuilderFactory.rocketmq()
                .namesrvAddr(properties.getServer())
                .accessKey(properties.getUserName())
                .secretKey(properties.getPassword())
                .consumerGroup(properties.getGroupId()).build();
    }
}
