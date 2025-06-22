package cn.huangdayu.things.ai.mcp.controller;

import cn.huangdayu.things.ai.mcp.dto.Result;
import cn.huangdayu.things.ai.mcp.properties.ThingsAiProperties;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "Base APIs")
@RequestMapping("/api/v1")
public class ThingsAIBaseController {

    private final ThingsAiProperties thingsAiProperties;

    @GetMapping("/dashscope/getModels")
    public Result<Set<ThingsAiProperties.Models>> getDashScopeModels() {
        return Result.success(thingsAiProperties.getModels());
    }

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Spring AI Alibaba Playground is running......");
    }


}
