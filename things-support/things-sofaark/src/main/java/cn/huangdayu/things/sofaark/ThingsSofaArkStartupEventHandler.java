package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.api.container.ThingsRegister;
import com.alipay.sofa.ark.spi.event.biz.AfterBizStartupEvent;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import com.alipay.sofa.koupleless.common.BizRuntimeContextRegistry;
import com.alipay.sofa.koupleless.common.model.ApplicationContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static cn.huangdayu.things.sofaark.ThingsSofaArkContainer.ARK_CONTAINER_MAP;

/**
 * @author huangdayu
 */
@Component
@RequiredArgsConstructor
public class ThingsSofaArkStartupEventHandler implements EventHandler<AfterBizStartupEvent> {

    private final ThingsRegister thingsRegister;


    @Override
    public void handleEvent(AfterBizStartupEvent event) {
        ApplicationContextHolder rootApplicationContext = BizRuntimeContextRegistry.getBizRuntimeContext(event.getSource()).getApplicationContext();
        if (rootApplicationContext != null) {
            ThingsSofaArkContainer thingsSofaArkContainer = new ThingsSofaArkContainer((ApplicationContext) rootApplicationContext.get());
            thingsRegister.register(thingsSofaArkContainer);
            ARK_CONTAINER_MAP.put(event.getSource(), thingsSofaArkContainer);
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
