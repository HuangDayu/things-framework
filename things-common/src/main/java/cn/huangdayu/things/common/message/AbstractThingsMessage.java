package cn.huangdayu.things.common.message;

import cn.huangdayu.things.common.constants.ThingsConstants;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@SuperBuilder
@Data
public abstract class AbstractThingsMessage implements Serializable {

    /**
     * 消息id
     */
    @Builder.Default
    private String id = UUID.randomUUID().toString().replaceAll("-", "");
    /**
     * 消息版本
     */
    @Builder.Default
    private String jsonrpc = "2.0";
    /**
     * 消息时间
     */
    @Builder.Default
    private Long time = System.currentTimeMillis();
    /**
     * Qos级别
     * QoS 0 - At most once 最多一次（可能丢失） 网络开销最低 适用场景： 环境传感器数据、非关键日志
     * QoS 1 - At least once 至少一次（可能重复） 网络开销中等 适用场景： 设备控制指令、状态更新
     * QoS 2 - Exactly once 恰好一次（不丢失不重复）网络开销最高 适用场景： 金融交易、关键配置下发
     *
     * @return
     */
    @Builder.Default
    private int qos = 0;


    /**
     * 超时时间，单位：毫秒
     */
    @Builder.Default
    private long timeout = 5000;


    /**
     * 消息方法
     *
     * @see ThingsConstants.Methods
     */
    private String method;


    public void setMethod(String method) {
        this.method = method;
    }

    public void setMethod(ThingsMessageMethod tmm) {
        this.method = tmm.toString();
    }


    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public ThingsMessageMethod getMessageMethod() {
        return new ThingsMessageMethod(this.getMethod());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractThingsMessage that = (AbstractThingsMessage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
