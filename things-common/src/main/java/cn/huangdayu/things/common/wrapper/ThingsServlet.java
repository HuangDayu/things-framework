package cn.huangdayu.things.common.wrapper;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author huangdayu
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsServlet {

    public ThingsServlet(JsonThingsMessage jtm) {
        this.jtm = jtm;
    }

    private String type;
    private String topic;
    private String clientCode;
    private String groupCode;
    private String sessionCode;
    private JsonThingsMessage jtm;

}
