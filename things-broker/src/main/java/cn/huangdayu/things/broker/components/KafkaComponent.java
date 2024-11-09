package cn.huangdayu.things.broker.components;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.component.ComponentsBuilderFactory;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class KafkaComponent extends AbstractComponent<ComponentProperties> {

    private final CamelContext camelContext;
    private Component component;


    @Override
    @SneakyThrows
    void start(ComponentProperties property) {


        ComponentsBuilderFactory.kafka()
                .brokers(property.getServer())
                .clientId(property.getClientId())
                .groupId(property.getGroupId())
                .autoCommitEnable(true)
                .autoCommitIntervalMs(1000)
                .register(camelContext, property.getName());

        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(property.getName() + ":" + property.getTopic())
                        .to(TARGET_ROUTER);
            }
        });

        component = camelContext.getComponent(property.getName());
        component.start();
    }

    @Override
    void stop() {
        component.stop();
    }

    @Override
    void output(JsonThingsMessage jsonThingsMessage) {

    }
}
