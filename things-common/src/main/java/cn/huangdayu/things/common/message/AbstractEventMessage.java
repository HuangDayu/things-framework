package cn.huangdayu.things.common.message;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huangdayu
 */
@Data
@AllArgsConstructor
public abstract class AbstractEventMessage implements ThingsEventMessage {

    private String deviceCode;

}
