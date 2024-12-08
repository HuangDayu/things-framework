package cn.huangdayu.things.common.message;

import cn.huangdayu.things.common.exception.ThingsException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.function.Consumer;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.*;


/**
 * @author huangdayu
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class JsonThingsMessage extends AbstractThingsMessage<JSONObject, JSONObject> implements Serializable {


    public JsonThingsMessage() {
        super();
        setMetadata(new JSONObject());
        setPayload(new JSONObject());
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public BaseThingsMetadata getBaseMetadata() {
        return getMetadata().toJavaObject(BaseThingsMetadata.class);
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
        return JSON.toJSONString(this);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage clientError(String errorTraceCode) {
        JsonThingsMessage response = cloneMessage();
        response.setBaseMetadata(thingsMetadata -> {
            thingsMetadata.setErrorCode(BAD_REQUEST);
            thingsMetadata.setErrorMessage("Client error");
            thingsMetadata.setErrorTraceCode(errorTraceCode);
        });
        return response;
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage notFound(String errorTraceCode) {
        JsonThingsMessage response = cloneMessage();
        response.setBaseMetadata(thingsMetadata -> {
            thingsMetadata.setErrorCode(NOT_FOUND);
            thingsMetadata.setErrorMessage("Not found");
            thingsMetadata.setErrorTraceCode(errorTraceCode);
        });
        return response;
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage serverError(String errorTraceCode) {
        JsonThingsMessage response = cloneMessage();
        response.setBaseMetadata(thingsMetadata -> {
            thingsMetadata.setErrorCode(ERROR);
            thingsMetadata.setErrorMessage("Server error");
            thingsMetadata.setErrorTraceCode(errorTraceCode);
        });
        return response;
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage serverError(String errorTraceCode, String errorMessage) {
        JsonThingsMessage response = cloneMessage();
        response.setBaseMetadata(thingsMetadata -> {
            thingsMetadata.setErrorCode(ERROR);
            thingsMetadata.setErrorMessage(errorMessage);
            thingsMetadata.setErrorTraceCode(errorTraceCode);
        });
        return response;
    }


    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage success() {
        return success(new JSONObject());
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage success(Object payload) {
        JsonThingsMessage response = cloneMessage();
        response.setBaseMetadata(thingsMetadata -> {
            thingsMetadata.setErrorCode(SUCCESS);
            thingsMetadata.setErrorMessage("Success");
        });
        response.setPayload((JSONObject) JSON.toJSON(payload));
        return response;
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage exception(ThingsException thingsException) {
        JsonThingsMessage response = cloneMessage();
        response.setBaseMetadata(thingsMetadata -> {
            thingsMetadata.setErrorCode(thingsException.getErrorCode());
            thingsMetadata.setErrorMessage(thingsException.getErrorMessage());
            thingsMetadata.setErrorTraceCode(thingsException.getErrorTraceCode());
        });
        return response;
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public JsonThingsMessage timeout() {
        JsonThingsMessage response = cloneMessage();
        response.setBaseMetadata(thingsMetadata -> {
            thingsMetadata.setErrorCode(GATEWAY_TIMEOUT);
            thingsMetadata.setErrorMessage("Async timeout");
        });
        return response;
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private JsonThingsMessage cloneMessage() {
        return JSON.copy(this);
    }
}
