package cn.huangdayu.things.ai.mcp.service;

import cn.huangdayu.things.ai.mcp.dto.ThingsChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.DefaultChatOptions;
import org.springframework.ai.model.tool.DefaultToolCallingChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;

import static cn.huangdayu.things.ai.mcp.config.ThingAIAutoConfiguration.THINGS_TOOL_CALLBACKS;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Component
public class ThingsAIChatService {

    private final ThingsAIModelFactory thingsAIModelFactory;
    private final ChatMemory chatMemory;

    @Value("classpath:/prompts/things-ai-tools.txt")
    private Resource thingsAIToolsPrompts;

    public Flux<String> chat(ThingsChatRequest thingsChatRequest) {
        return thingsAIModelFactory.getThingsModel(thingsChatRequest.getModel())
                .getChatClient()
                .prompt()
                .options(thingsChatRequest.isEnabledTools() ? getToolChatOptions(thingsChatRequest) : getDefaultChatOptions(thingsChatRequest))
                .user(thingsChatRequest.getPrompt())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, thingsChatRequest.getChatId()))
                .stream()
                .content();
    }

    public void clearChat(String chatId) {
        chatMemory.clear(chatId);
    }

    private ChatOptions getDefaultChatOptions(ThingsChatRequest thingsChatRequest) {
        DefaultChatOptions defaultChatOptions = new DefaultChatOptions();
        defaultChatOptions.setModel(thingsChatRequest.getModel());
        defaultChatOptions.setTemperature(0.8);
        return defaultChatOptions;
    }

    private ChatOptions getToolChatOptions(ThingsChatRequest thingsChatRequest) {
        DefaultToolCallingChatOptions callingChatOptions = new DefaultToolCallingChatOptions();
        callingChatOptions.setModel(thingsChatRequest.getModel());
        callingChatOptions.setInternalToolExecutionEnabled(true);
        callingChatOptions.setToolCallbacks(THINGS_TOOL_CALLBACKS.stream().toList());
        callingChatOptions.setToolContext(Map.of("thingsChatRequest", thingsChatRequest));
        return callingChatOptions;
    }
}
