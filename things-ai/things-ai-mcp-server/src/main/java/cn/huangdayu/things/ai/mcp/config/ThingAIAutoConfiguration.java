package cn.huangdayu.things.ai.mcp.config;

import cn.huangdayu.things.common.annotation.ThingsTools;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author huangdayu
 */
@Configuration
public class ThingAIAutoConfiguration {

    public static final Set<ToolCallback> THINGS_TOOL_CALLBACKS = new CopyOnWriteArraySet<>();

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
        List<Method> methods = new ArrayList<>();
        methods.addAll(List.of(clazz.getMethods()));
        methods.addAll(List.of(clazz.getDeclaredMethods()));
        return !methods.isEmpty() && methods.stream().anyMatch(m -> null != m.getAnnotation(Tool.class));
    }

}
