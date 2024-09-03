package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.engine.message.JsonThingsMessage;
import jakarta.servlet.http.HttpServletResponse;
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
    private HttpServletResponse response;

}
