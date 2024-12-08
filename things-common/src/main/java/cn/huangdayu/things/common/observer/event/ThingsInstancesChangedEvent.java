package cn.huangdayu.things.common.observer.event;

import cn.huangdayu.things.common.observer.ThingsBaseEvent;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsInstancesChangedEvent implements ThingsBaseEvent {

    private Object source;

    private Set<ThingsInstance> addedInstances;

    private Set<String> removedInstanceCodes;

}
