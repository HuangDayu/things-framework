package cn.huangdayu.things.sofaark.event;

import cn.huangdayu.things.common.observer.ThingsBaseEvent;
import com.alipay.sofa.ark.spi.event.AbstractArkEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import lombok.Getter;

/**
 * @author huangdayu
 */
@Getter
public class ThingsSofaArkEvent extends AbstractArkEvent<Biz> {
    private final String topic;
    private final ThingsBaseEvent thingsEvent;
    private final Biz biz;

    public ThingsSofaArkEvent(Biz biz, ThingsBaseEvent thingsEvent, String topic) {
        super(biz);
        this.biz = biz;
        this.topic = topic;
        this.thingsEvent = thingsEvent;
    }

}
