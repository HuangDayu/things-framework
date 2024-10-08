package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.engine.message.JsonThingsMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsRequest {

    private JsonThingsMessage message;
    private HttpServletRequest request;


}
