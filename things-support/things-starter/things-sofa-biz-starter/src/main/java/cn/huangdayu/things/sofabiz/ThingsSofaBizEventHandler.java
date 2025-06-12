package cn.huangdayu.things.sofabiz;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.events.ThingsContainerCancelledEvent;
import cn.huangdayu.things.common.events.ThingsContainerRegisteredEvent;
import cn.huangdayu.things.common.observer.ThingsBaseEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.spi.event.AbstractArkEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.service.classloader.ClassLoaderService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
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

    /**
     * 这里依赖SofaArk事件驱动实现ThingsEvent的传递，从biz中传递到ark中，警惕出现事件循环传递的问题，只能biz发动到ark，ark不能再发送到biz
     *
     * @param event The event that occurred.
     */
    private void convertEvent(ThingsBaseEvent event) {
        Biz biz = ArkClient.getBizManagerService().getBizByClassLoader(Thread.currentThread().getContextClassLoader());
        ArkClient.getEventAdminService().sendEvent(new SofaBizContainerEvent(biz, event, event.getClass().getName()));
    }

    @Getter
    private static class SofaBizContainerEvent extends AbstractArkEvent<Biz> {
        private final String topic;
        private final ThingsBaseEvent thingsEvent;

        public SofaBizContainerEvent(Biz source, ThingsBaseEvent thingsEvent, String topic) {
            super(source);
            this.topic = topic;
            this.thingsEvent = thingsEvent;
        }
    }
}
