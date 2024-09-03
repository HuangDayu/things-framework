package cn.huangdayu.things.engine.message;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * @author huangdayu
 */
@Data
public abstract class AbstractThingsMessage<M extends Serializable, P extends Serializable> implements Serializable {

    /**
     * 消息id
     */
    private String id;
    /**
     * 消息版本
     */
    private String version;
    /**
     * 消息时间
     */
    private Long time;
    /**
     * Qos级别
     * QoS 0 - At most once（至多一次）
     * QoS 1 - At least once（至少一次）
     * QoS 2 - Exactly once（确保只有一次）
     *
     * @return
     */
    private int qos = 0;

    /**
     * 消息元数据
     */
    private M metadata;
    /**
     * 消息体
     */
    private P payload;
    /**
     * 消息方法
     *
     * @see cn.huangdayu.things.engine.common.ThingsConstants.Methods
     */
    private String method;

    public AbstractThingsMessage() {
        setId(UUID.randomUUID().toString().replaceAll("-", ""));
        setVersion("1");
        setTime(System.currentTimeMillis());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractThingsMessage<?, ?> that = (AbstractThingsMessage<?, ?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
