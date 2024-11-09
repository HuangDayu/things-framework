package cn.huangdayu.things.common.exception;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huangdayu
 */
@Setter
@Getter
public class ThingsException extends RuntimeException {

    private final String errorCode;
    private final String errorTraceCode;
    private final String errorMessage;
    private final JsonThingsMessage thingsMessage;

    public ThingsException(JsonThingsMessage thingsMessage, String errorCode, String errorMessage, String errorTraceCode) {
        super(errorMessage);
        this.thingsMessage = thingsMessage;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorTraceCode = errorTraceCode;
    }

}
