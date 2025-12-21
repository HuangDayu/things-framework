package cn.huangdayu.things.mcp.tools.things;

import cn.huangdayu.things.common.annotation.ThingsTools;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 设备控制工具
 *
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsTools
public class ThingsMessageTools {

//    private final ThingsPublisher thingsPublisher;

    @Tool(description = "发送控制物联网设备的消息")
    public ThingsResponseMessage sendThingsMessage(@ToolParam(description = "设备控制消息，JSONRPC 2.0规范") ThingsRequestMessage message,
                                                   ToolContext toolContext) {
        log.debug("Send Things Message: {}", message);
        return message.success();
    }
}
