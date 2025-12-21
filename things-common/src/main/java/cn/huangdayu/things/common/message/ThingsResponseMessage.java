package cn.huangdayu.things.common.message;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangdayu
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class ThingsResponseMessage extends AbstractThingsMessage {

    public ThingsResponseMessage(Object result) {
        this.result = result;
    }

    public ThingsResponseMessage(ThingsErrorMessage error) {
        this.error = error;
    }

    /**
     * 结果，成功时必须包含。
     */
    private Object result;

    /**
     * 错误消息，失败是必须包含
     */
    private ThingsErrorMessage error;


    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public boolean isSuccess() {
        return error == null;
    }
}
