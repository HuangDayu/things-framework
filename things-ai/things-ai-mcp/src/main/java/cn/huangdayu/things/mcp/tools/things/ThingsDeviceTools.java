package cn.huangdayu.things.mcp.tools.things;

import cn.huangdayu.things.api.infrastructure.ThingsQueryService;
import cn.huangdayu.things.common.annotation.ThingsTools;
import cn.huangdayu.things.common.dsl.device.DeviceInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.Set;

/**
 * 设备相关工具
 *
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsTools
public class ThingsDeviceTools {

    private final ThingsQueryService thingsQueryService;


    @Tool(description = "获取物联网设备列表，可以指定设备名称，类型和位置，这些参数是非必须的，如果没有传递则查询用户所有的设备列表")
    public Set<DeviceInfo> getThingsDevices(@ToolParam(required = false, description = "设备名称，如灯，风扇，空调等设备名称") String deviceName,
                                            @ToolParam(required = false, description = "设备类型，如插座，开关，窗帘等设备类别") String deviceType,
                                            @ToolParam(required = false, description = "设备方位，如房间名称，位置，坐标等信息") String deviceLocation,
                                            ToolContext toolContext) {
        return thingsQueryService.queryDeviceInfo(deviceName, deviceType, deviceLocation);
    }

}
