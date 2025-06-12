package cn.huangdayu.things.sofaark.singleton;

import cn.huangdayu.things.api.container.ThingsRegister;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.events.ThingsContainerCancelledEvent;
import cn.huangdayu.things.common.events.ThingsContainerRegisteredEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.sofaark.condition.ThingsSofaArkSingletonCondition;
import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.spi.model.Biz;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.huangdayu.things.sofaark.utils.ThingsSofaArkUtils.getBizContext;

/**
 * @author huangdayu
 */
@Conditional(ThingsSofaArkSingletonCondition.class)
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaArkSingleton {
    public static final Map<Biz, ThingsSofaArkContainer> ARK_CONTAINER_MAP = new ConcurrentHashMap<>();
    private final ThingsEventObserver thingsEventObserver;
    private final ThingsRegister thingsRegister;

    public void register(Biz biz) {
        if (biz == ArkClient.getMasterBiz()) {
            return;
        }
        if (ARK_CONTAINER_MAP.containsKey(biz)) {
            thingsRegister.unregister(ARK_CONTAINER_MAP.get(biz));
        }
        ThingsSofaArkContainer thingsSofaArkContainer = new ThingsSofaArkContainer(getBizContext(biz));
        thingsRegister.register(thingsSofaArkContainer);
        ARK_CONTAINER_MAP.put(biz, thingsSofaArkContainer);
        thingsEventObserver.notifyObservers(new ThingsContainerRegisteredEvent(biz, thingsSofaArkContainer));
    }

    public void unregister(Biz biz) {
        if (biz == ArkClient.getMasterBiz()) {
            return;
        }
        ThingsSofaArkContainer thingsSofaArkContainer = ARK_CONTAINER_MAP.get(biz);
        if (thingsSofaArkContainer != null) {
            thingsRegister.unregister(thingsSofaArkContainer);
            ARK_CONTAINER_MAP.remove(biz);
            thingsEventObserver.notifyObservers(new ThingsContainerCancelledEvent(biz, thingsSofaArkContainer));
        }
    }

}
