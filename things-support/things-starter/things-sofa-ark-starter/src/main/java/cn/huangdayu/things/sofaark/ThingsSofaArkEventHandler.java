package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.common.events.ThingsContainerCancelledEvent;
import cn.huangdayu.things.common.events.ThingsContainerRegisteredEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import com.alipay.sofa.ark.spi.event.ArkEvent;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author huangdayu
 */
@Component
@RequiredArgsConstructor
public class ThingsSofaArkEventHandler implements EventHandler<ArkEvent> {

    private final ThingsEventObserver thingsEventObserver;

    @Override
    public void handleEvent(ArkEvent event) {
        if (event.getTopic().equals(ThingsContainerRegisteredEvent.class.getName())) {
            thingsEventObserver.notifyObservers(new ThingsContainerRegisteredEvent(event));
        }
        if (event.getTopic().equals(ThingsContainerCancelledEvent.class.getName())) {
            thingsEventObserver.notifyObservers(new ThingsContainerCancelledEvent(event));
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
