package cn.huangdayu.things.mcp.tools.things;

import cn.huangdayu.things.common.annotation.ThingsTools;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 设备控制工具
 *
 * @author huangdayu
 */
@ThingsTools
public class ThingsControlTools {


    @Tool(description = "控制物模型设备")
    public String sendThingsMessage(@ToolParam(required = true, description = "设备控制消息，JSONRPC 2.0规范") String message,
                                      ToolContext toolContext) {
        return null;
    }
}
