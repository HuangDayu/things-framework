package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.events.ThingsContainerCancelledEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import com.alipay.sofa.ark.spi.event.biz.AfterBizStopEvent;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaArkBizStoped implements EventHandler<AfterBizStopEvent> {

    private final ThingsEventObserver thingsEventObserver;

    @Override
    public void handleEvent(AfterBizStopEvent event) {
        thingsEventObserver.notifyObservers(new ThingsContainerCancelledEvent(event));
    }


    @Override
    public int getPriority() {
        return 0;
    }
}
