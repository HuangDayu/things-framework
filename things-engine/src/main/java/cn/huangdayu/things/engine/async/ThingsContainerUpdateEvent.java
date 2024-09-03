package cn.huangdayu.things.engine.async;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsContainerUpdateEvent implements ThingsEngineEvent {

    private Object source;

}
