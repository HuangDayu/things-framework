package cn.huangdayu.things.rules.comparator;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesPropertyComparator;

/**
 * 不等于比较器
 * 负责执行属性值不等于目标值的比较操作
 *
 * @author huangdayu
 */
@ThingsBean
public class NotEqualThingsRulesComparator implements ThingsRulesPropertyComparator {

    /**
     * 执行不等于比较
     * 检查属性值是否不等于目标值
     *
     * @param condition     触发条件对象
     * @param propertyValue 属性值
     * @return 如果属性值不等于目标值返回true，否则返回false
     */
    @Override
    public boolean compare(ThingsRules.TriggerCondition condition, Object propertyValue) {
        // 使用不等于操作符进行比较
        return !propertyValue.equals(condition.getValue());
    }

    /**
     * 获取操作符
     *
     * @return 操作符字符串
     */
    @Override
    public String getType() {
        return "!=";
    }
}