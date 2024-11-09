package cn.huangdayu.things.common.message;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author huangdayu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseThingsMessage<M extends BaseThingsMetadata, P extends Serializable> extends AbstractThingsMessage<M, P> implements Serializable {


    public BaseThingsMessage() {
        super();
    }

    public BaseThingsMessage(M metadata, P payload) {
        super();
        setPayload(payload);
        setMetadata(metadata);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
