package cn.huangdayu.things.ai.mcp.service;

import cn.huangdayu.things.ai.mcp.dto.ThingsAIModels;
import cn.huangdayu.things.ai.mcp.properties.ThingsAiProperties;
import cn.huangdayu.things.common.exception.ThingsException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.ERROR;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Component
public class ThingsAIModelFactory {

    private static final Map<String, ThingsAIModels> thingsAIModels = new ConcurrentHashMap<>();
    private final Map<String, ChatModel> chatModels;
    private final ThingsAiProperties thingsAiProperties;
    private final ChatMemory chatMemory;

    public ThingsAIModels getThingsModel(String model) {
        ThingsAiProperties.Models thingsModel = findThingsModel(model);
        if (thingsModel == null) {
            throw new ThingsException(ERROR, "model not found : " + model);
        }
        return thingsAIModels.computeIfAbsent(model, chatModel -> {
            Optional<ThingsAIModels> first = chatModels.entrySet().stream()
                    .filter(entry -> entry.getKey().contains(thingsModel.getSupplier()))
                    .map(entry -> new ThingsAIModels(model, entry.getValue(),
                            ChatClient.builder(entry.getValue()).defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build()))
                    .findFirst();
            return first.orElseThrow(() -> new ThingsException(ERROR, "model not found : " + model));
        });
    }

    private ThingsAiProperties.Models findThingsModel(String model) {
        for (ThingsAiProperties.Models thingsAiModel : thingsAiProperties.getModels()) {
            if (thingsAiModel.getModel().equals(model)) {
                return thingsAiModel;
            }
        }
        return null;
    }

    public ThingsAIModels getDefaultThingsModel() {
        return getThingsModel(thingsAiProperties.getModels().stream().findFirst().get().getModel());
    }

}
