package cn.huangdayu.things.engine.async;

import cn.huangdayu.things.common.event.ThingsEngineEvent;
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
public class ThingsInstancesChangeEvent implements ThingsEngineEvent {

    private Object source;

    private Set<ThingsInstance> addedInstances;

    private Set<String> removedInstanceCodes;

}
