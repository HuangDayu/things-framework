package cn.huangdayu.things.common.message;

import cn.huangdayu.things.common.constants.ThingsConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseThingsMetadata implements Serializable {

    public BaseThingsMetadata(String productCode, String deviceCode) {
        this.productCode = productCode;
        this.deviceCode = deviceCode;
    }

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 物模型标识/产品标识
     */
    private String productCode;

    /**
     * 物标识/设备标识
     */
    private String deviceCode;


    /**
     * 分组标识
     */
    private Set<String> groupCode;

    /**
     * 来源服务信息
     */
    private String sourceCode;

    /**
     * 目标服务信息
     */
    private String targetCode;


    /**
     * 错误码
     *
     * @see ThingsConstants.ErrorCodes
     */
    private String errorCode;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 错误追踪id
     */
    private String errorTraceCode;
}