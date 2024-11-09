package cn.huangdayu.things.broker.components;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.builder.component.dsl.VertxWebsocketComponentBuilderFactory;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class WebSocketComponent extends AbstractComponent<ComponentProperties> {

    private final CamelContext camelContext;
    private Component component;

    @SneakyThrows
    @Override
    void start(ComponentProperties property) {

        String[] split = property.getServer().split(":");
        VertxWebsocketComponentBuilderFactory.VertxWebsocketComponentBuilder websocketComponentBuilder = ComponentsBuilderFactory.vertxWebsocket()
                .defaultHost(split[0])
                .defaultPort(Integer.parseInt(split[1]))
                .originHeaderUrl(property.getTopic());

        websocketComponentBuilder.register(camelContext, property.getName());


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
