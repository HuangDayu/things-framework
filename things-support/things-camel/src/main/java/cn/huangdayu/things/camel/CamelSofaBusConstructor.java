package cn.huangdayu.things.camel;

import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

/**
 * @author huangdayu
 */
@Getter
@RequiredArgsConstructor
public class CamelSofaBusConstructor {

    private final CamelContext camelContext;
    private final ProducerTemplate producerTemplate;
    private final ThingsEngineProperties.ThingsSofaBusProperties properties;

}
