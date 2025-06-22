package cn.huangdayu.things.ai.mcp.tools;

import cn.hutool.core.util.StrUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * @author huangdayu
 */
@Service
public class DateTimeTools {

    @Tool(description = "根据时区获取时间")
    public String getCurrentDateTime(@ToolParam(required = false, description = "时区，默认东八区") String timeZone) {
        return LocalDateTime.now().atZone(StrUtil.isNotBlank(timeZone) ? TimeZone.getTimeZone(timeZone).toZoneId()
                : LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

}
