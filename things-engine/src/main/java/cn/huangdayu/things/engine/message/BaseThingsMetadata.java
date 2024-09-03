package cn.huangdayu.things.engine.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangdayu
 */
@Data
public class BaseThingsMetadata implements Serializable {


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
     * 来源服务信息
     */
    private String source;

    /**
     * 目标服务信息
     */
    private String target;


    /**
     * 错误码
     *
     * @see cn.huangdayu.things.engine.common.ThingsConstants.ErrorCodes
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