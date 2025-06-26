package cn.huangdayu.things.ai.mcp.tools;

import cn.huangdayu.things.common.annotation.ThingsTools;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * 设备相关工具
 *
 * @author huangdayu
 */
@ThingsTools
public class ThingsDeviceTools {

    @Tool(description = "获取设备列表")
    public String getThingsDevices(ToolContext toolContext) {
        return null;
    }

}
