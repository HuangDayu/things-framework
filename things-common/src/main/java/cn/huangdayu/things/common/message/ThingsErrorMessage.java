package cn.huangdayu.things.common.message;

import cn.huangdayu.things.common.constants.ThingsConstants;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huangdayu
 */
@Data
public class ThingsErrorMessage implements Serializable {
    /**
     * 错误码
     *
     * @see ThingsConstants.ErrorCodes
     */
    private String code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 错误数据
     */
    private Object data;
}
