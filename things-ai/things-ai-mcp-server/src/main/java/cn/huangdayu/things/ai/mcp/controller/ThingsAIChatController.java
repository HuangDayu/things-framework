package cn.huangdayu.things.ai.mcp.controller;

import cn.huangdayu.things.ai.mcp.service.ThingsAIChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "Chat APIs")
@RequestMapping("/api/v1")
public class ThingsAIChatController {

    private final ThingsAIChatService thingsAIChatService;

    @PostMapping("/chat")
    @Operation(summary = "DashScope Flux Chat")
    public Flux<String> chat(HttpServletResponse response,
                             HttpServletRequest request,
                             @Validated @RequestBody String prompt,
                             @RequestHeader(value = "model", required = false) String model,
                             @RequestHeader(value = "chatId", required = false, defaultValue = "things-ai-chat") String chatId) {
        response.setCharacterEncoding("UTF-8");
        return thingsAIChatService.chat(chatId, model, prompt);
    }

    @PostMapping("/deep-thinking/chat")
    public Flux<String> deepThinkingChat(HttpServletResponse response,
                                         HttpServletRequest request,
                                         @Validated @RequestBody String prompt,
                                         @RequestHeader(value = "model", required = false) String model,
                                         @RequestHeader(value = "chatId", required = false, defaultValue = "things-ai-deep-think-chat") String chatId) {
        response.setCharacterEncoding("UTF-8");
        return thingsAIChatService.deepThinkingChat(chatId, model, prompt);
    }


    @PostMapping("/search")
    public Flux<String> search(
            HttpServletResponse response,
            @Validated @RequestBody String prompt,
            @RequestHeader(value = "model", required = false, defaultValue = "qwen3:1.7b") String model,
            @RequestHeader(value = "chatId", required = false, defaultValue = "things-ai-search-chat") String chatId) {
        response.setCharacterEncoding("UTF-8");
        return thingsAIChatService.search(chatId, model, prompt);
    }
}
