package cn.huangdayu.things.mcp.config;

import cn.huangdayu.things.common.annotation.ThingsTools;
import cn.huangdayu.things.mcp.properties.ThingsMcpProperties;
import com.alibaba.fastjson2.JSON;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author huangdayu
 */
@Slf4j
@Configuration
public class ThingMcpConfiguration {

    public static final Set<ToolCallback> THINGS_TOOL_CALLBACKS = new CopyOnWriteArraySet<>();

    public static final List<McpSchema.Resource> THINGS_RESOURCES = new ArrayList<>();


    static {
        THINGS_RESOURCES.add(McpSchema.Resource.builder().uri("classpath:/things-templates/things-template-dsl.json")
                .name("things-template-dsl").title("物模型物模板DSL规范").description("物模型物模板DSL规范").build());
        THINGS_RESOURCES.add(McpSchema.Resource.builder().uri("classpath:/things-templates/things-message-dsl.json")
                .name("things-message-dsl").title("物模型消息DSL规范").description("物模型消息DSL规范").build());
    }

    /**
     * 自动注册容器中所有注解了@ThingsTools的Bean的Tools
     *
     * @param applicationContext
     * @return
     */
    @Bean
    public ToolCallbackProvider thingsToolCallbackProvider(ApplicationContext applicationContext) {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(ThingsTools.class);
        Object[] array = beansWithAnnotation.values().stream().filter(v -> hasTools(v.getClass())).toArray();
        MethodToolCallbackProvider callbackProvider = MethodToolCallbackProvider.builder().toolObjects(array).build();
        THINGS_TOOL_CALLBACKS.addAll(List.of(callbackProvider.getToolCallbacks()));
        return callbackProvider;
    }

    private boolean hasTools(Class<?> clazz) {
        ThingsTools annotation = clazz.getAnnotation(ThingsTools.class);
        if (!annotation.enabled()) {
            return false;
        }
        List<Method> methods = new ArrayList<>();
        methods.addAll(List.of(clazz.getMethods()));
        methods.addAll(List.of(clazz.getDeclaredMethods()));
        return !methods.isEmpty() && methods.stream().anyMatch(m -> null != m.getAnnotation(Tool.class));
    }

    @Bean
    public List<McpServerFeatures.AsyncPromptSpecification> mcpPrompts(@Value("classpath:/things-prompts/things-assistant-prompts.md") Resource resource) {
        return List.of(thingsPrompts(resource));
    }


    public McpServerFeatures.AsyncPromptSpecification greetingPrompts() {
        var prompt = new McpSchema.Prompt("greeting", "A friendly greeting prompt", List.of(new McpSchema.PromptArgument("name", "The name to greet", true)));
        return new McpServerFeatures.AsyncPromptSpecification(prompt, (exchange, getPromptRequest) -> {
            String nameArgument = (String) getPromptRequest.arguments().getOrDefault("name", "friend");
            var userMessage = new McpSchema.PromptMessage(McpSchema.Role.USER, new McpSchema.TextContent("Hello " + nameArgument + "! How can I assist you today?"));
            return Mono.just(new McpSchema.GetPromptResult("A personalized greeting message", List.of(userMessage)));
        });
    }

    @SneakyThrows
    public McpServerFeatures.AsyncPromptSpecification thingsPrompts(Resource resource) {
        String prompts = resource.getContentAsString(StandardCharsets.UTF_8);
        var prompt = new McpSchema.Prompt("things-prompts", "物联网物模型上下文协议提示词", List.of());
        return new McpServerFeatures.AsyncPromptSpecification(prompt, (exchange, getPromptRequest) -> {
            McpSchema.PromptMessage userMessage = new McpSchema.PromptMessage(McpSchema.Role.ASSISTANT, new McpSchema.TextContent(prompts));
            return Mono.just(new McpSchema.GetPromptResult("动态的物联网物模型上下文协议提示词", List.of(userMessage)));
        });
    }

    @Bean
    public List<McpServerFeatures.AsyncResourceSpecification> mcpResources(ThingsMcpProperties thingsMcpProperties) {
        Set<String> resourceUris = new CopyOnWriteArraySet<>();
        Set<McpSchema.Resource> continueResourceUris = new CopyOnWriteArraySet<>();
        List<McpSchema.Resource> resources = JSON.parseArray(JSON.toJSONString(thingsMcpProperties.getResources()), McpSchema.Resource.class);
        List<McpServerFeatures.AsyncResourceSpecification> asyncResourceSpecifications = addResource(resourceUris, continueResourceUris, resources);
        asyncResourceSpecifications.addAll(addResource(resourceUris, continueResourceUris, THINGS_RESOURCES));
        continueResourceUris.forEach(resource -> log.warn("Things Resource [{}]/[{}] is repetitive.", resource.uri(), resource.name()));
        return asyncResourceSpecifications;
    }

    private List<McpServerFeatures.AsyncResourceSpecification> addResource(Set<String> addResourceUris, Set<McpSchema.Resource> continueResourceUris, List<McpSchema.Resource> resources) {
        List<McpServerFeatures.AsyncResourceSpecification> mcpResources = new CopyOnWriteArrayList<>();
        resources.forEach(resource -> {
            if (!addResourceUris.contains(resource.uri())) {
                mcpResources.add(mcpResources(resource));
                addResourceUris.add(resource.uri());
            } else {
                continueResourceUris.add(resource);
            }
        });
        return mcpResources;
    }

    @SneakyThrows
    public McpServerFeatures.AsyncResourceSpecification mcpResources(McpSchema.Resource resource) {
        String thingsDsl = readResource(resource.uri()).getContentAsString(StandardCharsets.UTF_8);
        return new McpServerFeatures.AsyncResourceSpecification(resource, (exchange, request) ->
                Mono.just(new McpSchema.ReadResourceResult(List.of(new McpSchema.TextResourceContents(request.uri(), resource.mimeType(), thingsDsl)))));
    }

    @SneakyThrows
    private Resource readResource(String uri) {
        if (uri.startsWith("classpath:")) {
            return new ClassPathResource(uri.replace("classpath:", ""));
        }
        if (uri.startsWith("file:")) {
            return new FileSystemResource(uri.replace("file:", ""));
        }
        return new FileUrlResource(uri);
    }

    @SneakyThrows
    public McpServerFeatures.AsyncResourceSpecification thingsDslResources(Resource resource) {
        String thingsDsl = resource.getContentAsString(StandardCharsets.UTF_8);
        var systemInfoResource = McpSchema.Resource.builder().uri(resource.getURI().toString()).name("things-dsl").title("物模型DSL规范").description("物模型DSL规范").build();
        return new McpServerFeatures.AsyncResourceSpecification(systemInfoResource, (exchange, request) ->
                Mono.just(new McpSchema.ReadResourceResult(List.of(new McpSchema.TextResourceContents(request.uri(), "application/json", thingsDsl)))));
    }

}
