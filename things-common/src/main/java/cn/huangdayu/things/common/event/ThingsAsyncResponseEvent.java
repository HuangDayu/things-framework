package cn.huangdayu.things.common.event;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsAsyncResponseEvent implements ThingsEngineEvent {
    private Object source;
    private JsonThingsMessage jsonThingsMessage;
}
