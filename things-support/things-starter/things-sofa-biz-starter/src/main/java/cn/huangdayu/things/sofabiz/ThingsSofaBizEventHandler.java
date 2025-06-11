package cn.huangdayu.things.sofabiz;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.events.ThingsContainerCancelledEvent;
import cn.huangdayu.things.common.events.ThingsContainerRegisteredEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import com.alipay.sofa.ark.api.ArkClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaBizEventHandler {

    private final ThingsEventObserver thingsEventObserver;

    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsContainerRegisteredEvent.class, this::convertEvent);
        thingsEventObserver.registerObserver(ThingsContainerCancelledEvent.class, this::convertEvent);
    }


    private void convertEvent(Object event) {
        ArkClient.getEventAdminService().sendEvent(() -> event.getClass().getName());
    }
}
