package cn.huangdayu.things.gateway.manager;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ComponentsManager {

    private final CamelContext camelContext;

    private ProducerTemplate producerTemplate;

    @PostConstruct
    public void init() {
        producerTemplate = camelContext.createProducerTemplate();
        //        producerTemplate.sendBody("kafka:things-gateway", "hello");

    }




}
