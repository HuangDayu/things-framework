package cn.huangdayu.things.mcp.tools.things;

import cn.huangdayu.things.common.annotation.ThingsTools;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 物模型工具
 *
 * @author huangdayu
 */
@ThingsTools
public class ThingsDslTools {

    @Tool(description = "获取设备物模型信息")
    public ThingsResponseMessage getThingsDeviceDsl(@ToolParam(required = true, description = "产品编码") String productCode,
                                                    @ToolParam(required = false, description = "设备编码") String deviceCode,
                                                    ToolContext toolContext) {
        return null;
    }

}
