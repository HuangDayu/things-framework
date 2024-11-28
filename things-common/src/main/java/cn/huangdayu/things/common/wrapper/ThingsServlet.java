package cn.huangdayu.things.common.wrapper;

import cn.huangdayu.things.common.annotation.ThingsIntercepting;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThingsServlet {

    private ThingsIntercepting thingsIntercepting;
    private JsonThingsMessage message;

}
