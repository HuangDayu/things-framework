package cn.huangdayu.things.common.event;

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
public class ThingsInstancesUpdateEvent implements ThingsEngineEvent {

    private Object source;

}
