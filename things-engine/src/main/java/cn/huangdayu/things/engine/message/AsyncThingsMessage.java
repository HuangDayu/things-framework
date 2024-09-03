package cn.huangdayu.things.engine.message;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class AsyncThingsMessage extends JsonThingsMessage {

    /**
     * 异步超时时间
     */
    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private long timeout = 5000;


    /**
     * 已回复的次数
     */
    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private int replied = 0;

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private Consumer<JsonThingsMessage> responseConsumer;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public AsyncThingsMessage(long timeout, Consumer<JsonThingsMessage> responseConsumer) {
        this.timeout = timeout;
        this.responseConsumer = responseConsumer;
    }

    public AsyncThingsMessage(Consumer<JsonThingsMessage> responseConsumer) {
        this.responseConsumer = responseConsumer;
    }
}
