package cn.huangdayu.things.common.dsl.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsStatusInfo {

    /**
     * 说明：设备名称；类型：String；是否必选：是；长度：{1,50}；取值：任意字符；备注：设备在系统中的显示名称；关键字：deviceName,DeviceName
     */
    private String deviceName;
    /**
     * 说明：设备唯一标识符；类型：String；是否必选：是；长度：{1,100}；取值：设备SN号/MAC地址等；备注：平台唯一标识设备的关键字段；关键字：deviceID,deviceID,DeviceID,DeviceId
     */
    private String deviceId;
    /**
     * 说明：设备在线状态；类型：String；是否必选：是；长度：{1,50}；取值：online/offline；备注：反映设备实时连接状态；关键字：status,Status
     */
    private String status;
    /**
     * 说明：状态通知时间；类型：String；是否必选：否；长度：{1,100}；取值：YYYY-MM-DD hh:mm:ss格式；备注：平台生成的时间戳；关键字：time,Time
     */
    private String time;
    /**
     * 说明：UTC时间戳；类型：String；是否必选：否；长度：{1,100}；取值：ISO 8601格式；备注：国际标准时间；关键字：utcTime,UtcTime
     */
    private String utcTime;
    /**
     * 说明：最后通信时间；类型：String；是否必选：否；长度：{1,100}；取值：YYYY-MM-DD hh:mm:ss；备注：设备状态变更前最后一次通信时间；关键字：lastTime,LastTime
     */
    private String lastTime;
    /**
     * 说明：UTC最后通信时间；类型：String；是否必选：否；长度：{1,100}；取值：ISO 8601格式；备注：国际标准时间格式；关键字：utclastTime,UtclastTime
     */
    private String utcLastTime;
    /**
     * 说明：设备公网IP；类型：String；是否必选：否；长度：{1,100}；取值：IPv4地址格式；备注：设备网络出口地址；关键字：clientIp,ClientIp
     */
    private String clientIp;

}
