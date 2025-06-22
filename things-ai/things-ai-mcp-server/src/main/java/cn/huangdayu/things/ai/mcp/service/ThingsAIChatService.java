package cn.huangdayu.things.ai.mcp.service;

import cn.huangdayu.things.ai.mcp.tools.DateTimeTools;
import cn.huangdayu.things.ai.mcp.tools.OpenMeteoTool;
import cn.huangdayu.things.ai.mcp.tools.WebSearchTool;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.DefaultChatOptions;
import org.springframework.ai.model.tool.DefaultToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Component
public class ThingsAIChatService {

    private final ThingsAIModelFactory thingsAIModelFactory;

    @Value("classpath:/prompts/things-ai-tools.txt")
    private Resource thingsAIToolsPrompts;

    public Flux<String> chat(String chatId, String model, String prompt) {
        DefaultChatOptions defaultChatOptions = new DefaultChatOptions();
        defaultChatOptions.setModel(model);
        defaultChatOptions.setTemperature(0.8);
        return thingsAIModelFactory.getThingsModel(model)
                .getChatClient()
                .prompt()
                .options(defaultChatOptions)
                .user(prompt)
                .advisors(memoryAdvisor -> memoryAdvisor
                        .param(ChatMemory.CONVERSATION_ID, chatId)
                ).stream()
                .content();
    }

    public Flux<String> deepThinkingChat(String chatId, String model, String prompt) {
        DefaultChatOptions defaultChatOptions = new DefaultChatOptions();
        defaultChatOptions.setModel(model);
        defaultChatOptions.setTemperature(0.8);
        return thingsAIModelFactory.getThingsModel(model)
                .getChatClient()
                .prompt()
                .options(defaultChatOptions)
                .user(prompt)
                .advisors(memoryAdvisor -> memoryAdvisor
                        .param(ChatMemory.CONVERSATION_ID, chatId)
                ).stream()
                .content();
    }

    public Flux<String> search(String chatId, String model, String prompt) {
        DefaultToolCallingChatOptions callingChatOptions = new DefaultToolCallingChatOptions();
        callingChatOptions.setModel(model);
        callingChatOptions.setInternalToolExecutionEnabled(true);
        callingChatOptions.setToolCallbacks(List.of(ToolCallbacks.from(new DateTimeTools(), new WebSearchTool(), new OpenMeteoTool())));
        return thingsAIModelFactory.getThingsModel(model)
                .getChatClient()
                .prompt()
                .options(callingChatOptions)
                .system(thingsAIToolsPrompts)
                .user(prompt)
                .advisors(memoryAdvisor -> memoryAdvisor
                        .param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }
}
