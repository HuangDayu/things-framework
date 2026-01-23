package cn.huangdayu.things.api.rules;

/**
 * 规则处理器接口
 * 定义规则处理组件的标准接口
 * 支持多种实现方式（本地、远程、分布式等）
 *
 * @author huangdayu
 */
public interface ThingsRulesHandler {

    /**
     * 获取处理器类型标识
     * 用于区分不同类型的处理器实现
     *
     * @return 处理器类型字符串
     */
    String getType();

    /**
     * 检查处理器是否可用
     * 用于健康检查或功能验证
     *
     * @return true如果处理器可用，false否则
     */
    default boolean isAvailable() {
        return true;
    }

    /**
     * 获取处理器是否为默认处理器
     *
     * @return
     */
    default boolean isDefault() {
        return false;
    }
}
