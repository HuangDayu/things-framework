package cn.huangdayu.things.ai.mcp.config;

import cn.huangdayu.things.ai.mcp.tools.OpenMeteoTool;
import cn.huangdayu.things.ai.mcp.tools.WebSearchTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huangdayu
 */
@Configuration
public class ThingAIAutoConfiguration {

    @Bean
    public ToolCallbackProvider weatherTools(OpenMeteoTool openMeteoTool) {
        return MethodToolCallbackProvider.builder().toolObjects(openMeteoTool).build();
    }

    @Bean
    public ToolCallbackProvider webSearchTools(WebSearchTool webSearchTool) {
        return MethodToolCallbackProvider.builder().toolObjects(webSearchTool).build();
    }

}
