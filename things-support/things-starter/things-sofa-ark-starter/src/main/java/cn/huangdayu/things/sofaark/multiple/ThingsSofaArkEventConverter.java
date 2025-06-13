package cn.huangdayu.things.sofaark.multiple;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.sofaark.event.ThingsSofaArkEvent;
import cn.huangdayu.things.sofaark.condition.ThingsSofaArkMultipleCondition;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;

/**
 * @author huangdayu
 */
@Conditional(ThingsSofaArkMultipleCondition.class)
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaArkEventConverter implements EventHandler<ThingsSofaArkEvent> {

    private final ThingsEventObserver thingsEventObserver;

    /**
     * 这里依赖SofaArk事件驱动实现ThingsEvent的传递，从biz中传递到ark中，警惕出现事件循环传递的问题，只能biz发动到ark，ark不能再发送到biz
     *
     * @param event The event that occurred.
     */
    @Override
    public void handleEvent(ThingsSofaArkEvent event) {
        thingsEventObserver.notifyObservers(event.getThingsEvent());
    }

    @Override
    public int getPriority() {
        return 0;
    }


}
