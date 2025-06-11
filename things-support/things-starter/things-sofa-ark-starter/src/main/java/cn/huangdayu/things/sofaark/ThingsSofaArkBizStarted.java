package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.events.ThingsContainerRegisteredEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import com.alipay.sofa.ark.spi.event.AfterFinishStartupEvent;
import com.alipay.sofa.ark.spi.event.biz.AfterBizStartupEvent;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaArkBizStarted implements EventHandler<AfterBizStartupEvent> {

    private final ThingsEventObserver thingsEventObserver;


    @Override
    public void handleEvent(AfterBizStartupEvent event) {
        thingsEventObserver.notifyObservers(new ThingsContainerRegisteredEvent(event));
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
