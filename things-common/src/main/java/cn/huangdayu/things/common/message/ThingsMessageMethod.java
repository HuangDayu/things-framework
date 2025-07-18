package cn.huangdayu.things.common.message;

import cn.huangdayu.things.common.exception.ThingsException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsMessageMethod implements Serializable {

    public static final String METHOD_SEPARATOR = "/";


    public ThingsMessageMethod(String method) {
        String[] split = method.split(METHOD_SEPARATOR);
        if (split.length != 5) {
            throw new ThingsException(BAD_REQUEST, "method is error");
        }
        this.productCode = split[0];
        this.deviceCode = split[1];
        this.type = split[2];
        this.identifier = split[3];
        this.action = split[4];
    }

    /**
     * 物模型标识/产品标识
     */
    private String productCode;

    /**
     * 物标识/设备标识
     */
    private String deviceCode;

    /**
     * 类型： service/property/event
     */
    private String type;

    /**
     * 标识
     */
    private String identifier;

    /**
     * 动作：get/set/post/request/response
     */
    private String action;


    @Override
    public String toString() {
        return productCode + METHOD_SEPARATOR + deviceCode + METHOD_SEPARATOR + type + METHOD_SEPARATOR + identifier + METHOD_SEPARATOR + action;
    }
}