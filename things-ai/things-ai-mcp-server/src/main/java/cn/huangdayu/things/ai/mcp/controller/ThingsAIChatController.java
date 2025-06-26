package cn.huangdayu.things.ai.mcp.controller;

import cn.huangdayu.things.ai.mcp.dto.Result;
import cn.huangdayu.things.ai.mcp.dto.ThingsChatRequest;
import cn.huangdayu.things.ai.mcp.properties.ThingsAiProperties;
import cn.huangdayu.things.ai.mcp.service.ThingsAIChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Set;

/**
 * @author huangdayu
 */
@CrossOrigin
@RequiredArgsConstructor
@RestController
@Tag(name = "Chat APIs")
@RequestMapping("/api/v1")
public class ThingsAIChatController {

    private final ThingsAIChatService thingsAIChatService;
    private final ThingsAiProperties thingsAiProperties;

    @PostMapping("/chat")
    @Operation(summary = "Flux Chat")
    public Flux<String> chat(HttpServletResponse response, HttpServletRequest request, @Validated @RequestBody String prompt,
                             @RequestHeader(value = "model") String model, @RequestHeader(value = "chatId") String chatId,
                             @RequestHeader(value = "onlineSearch", defaultValue = "false") boolean onlineSearch,
                             @RequestHeader(value = "deepThink", defaultValue = "false") boolean deepThink,
                             @RequestHeader(value = "enabledTools", defaultValue = "false") boolean enabledTools) {
        response.setCharacterEncoding("UTF-8");
        return thingsAIChatService.chat(new ThingsChatRequest(chatId, model, prompt, onlineSearch, deepThink, enabledTools));
    }


    @DeleteMapping("/chat/{chatId}")
    public void clearChat(@PathVariable String chatId) {
        thingsAIChatService.clearChat(chatId);
    }

    @GetMapping("/models")
    public Result<Set<ThingsAiProperties.Models>> getModels() {
        return Result.success(thingsAiProperties.getModels());
    }
}
