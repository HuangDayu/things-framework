package cn.huangdayu.things.common.wrapper;

import lombok.AllArgsConstructor;
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

    private String type;
    private String topic;
    private String clientCode;
    private String groupCode;
    private String sessionCode;
    private Object source;
    private Object target;
    private Object subscriber;

}
