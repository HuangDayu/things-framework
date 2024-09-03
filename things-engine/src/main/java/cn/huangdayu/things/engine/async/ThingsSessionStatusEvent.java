package cn.huangdayu.things.engine.async;

import cn.huangdayu.things.engine.wrapper.ThingsSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsSessionStatusEvent implements ThingsEngineEvent {

    private Object source;

    private ThingsSession session;


}
