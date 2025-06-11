package cn.huangdayu.things.starter.engine;

import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static cn.huangdayu.things.common.utils.ThingsUtils.getUUID;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ConditionalOnClass(HttpStatusCode.class)
@RestControllerAdvice
public class ThingsExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(ThingsException.class)
    public ResponseEntity<Object> handleException(ThingsException e, HttpServletRequest request, WebRequest webRequest) {
        JsonThingsMessage jtm = e.getJtm();
        if (jtm == null) {
            jtm = new JsonThingsMessage();
        }
        String errorTraceCode = getUUID();
        HttpStatus httpStatus = INTERNAL_SERVER_ERROR;
        try {
            httpStatus = HttpStatus.valueOf(Integer.parseInt(e.getErrorCode()));
        } catch (Exception ignored) {
        }
        log.error("Things exception , traceId : {} , exception stack trace : ", errorTraceCode, e);
        return ResponseEntity.status(httpStatus).body(jtm.serverError(errorTraceCode, e.getErrorMessage()));
    }


}
