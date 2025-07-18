package cn.huangdayu.things.common.wrapper;

import cn.huangdayu.things.common.message.ThingsRequestMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author huangdayu
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsSubscribes implements Serializable {

    /**
     * 订阅者
     */
    private Object subscriber;

    /**
     * 物模型消息
     */
    private ThingsRequestMessage trm;
    /**
     * 是否共享订阅，默认为false，如果是，则将产品标识作为共享订阅的分组标识，groupId=productCode
     */
    private boolean share;
    /**
     * 产品标识
     */
    private String productCode;
    /**
     * 设备标识
     */
    private String deviceCode;
    /**
     * 方法： thing.service.recordStreaming.request
     */
    private String method;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ThingsSubscribes that = (ThingsSubscribes) o;
        return share == that.share && Objects.equals(productCode, that.productCode) && Objects.equals(deviceCode, that.deviceCode) && Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(share, productCode, deviceCode, method);
    }
}
