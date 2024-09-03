package cn.huangdayu.things.gateway.components;

import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Component
public class ComponentsFactory {

    private ProducerTemplate producerTemplate;
    private final CamelContext camelContext;

    public ProducerTemplate getProducerTemplate() {
        if (producerTemplate == null) {
            producerTemplate = camelContext.createProducerTemplate();
        }
        return producerTemplate;
    }

//    public void get() {
//        producerTemplate.sendBody("kafka:things-gateway", "hello");
//    }

}
