package cn.huangdayu.things.common.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author huangdayu
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsSubscribes implements Serializable {

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


}
