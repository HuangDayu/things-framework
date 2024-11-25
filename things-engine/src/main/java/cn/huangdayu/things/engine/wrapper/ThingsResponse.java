package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsResponse {

    private JsonThingsMessage message;

}
