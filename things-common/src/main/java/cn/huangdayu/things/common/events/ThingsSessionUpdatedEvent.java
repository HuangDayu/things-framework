package cn.huangdayu.things.common.events;

import cn.huangdayu.things.common.observer.ThingsBaseEvent;
import cn.huangdayu.things.common.wrapper.ThingsSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsSessionUpdatedEvent implements ThingsBaseEvent {

    private Object source;

    private ThingsSession session;


}
