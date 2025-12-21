package cn.huangdayu.things.rules.comparator;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesPropertyComparator;

/**
 * 大于比较器
 * 负责执行属性值大于目标值的比较操作
 *
 * @author huangdayu
 */
@ThingsBean
public class GreaterThingsRulesComparator implements ThingsRulesPropertyComparator {

    /**
     * 执行大于比较
     * 检查属性值是否大于目标值
     *
     * @param condition     触发条件对象
     * @param propertyValue 属性值
     * @return 如果属性值大于目标值返回true，否则返回false
     */
    @Override
    public boolean compare(ThingsRules.TriggerCondition condition, Object propertyValue) {
        // 检查属性值和目标值是否都是数字类型
        if (propertyValue instanceof Number && condition.getValue() instanceof Number) {
            // 执行数值比较
            return ((Number) propertyValue).doubleValue() > ((Number) condition.getValue()).doubleValue();
        }
        return false;
    }

    /**
     * 获取操作符
     *
     * @return 操作符字符串
     */
    @Override
    public String getType() {
        return ">";
    }
}