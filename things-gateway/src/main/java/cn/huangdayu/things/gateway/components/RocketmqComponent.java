package cn.huangdayu.things.gateway.components;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.component.ComponentsBuilderFactory;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class RocketmqComponent extends AbstractComponent<ComponentProperties> {


    private final CamelContext camelContext;
    private Component component;

    @Override
    @SneakyThrows
    void start(ComponentProperties property) {


        ComponentsBuilderFactory.rocketmq()
                .namesrvAddr(property.getServer())
                .accessKey(property.getUserName())
                .secretKey(property.getPassword())
                .consumerGroup(property.getGroupId())
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
