package cn.huangdayu.things.sofabiz.multiple;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.observer.ThingsBaseEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.sofaark.event.ThingsSofaArkEvent;
import cn.huangdayu.things.sofabiz.condition.ThingsSofaBizMultipleCondition;
import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.spi.model.Biz;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;

/**
 * @author huangdayu
 */
@Conditional(ThingsSofaBizMultipleCondition.class)
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaBizEventConverter {

    private final ThingsEventObserver thingsEventObserver;

    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsBaseEvent.class, this::convertEvent);
    }

    /**
     * 这里依赖SofaArk事件驱动实现ThingsEvent的传递，从biz中传递到ark中，警惕出现事件循环传递的问题，只能biz发动到ark，ark不能再发送到biz
     *
     * @param event The event that occurred.
     */
    private void convertEvent(ThingsBaseEvent event) {
        Biz biz = ArkClient.getBizManagerService().getBizByClassLoader(Thread.currentThread().getContextClassLoader());
        ArkClient.getEventAdminService().sendEvent(new ThingsSofaArkEvent(biz, event, event.getClass().getName()));
    }

}
