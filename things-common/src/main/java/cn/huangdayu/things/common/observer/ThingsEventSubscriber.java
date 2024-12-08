package cn.huangdayu.things.common.observer;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@Data
public class ThingsEventSubscriber<T extends ThingsBaseEvent> {

    private Class<T> type;
    private ThingsEventConsumer<T> subscriber;

}
