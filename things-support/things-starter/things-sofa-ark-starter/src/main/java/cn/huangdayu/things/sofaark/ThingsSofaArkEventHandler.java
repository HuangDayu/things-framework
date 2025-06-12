package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.common.events.ThingsContainerCancelledEvent;
import cn.huangdayu.things.common.events.ThingsContainerRegisteredEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import com.alipay.sofa.ark.spi.event.AbstractArkEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cn.huangdayu.things.sofaark.ThingsSofaArkUtils.getBizContext;

/**
 * @author huangdayu
 */
@Component
@RequiredArgsConstructor
public class ThingsSofaArkEventHandler implements EventHandler<AbstractArkEvent<Biz>> {

    private final ThingsEventObserver thingsEventObserver;

    /**
     * 这里依赖SofaArk事件驱动实现ThingsEvent的传递，从biz中传递到ark中，警惕出现事件循环传递的问题，只能biz发动到ark，ark不能再发送到biz
     *
     * @param event The event that occurred.
     */
    @Override
    public void handleEvent(AbstractArkEvent<Biz> event) {
        if (event.getTopic().equals(ThingsContainerRegisteredEvent.class.getName())) {
            ThingsContainer thingsContainer = getBizContext(event.getSource()).getBean(ThingsContainer.class);
            thingsEventObserver.notifyObservers(new ThingsContainerRegisteredEvent(event, thingsContainer));
        }
        if (event.getTopic().equals(ThingsContainerCancelledEvent.class.getName())) {
            ThingsContainer thingsContainer = getBizContext(event.getSource()).getBean(ThingsContainer.class);
            thingsEventObserver.notifyObservers(new ThingsContainerCancelledEvent(event, thingsContainer));
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
