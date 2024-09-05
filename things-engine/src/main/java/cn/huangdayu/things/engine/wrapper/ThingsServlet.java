package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.engine.annotation.ThingsIntercepting;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private HttpServletRequest request;
    private HttpServletResponse response;

}
