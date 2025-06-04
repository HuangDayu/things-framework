package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.api.container.ThingsRegister;
import com.alipay.sofa.ark.spi.event.biz.AfterBizStopEvent;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cn.huangdayu.things.sofaark.ThingsSofaArkContainer.ARK_CONTAINER_MAP;

/**
 * @author huangdayu
 */
@Component
@RequiredArgsConstructor
public class ThingsSofaArkStopEventHandler implements EventHandler<AfterBizStopEvent> {

    private final ThingsRegister thingsRegister;

    @Override
    public void handleEvent(AfterBizStopEvent event) {
        ThingsSofaArkContainer thingsSofaArkContainer = ARK_CONTAINER_MAP.get(event.getSource());
        if (thingsSofaArkContainer != null) {
            thingsRegister.cancel(thingsSofaArkContainer);
            ARK_CONTAINER_MAP.remove(event.getSource());
        }
    }


    @Override
    public int getPriority() {
        return 0;
    }
}
