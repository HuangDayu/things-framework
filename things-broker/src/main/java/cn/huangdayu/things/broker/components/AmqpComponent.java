package cn.huangdayu.things.broker.components;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.amqp.AMQPComponent;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class AmqpComponent extends AbstractComponent<ComponentProperties> {
    private final CamelContext camelContext;
    private Component component;


    @Override
    @SneakyThrows
    void start(ComponentProperties property) {

        AMQPComponent amqpComponent = AMQPComponent.amqpComponent(property.getServer(), property.getUserName(), property.getPassword());

        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(property.getName() + ":" + property.getTopic())
                        .to(TARGET_ROUTER);
            }
        });

        camelContext.addComponent(property.getName(), amqpComponent);

        component = camelContext.getComponent(property.getName());
        component.start();
    }

    @Override
    void stop() {
        component.stop();
    }

    @Override
    void output(JsonThingsMessage jtm) {

    }
}
