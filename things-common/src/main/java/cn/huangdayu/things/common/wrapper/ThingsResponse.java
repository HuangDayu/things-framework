package cn.huangdayu.things.common.wrapper;

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

    private Object source;
    private String type;
    private String endpoint;
    private String clientCode;
    private String groupCode;
    private String sessionCode;
    private JsonThingsMessage jtm;

}
