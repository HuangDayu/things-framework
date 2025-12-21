package cn.huangdayu.things.common.dsl.rules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 规则实体类
 * 定义了物联网规则引擎中的规则结构，包括触发器、动作和执行条件等核心组件
 *
 * @author huangdayu
 */
@Data
public class ThingsRules {

    /**
     * 规则ID
     * 用于唯一标识一个规则
     */
    private String id;

    /**
     * 规则名称
     * 规则的可读名称
     */
    private String name;

    /**
     * 规则描述
     * 对规则功能的详细描述
     */
    private String description;

    /**
     * 规则状态 (enabled/disabled)
     * 控制规则是否启用
     */
    private String status;

    /**
     * 规则优先级
     */
    private int priority;

    /**
     * 触发器列表
     * 定义能够触发规则执行的条件集合
     */
    private List<Trigger> triggers;

    /**
     * 动作列表
     * 定义规则触发后需要执行的动作集合
     */
    private List<Action> actions;

    /**
     * 执行条件
     * 定义规则执行需要满足的额外条件，如时间范围、执行次数限制等
     */
    private ExecutionCondition executionCondition;

    /**
     * 触发器定义
     * 描述能够触发规则执行的条件
     */
    @Data
    public static class Trigger {

        /**
         * 触发器类型
         * 可选值: device(设备属性变化)、event(设备事件)、timer(定时器)、composite(复合条件)
         */
        private String type;

        /**
         * 触发条件
         * 具体的触发条件定义
         */
        private TriggerCondition condition;
    }

    /**
     * 触发条件定义
     * 包含触发规则执行的具体条件信息
     */
    @Data
    public static class TriggerCondition {

        /**
         * 触发器类型
         * 可选值: device(设备属性变化)、event(设备事件)、timer(定时器)、composite(复合条件)
         * 用于在复合触发器的子条件中指定类型
         */
        private String type;

        /**
         * 设备信息
         * 定义与设备相关的触发条件
         */
        private DeviceInfo deviceInfo;

        /**
         * 属性名称
         * 设备属性触发器中使用的属性名
         */
        private String property;

        /**
         * 比较操作符
         * 属性值与目标值的比较方式，如 >、<、==、>=、<=、!=
         */
        private String operator;

        /**
         * 目标值
         * 用于与属性值进行比较的目标值
         */
        private Object value;

        /**
         * 事件名称
         * 事件触发器中使用的事件名
         */
        private String event;

        /**
         * Cron表达式
         * 定时器触发器中使用的时间表达式
         */
        private String cron;

        /**
         * 逻辑操作符
         * 复合触发器中使用的逻辑操作符，如 AND、OR
         */
        private String logicOperator;

        /**
         * 子条件列表
         * 复合触发器中的子条件集合
         */
        private List<TriggerCondition> conditions;

        /**
         * 条件超时时间
         */
        private Long timeout;

        /**
         * 外部数据配置
         */
        private ExternalData externalData;
    }

    /**
     * 外部数据配置
     * 用于指定从外部数据源获取数据的配置信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalData {
        /**
         * 数据源类型
         * 如: database, cache, api, file等
         */
        private String source;

        /**
         * 数据查询语句或标识符
         * 如SQL语句、API端点、缓存键等
         */
        private String query;

        /**
         * 查询参数
         * 用于参数化查询
         */
        private Map<String, Object> parameters;

        /**
         * 数据映射配置
         * 定义如何将外部数据映射到条件比较中
         */
        private DataMapping mapping;
    }

    /**
     * 数据映射配置
     * 定义如何将外部数据映射到条件比较中
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataMapping {
        /**
         * 数据字段路径
         * 用于从复杂数据结构中提取特定字段
         */
        private String fieldPath;

        /**
         * 数据转换器
         * 用于在比较前对数据进行转换
         */
        private String converter;
    }

    /**
     * 设备信息定义
     * 描述设备的基本信息
     */
    @Data
    public static class DeviceInfo {

        /**
         * 产品编码
         * 设备所属产品的唯一标识
         */
        private String productCode;

        /**
         * 设备编码
         * 设备的唯一标识
         */
        private String deviceCode;

        /**
         * 消息类型
         * 消息的类型，如 properties(属性)、events(事件)、actions(动作)
         */
        private String messageType;

        /**
         * 标识符
         * 消息的标识符
         */
        private String identifier;

        /**
         * 动作类型
         * 消息的动作类型，如 post(上报)、request(请求)
         */
        private String action;
    }

    /**
     * 动作定义
     * 描述规则触发后需要执行的动作
     */
    @Data
    public static class Action {

        /**
         * 动作类型
         * 可选值: device_control(设备控制)、notification(通知)、delay(延时)、
         * property_set(属性设置)、data_forward(数据转发)、scene_trigger(场景触发)、
         * rule_status(规则状态)、condition(条件)
         */
        private String type;

        /**
         * 动作参数
         * 动作执行所需的具体参数
         */
        private ActionParams params;
    }

    /**
     * 动作参数定义
     * 包含各种类型动作执行所需的参数
     */
    @Data
    public static class ActionParams {

        /**
         * 设备控制参数
         */
        private DeviceControlParams deviceControl;

        /**
         * 通知参数
         */
        private NotificationParams notification;

        /**
         * 延时参数
         */
        private DelayParams delay;

        /**
         * 属性设置参数
         */
        private PropertySetParams propertySet;

        /**
         * 数据转发参数
         */
        private DataForwardParams dataForward;

        /**
         * 场景触发参数
         */
        private SceneTriggerParams sceneTrigger;

        /**
         * 规则状态参数
         */
        private RuleStatusParams ruleStatus;

        /**
         * 条件参数
         */
        private ConditionParams condition;
    }

    /**
     * 设备控制参数
     * 定义设备控制动作所需的参数
     */
    @Data
    public static class DeviceControlParams {

        /**
         * 目标设备信息
         */
        private DeviceInfo targetDevice;

        /**
         * 服务名称
         */
        private String service;

        /**
         * 参数映射
         */
        private Map<String, Object> params;
    }

    /**
     * 通知参数
     * 定义发送通知动作所需的参数
     */
    @Data
    public static class NotificationParams {

        /**
         * 通知类型
         */
        private String type;

        /**
         * 通知标题
         */
        private String title;

        /**
         * 通知内容
         */
        private String content;

        /**
         * 接收者列表
         */
        private List<String> recipients;
    }

    /**
     * 延时参数
     * 定义延时动作所需的参数
     */
    @Data
    public static class DelayParams {

        /**
         * 延时持续时间(秒)
         */
        private Integer duration;
    }

    /**
     * 属性设置参数
     * 定义属性设置动作所需的参数
     */
    @Data
    public static class PropertySetParams {

        /**
         * 目标设备信息
         */
        private String targetDevice;

        /**
         * 属性映射
         */
        private Map<String, Object> properties;
    }

    /**
     * 数据转发参数
     * 定义数据转发动作所需的参数
     */
    @Data
    public static class DataForwardParams {

        /**
         * 目标地址
         */
        private String targetUrl;

        /**
         * 转发数据
         */
        private Object data;
    }

    /**
     * 场景触发参数
     * 定义场景触发动作所需的参数
     */
    @Data
    public static class SceneTriggerParams {

        /**
         * 场景ID
         */
        private String sceneId;
    }

    /**
     * 规则状态参数
     * 定义规则状态修改动作所需的参数
     */
    @Data
    public static class RuleStatusParams {

        /**
         * 规则ID
         */
        private String ruleId;

        /**
         * 新状态
         */
        private String status;
    }

    /**
     * 条件参数
     * 定义条件动作所需的参数
     */
    @Data
    public static class ConditionParams {

        /**
         * 条件表达式
         */
        private String expression;
    }

    /**
     * 执行条件定义
     * 定义规则执行需要满足的额外条件
     */
    @Data
    public static class ExecutionCondition {

        /**
         * 时间范围条件
         */
        private TimeRange timeRange;

        /**
         * 星期几条件
         */
        private List<Integer> daysOfWeek;

        /**
         * 执行次数限制条件
         */
        private ExecutionLimit executionLimit;
    }

    /**
     * 时间范围定义
     * 定义规则可执行的时间范围
     */
    @Data
    public static class TimeRange {

        /**
         * 开始时间 (格式: HH:mm:ss)
         */
        private String start;

        /**
         * 结束时间 (格式: HH:mm:ss)
         */
        private String end;
    }

    /**
     * 执行次数限制定义
     * 定义规则在特定时间窗口内的执行次数限制
     */
    @Data
    public static class ExecutionLimit {

        /**
         * 最大执行次数
         */
        private Integer count;

        /**
         * 时间周期 (格式: 数字+单位，如 1H 表示1小时)
         */
        private String period;
    }
}