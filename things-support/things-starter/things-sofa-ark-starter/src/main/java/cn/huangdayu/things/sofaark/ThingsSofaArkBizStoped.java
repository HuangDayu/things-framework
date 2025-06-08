package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.api.container.ThingsRegister;
import cn.huangdayu.things.common.annotation.ThingsBean;
import com.alipay.sofa.ark.spi.event.biz.AfterBizStopEvent;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.sofaark.ThingsSofaArkContainer.ARK_CONTAINER_MAP;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaArkBizStoped implements EventHandler<AfterBizStopEvent> {

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
