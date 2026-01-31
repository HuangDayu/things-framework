package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import lombok.Getter;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.support.DefaultComponent;
import org.eclipse.californium.core.CoapClient;

import static cn.huangdayu.things.common.enums.ThingsSofaBusType.COAP;

/**
 * @author huangdayu
 * <a>https://camel.apache.org/components/4.14.x/coap-component.html#_spring_boot_auto_configuration</a>
 * <a>https://camel.zulipchat.com/#narrow/channel/257298-camel/topic/Support.20for.20Shared.20CoAP.20Client.20Instance/with/570270000</a>
 */
@Getter
public class CoapClientSofaBus extends ComponentSofaBus implements ThingsSofaBus {

    @Override
    protected DefaultComponent buildComponent() {
        return ComponentsBuilderFactory.coap()
                .bridgeErrorHandler(true)
                .lazyStartProducer(false)
                .autowiredEnabled(true)
                .configurationFile("classpath:coap.properties")
                .build();
    }

    public CoapClientSofaBus(CamelSofaBusConstructor constructor) {
        super(constructor);
    }

    @Override
    public ThingsSofaBusType getType() {
        return COAP;
    }


}