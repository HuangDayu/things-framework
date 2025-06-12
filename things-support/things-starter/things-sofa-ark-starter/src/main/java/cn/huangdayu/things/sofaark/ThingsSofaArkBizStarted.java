package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.events.ThingsContainerRegisteredEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.spi.event.biz.AfterBizStartupEvent;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.sofaark.ThingsSofaArkUtils.getBizContext;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaArkBizStarted implements EventHandler<AfterBizStartupEvent> {

    private final ThingsEventObserver thingsEventObserver;


    @Override
    public void handleEvent(AfterBizStartupEvent event) {
        if (event.getSource() != ArkClient.getMasterBiz()) {
            ThingsContainer thingsContainer = getBizContext(event.getSource()).getBean(ThingsContainer.class);
            thingsEventObserver.notifyObservers(new ThingsContainerRegisteredEvent(event.getSource(), thingsContainer));
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
