package cn.huangdayu.things.common.message;

import cn.huangdayu.things.common.exception.ThingsException;
import cn.hutool.core.util.StrUtil;
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
import java.util.function.Consumer;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.*;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_IDENTIFIER;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_SERVICE_RESPONSE;
import static cn.huangdayu.things.common.utils.ThingsUtils.*;


/**
 * @author huangdayu
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class JsonThingsMessage extends AbstractThingsMessage<JSONObject, JSONObject> implements Serializable {


    public JsonThingsMessage() {
        super();
        setMetadata(new JSONObject());
        setPayload(new JSONObject());
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public BaseThingsMetadata getBaseMetadata() {
        return jsonToObject(getMetadata(), BaseThingsMetadata.class);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public void setBaseMetadata(Consumer<BaseThingsMetadata> consumer) {
        BaseThingsMetadata baseThingsMetadata = getBaseMetadata();
        consumer.accept(baseThingsMetadata);
        getMetadata().putAll((JSONObject) JSON.toJSON(baseThingsMetadata, JSONWriter.Feature.IgnoreEmpty));
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this, JSONWriter.Feature.NullAsDefaultValue);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage clientError(String errorTraceCode) {
        return response(BAD_REQUEST, "Client error", errorTraceCode, null);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage notFound(String errorTraceCode) {
        return response(NOT_FOUND, "Not found", errorTraceCode, null);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage serverError(String errorTraceCode) {
        return response(ERROR, "Server error", errorTraceCode, null);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage serverError(String errorTraceCode, String errorMessage) {
        return response(ERROR, errorMessage, errorTraceCode, null);
    }


    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage success() {
        return success(new JSONObject());
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage success(Object payload) {
        return response(SUCCESS, "Success", null, payload);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage exception(ThingsException thingsException) {
        return response(thingsException.getErrorCode(), thingsException.getErrorMessage(), thingsException.getErrorTraceCode(), null);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage timeout() {
        return response(GATEWAY_TIMEOUT, "Async timeout", null, null);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private JsonThingsMessage response(String errorCode, String errorMessage, String errorTraceCode, Object payload) {
        JsonThingsMessage response = cloneMessage();
        response.setBaseMetadata(thingsMetadata -> {
            thingsMetadata.setErrorCode(errorCode);
            thingsMetadata.setErrorMessage(errorMessage);
            thingsMetadata.setErrorTraceCode(errorTraceCode);
        });
        if (payload != null) {
            response.setPayload((JSONObject) JSON.toJSON(payload));
        }
        if (isServiceRequest(response)) {
            response.setMethod(THINGS_SERVICE_RESPONSE.replace(THINGS_IDENTIFIER, subIdentifies(response.getMethod())));
        }
        return response;
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage cloneMessage() {
        return JSON.copy(this);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JSONObject toJson() {
        return JSON.parseObject(JSON.toJSONString(this));
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public boolean isResponse() {
        return StrUtil.isNotBlank(getBaseMetadata().getErrorCode());
    }
}
