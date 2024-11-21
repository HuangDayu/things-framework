package cn.huangdayu.things.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@Data
public class ThingsEventSubscriber<T extends ThingsEngineEvent> {

    private Class<T> type;
    private ThingsEventConsumer<T> subscriber;

}
