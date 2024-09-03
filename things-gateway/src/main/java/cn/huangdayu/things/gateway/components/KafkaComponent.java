package cn.huangdayu.things.gateway.components;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.springframework.stereotype.Component;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Component
public class KafkaComponent extends AbstractComponent<ComponentProperty> {

    private final CamelContext camelContext;


    @Override
    @SneakyThrows
    void start(ComponentProperty property) {
        org.apache.camel.Component component = camelContext.hasComponent(property.getName());
        if (component != null) {
            return;
        }

        ComponentsBuilderFactory.kafka()
                .brokers(property.getServer())
                .groupId(property.getGroupId())
                .autoCommitEnable(true)
                .autoCommitIntervalMs(1000)
                .register(camelContext, property.getName());

        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("kafka:things-gateway")
                        .routeId("kafka-things-gateway-route")
                        .log("Received message: ${body}")
                        .to("direct:things-message-router");
            }
        });

        camelContext.getComponent(property.getName()).start();

    }
}
