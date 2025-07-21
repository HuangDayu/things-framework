package cn.huangdayu.things.common.dsl;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author huangdayu
 */
@Data
public class ThingsProfileInfo implements Serializable {

    /**
     * 说明：设备描述；类型：String；是否必选：否；长度：{1,100}；取值：支持中文/字母/数字/短划线/下划线；备注：产品详细描述；关键字：description,Description
     */
    private String description;

    /**
     * 说明：设备标识；类型：String；是否必选：有条件必选；长度：{1,100}；取值：设备唯一ID；备注：平台注册的设备ID；关键字：deviceID,deviceUID,DeviceUDID,DeviceId
     */
    private String deviceId;

    /**
     * 说明：设备品牌；类型：String；是否必选：否；长度：{1,100}；取值：厂商名称；备注：如华为/小米等；关键字：brand,Brand
     */
    private String brand;

    /**
     * 说明：设备型号；类型：String；是否必选：否；长度：{1,100}；取值：厂家定义型号代码；备注：区分不同规格产品的标识；关键字：model,Model,deviceModel,DeviceModel
     */
    private String model;

    /**
     * 说明：设备类型；类型：Enum；是否必选：否；取值：0-直接设备/1-网关设备/2-网关子设备；备注：设备在网络中的角色；关键字：type,Type
     */
    private String type;

    /**
     * 说明：协议类型；类型：Enum；是否必选：否；取值：0-MQTT/1-LwM2M/2-HTTP/3-其他；备注：设备通信协议；关键字：protocolType,ProtocolType
     */
    private String protocolType;

    /**
     * 说明：网络类型；类型：Enum；是否必选：否；取值：0-WIFI/1-2G/2-3G/3-4G/4-Cat1/5-5G/6-NB-IoT/7-LoRa/8-其他；备注：设备网络接入方式；关键字：netType,NetType
     */
    private String netType;

    /**
     * 说明：MAC地址；类型：String；是否必选：否；长度：{1,50}；取值：标准MAC格式；备注：设备物理地址；关键字：deviceMAC,DeviceMAC
     */
    private String deviceMac;

    /**
     * 说明：设备序列号；类型：String；是否必选：否；长度：{1,100}；取值：厂商序列号；备注：设备出厂唯一标识；关键字：deviceSerialNumber,DeviceSerialNumber
     */
    private String deviceSerialNumber;

    /**
     * 说明：物模型版本；类型：String；是否必选：否；长度：{1,100}；取值：版本号格式；备注：物模型规范版本；关键字：version,Version
     */
    private String version;

    /**
     * 说明：固件版本；类型：String；是否必选：否；长度：{1,20}；取值：版本号格式；备注：设备软件版本；关键字：firmware
     */
    private String firmware;

    /**
     * 说明：通信模块供应商；类型：String；是否必选：否；长度：{1,100}；取值：供应商名称；备注：模组生产厂商；关键字：deviceCU,DeviceCU
     */
    private String deviceCu;

    /**
     * 说明：产品类别；类型：String；是否必选：否；长度：{1,100}；取值：行业分类名称；备注：设备所属品类；关键字：productClass,ProductClass
     */
    private String productClass;

    /**
     * 框架新增字段
     * 说明：产品编码；类型：String；是否必选：是；长度：{1,100}；取值：产品唯一标识；备注：产品唯一标识；关键字：productCode,ProductCode
     */
    private String productCode;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThingsProfileInfo that = (ThingsProfileInfo) o;
        return Objects.equals(productCode, that.productCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productCode);
    }
}
