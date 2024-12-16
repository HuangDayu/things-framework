package cn.huangdayu.things.common.message;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangdayu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusThingsMessage extends JsonThingsMessage {

    private String topic;
    private String clientId;
    private String groupId;
    private String sessionId;

    @Override
    public String toString() {
        return super.toString();
    }
}
