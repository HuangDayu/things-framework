package cn.huangdayu.things.common.message;

import cn.huangdayu.things.common.constants.ThingsConstants;
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
     * QoS 0 - At most once 最多一次（可能丢失） 网络开销最低 适用场景： 环境传感器数据、非关键日志
     * QoS 1 - At least once 至少一次（可能重复） 网络开销中等 适用场景： 设备控制指令、状态更新
     * QoS 2 - Exactly once 恰好一次（不丢失不重复）网络开销最高 适用场景： 金融交易、关键配置下发
     *
     * @return
     */
    private int qos = 0;


    /**
     * 超时时间，单位：毫秒
     */
    private long timeout = 5000;

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
     * @see ThingsConstants.Methods
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
