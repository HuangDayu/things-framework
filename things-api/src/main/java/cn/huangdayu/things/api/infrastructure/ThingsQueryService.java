package cn.huangdayu.things.api.infrastructure;

import cn.huangdayu.things.common.dsl.device.DeviceInfo;
import cn.huangdayu.things.common.dsl.template.ThingsInfo;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsQueryService {

    /**
     * 查询设备信息
     *
     * @param deviceName
     * @param deviceType
     * @param deviceLocation
     * @return
     */
    Set<DeviceInfo> queryDeviceInfo(String deviceName, String deviceType, String deviceLocation);

    /**
     * 查询设备或产品的物模型
     *
     * @param productCode
     * @param deviceCode
     * @return
     */
    ThingsInfo queryThingsInfo(String productCode, String deviceCode);

}
