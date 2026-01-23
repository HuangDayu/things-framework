package cn.huangdayu.things.rules;

import cn.huangdayu.things.api.rules.*;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.ERROR;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsRulesHandlerFactory {

    private final Map<String, ThingsRulesHandler> thingsRulesHandlerMap;
    private final ThingsRulesProperties thingsRulesProperties;
    private static ThingsRulesProperties thingsRulesPropertiesForStatic;
    private static final Table<String, Class<? extends ThingsRulesHandler>, ThingsRulesHandler> HANDLER_TABLE = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    public ThingsRulesHandlerFactory(Map<String, ThingsRulesHandler> thingsRulesHandlerMap, ThingsRulesProperties thingsRulesProperties) {
        this.thingsRulesHandlerMap = thingsRulesHandlerMap;
        this.thingsRulesProperties = thingsRulesProperties;
        ThingsRulesHandlerFactory.thingsRulesPropertiesForStatic = thingsRulesProperties;
    }

    @PostConstruct
    public void init() {
        thingsRulesHandlerMap.values().forEach(handler -> HANDLER_TABLE.put(handler.getType(), getHandlerClass(handler), handler));
    }


    public static Class<? extends ThingsRulesHandler> getHandlerClass(Object bean) {
        if (bean.getClass().getInterfaces().length == 1) {
            return (Class<? extends ThingsRulesHandler>) bean.getClass().getInterfaces()[0];
        }
        for (Class<?> anInterface : bean.getClass().getInterfaces()) {
            if (ThingsRulesHandler.class.isAssignableFrom(anInterface) && !ThingsRulesHandler.class.equals(anInterface)) {
                return (Class<? extends ThingsRulesHandler>) anInterface;
            }
        }
        throw new ThingsException(ERROR, "Not found Things Rules Handler");
    }

    public static <T extends ThingsRulesHandler> T getHandler(Class<T> handlerClassType) {
        String type = ThingsRulesHandlerFactory.thingsRulesPropertiesForStatic.getThingsRulesHandler().get(handlerClassType);
        if (StrUtil.isBlank(type)) {
            return getDefaultHandler(handlerClassType);
        }
        return (T) HANDLER_TABLE.get(type, handlerClassType);
    }

    public static <T extends ThingsRulesHandler> T getHandler(String type, Class<T> handlerClassType) {
        return (T) HANDLER_TABLE.get(type, handlerClassType);
    }

    public static <T extends ThingsRulesHandler> Map<String, T> getAllHandlers(Class<T> handlerClassType) {
        Map<String, ThingsRulesHandler> column = HANDLER_TABLE.getColumn(handlerClassType);
        return column.values().stream().map(thingsRulesHandler -> (T) thingsRulesHandler).collect(Collectors.toMap(ThingsRulesHandler::getType, v -> v));
    }

    public static <T extends ThingsRulesHandler> T getDefaultHandler(Class<T> handlerClassType) {
        Map<String, ThingsRulesHandler> column = HANDLER_TABLE.getColumn(handlerClassType);
        return column.values().stream().map(thingsRulesHandler -> (T) thingsRulesHandler).filter(ThingsRulesHandler::isDefault).findFirst().orElse(null);
    }

    public <T extends ThingsRulesHandler> T getPropertyHandler(Class<T> handlerClassType) {
        String type = thingsRulesProperties.getThingsRulesHandler().get(handlerClassType);
        if (StrUtil.isBlank(type)) {
            return getDefaultHandler(handlerClassType);
        }
        return (T) HANDLER_TABLE.get(type, handlerClassType);
    }

    /**
     * 获取执行条件检查器
     *
     * @param type 条件类型
     * @return 执行条件检查器，如果未找到返回null
     */
    public static ThingsRulesConditionChecker getChecker(String type) {
        return getHandler(type, ThingsRulesConditionChecker.class);
    }

    /**
     * 获取所有执行条件检查器
     *
     * @return 执行条件检查器映射
     */
    public static Map<String, ThingsRulesConditionChecker> getAllCheckers() {
        return getAllHandlers(ThingsRulesConditionChecker.class);
    }

    /**
     * 获取属性比较器
     *
     * @param operatorType 操作符
     * @return 属性比较器，如果未找到返回null
     */
    public static ThingsRulesPropertyComparator getComparator(String operatorType) {
        return getHandler(operatorType, ThingsRulesPropertyComparator.class);
    }

    /**
     * 获取所有属性比较器
     *
     * @return 获取所有属性比较器
     */
    public static Map<String, ThingsRulesPropertyComparator> getAllComparator() {
        return getAllHandlers(ThingsRulesPropertyComparator.class);
    }

    /**
     * 获取动作执行器
     *
     * @param actionType 动作类型
     * @return 动作执行器，如果未找到返回null
     */
    public static ThingsRulesActionExecutor getActionExecutor(String actionType) {
        return getHandler(actionType, ThingsRulesActionExecutor.class);
    }

    /**
     * 获取触发器处理器
     *
     * @param type 处理器类型
     * @return 触发器处理器，如果未找到返回null
     */
    public static ThingsRulesTriggerProcessor getTriggerProcessor(String type) {
        return getHandler(type, ThingsRulesTriggerProcessor.class);
    }

    /**
     * 获取所有动作执行器
     *
     * @return 动作执行器映射
     */
    public static Map<String, ThingsRulesActionExecutor> getAllActionExecutor() {
        return getAllHandlers(ThingsRulesActionExecutor.class);
    }

    /**
     * 获取触发器匹配器
     *
     * @param triggerType 触发器类型
     * @return 触发器匹配器，如果未找到返回null
     */
    public static ThingsRulesTriggerMatcher getTriggerMatcher(String triggerType) {
        return getHandler(triggerType, ThingsRulesTriggerMatcher.class);
    }


    /**
     * 获取所有触发器匹配器
     *
     * @return 触发器匹配器映射
     */
    public static Map<String, ThingsRulesTriggerMatcher> getAllTriggerMatcher() {
        return getAllHandlers(ThingsRulesTriggerMatcher.class);
    }

}
