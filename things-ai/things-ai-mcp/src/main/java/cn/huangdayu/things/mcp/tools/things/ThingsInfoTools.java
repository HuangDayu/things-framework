package cn.huangdayu.things.mcp.tools.things;

import cn.huangdayu.things.api.infrastructure.ThingsQueryService;
import cn.huangdayu.things.common.annotation.ThingsTools;
import cn.huangdayu.things.common.dsl.template.ThingsInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 物模型工具
 *
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsTools
public class ThingsInfoTools {

    private final ThingsQueryService thingsQueryService;


    @Tool(description = "获取物联网设备的物模型模板")
    public ThingsInfo getThingsDeviceDsl(@ToolParam(required = true, description = "产品编码") String productCode,
                                         @ToolParam(required = false, description = "设备编码") String deviceCode,
                                         ToolContext toolContext) {
        return thingsQueryService.queryThingsInfo(productCode, deviceCode);
    }

}
