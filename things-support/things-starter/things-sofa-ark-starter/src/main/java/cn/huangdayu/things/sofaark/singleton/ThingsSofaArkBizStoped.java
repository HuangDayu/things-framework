package cn.huangdayu.things.sofaark.singleton;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.sofaark.condition.ThingsSofaArkSingletonCondition;
import com.alipay.sofa.ark.spi.event.biz.AfterBizStopEvent;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;

/**
 * @author huangdayu
 */
@Conditional(ThingsSofaArkSingletonCondition.class)
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaArkBizStoped implements EventHandler<AfterBizStopEvent> {

    private final ThingsSofaArkSingleton thingsSofaArkSingleton;

    @Override
    public void handleEvent(AfterBizStopEvent event) {
        thingsSofaArkSingleton.unregister(event.getSource());
    }


    @Override
    public int getPriority() {
        return 0;
    }
}
