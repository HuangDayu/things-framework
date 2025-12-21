package cn.huangdayu.things.common.message;

import cn.huangdayu.things.common.exception.ThingsException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.*;


/**
 * @author huangdayu
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ThingsRequestMessage extends AbstractThingsMessage implements Serializable {


    /**
     * 消息体
     */
    private JSONObject params;


    public ThingsRequestMessage() {
        super();
        this.params = new JSONObject();
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this, JSONWriter.Feature.NullAsDefaultValue);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public ThingsResponseMessage clientError(String errorMessage) {
        return responseError(BAD_REQUEST, errorMessage, null);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public ThingsResponseMessage notFound(String errorMessage) {
        return responseError(NOT_FOUND, errorMessage, null);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public ThingsResponseMessage serverError(String errorMessage) {
        return responseError(ERROR, errorMessage, null);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public ThingsResponseMessage serverError(String errorTraceCode, String errorMessage) {
        return responseError(ERROR, errorMessage, errorTraceCode);
    }


    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public ThingsResponseMessage success() {
        return responseSuccess(null);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public ThingsResponseMessage success(Object params) {
        return responseSuccess(params);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public ThingsResponseMessage exception(ThingsException thingsException) {
        return responseError(thingsException.getErrorCode(), thingsException.getErrorMessage(), thingsException.getErrorTraceCode());
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public ThingsResponseMessage timeout() {
        return responseError(GATEWAY_TIMEOUT, "Async timeout", null);
    }


    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public ThingsResponseMessage responseSuccess(Object result) {
        ThingsResponseMessage thingsResponseMessage = JSON.parseObject(JSON.toJSONString(this), ThingsResponseMessage.class);
        thingsResponseMessage.setResult(result != null ? result : new JSONObject());
        return thingsResponseMessage;
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public ThingsResponseMessage responseError(String errorCode, String errorMessage, String errorTraceCode) {
        ThingsResponseMessage thingsResponseMessage = JSON.parseObject(JSON.toJSONString(this), ThingsResponseMessage.class);
        ThingsErrorMessage thingsMetadata = new ThingsErrorMessage();
        thingsMetadata.setCode(errorCode);
        thingsMetadata.setMessage(errorMessage);
        thingsMetadata.setData(errorTraceCode);
        thingsResponseMessage.setError(thingsMetadata);
        return thingsResponseMessage;
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public ThingsRequestMessage cloneMessage() {
        return JSON.copy(this);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JSONObject toJson() {
        return JSON.parseObject(JSON.toJSONString(this));
    }
}
