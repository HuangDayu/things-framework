package cn.huangdayu.things.sofaark.singleton;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.sofaark.condition.ThingsSofaArkSingletonCondition;
import com.alipay.sofa.ark.spi.event.biz.AfterBizStartupEvent;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;

/**
 * @author huangdayu
 */
@Conditional(ThingsSofaArkSingletonCondition.class)
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaArkBizStarted implements EventHandler<AfterBizStartupEvent> {

    private final ThingsSofaArkSingleton thingsSofaArkSingleton;

    @Override
    public void handleEvent(AfterBizStartupEvent event) {
        thingsSofaArkSingleton.register(event.getSource());
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
