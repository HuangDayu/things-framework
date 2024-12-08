package cn.huangdayu.things.common.observer.event;

import cn.huangdayu.things.common.observer.ThingsBaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsContainerUpdatedEvent implements ThingsBaseEvent {

    private Object source;

}
