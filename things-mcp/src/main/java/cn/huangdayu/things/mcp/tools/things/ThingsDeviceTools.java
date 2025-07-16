package cn.huangdayu.things.mcp.tools.things;

import cn.huangdayu.things.common.annotation.ThingsTools;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 设备相关工具
 *
 * @author huangdayu
 */
@ThingsTools
public class ThingsDeviceTools {

    @Tool(description = "获取用户设备信息")
    public String getThingsDevices(@ToolParam(required = true, description = "设备名称") String deviceName,
                                   @ToolParam(required = false, description = "设备类型") String deviceType,
                                   ToolContext toolContext) {
        return null;
    }

}
