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
@ThingsBean
@RequiredArgsConstructor
public class RestfulComponent extends AbstractComponent<ComponentProperties> {

    private final CamelContext camelContext;
    private Component component;

    @SneakyThrows
    @Override
    void start(ComponentProperties property) {

        ComponentsBuilderFactory.restOpenapi()
                .host(property.getServer())
                .consumes("application/json")
                .produces("application/json")
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
