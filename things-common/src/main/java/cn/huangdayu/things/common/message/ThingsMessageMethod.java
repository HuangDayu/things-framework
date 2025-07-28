package cn.huangdayu.things.common.message;

import cn.huangdayu.things.common.exception.ThingsException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsMessageMethod implements Serializable {


    public ThingsMessageMethod(String method) {
        String[] split = method.split(THINGS_SEPARATOR);
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
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        ThingsMessageMethod that = (ThingsMessageMethod) object;
        return Objects.equals(toString(), that.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(toString());
    }

    @Override
    public String toString() {
        return productCode + THINGS_SEPARATOR + deviceCode + THINGS_SEPARATOR + type + THINGS_SEPARATOR + identifier + THINGS_SEPARATOR + action;
    }
}