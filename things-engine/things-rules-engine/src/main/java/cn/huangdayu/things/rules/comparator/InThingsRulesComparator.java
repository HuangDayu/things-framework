package cn.huangdayu.things.rules.comparator;

import cn.huangdayu.things.api.rules.ThingsRulesPropertyComparator;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.hutool.core.convert.Convert;

import java.util.Collection;

/**
 * 包含比较器
 * 负责执行属性值包含目标值的比较操作
 *
 * @author huangdayu
 */
@ThingsBean
public class InThingsRulesComparator implements ThingsRulesPropertyComparator {

    /**
     * 执行包含比较
     * 检查属性值是否包含目标值
     *
     * @param condition     触发条件对象
     * @param propertyValue 属性值
     * @return 如果属性值小于目标值返回true，否则返回false
     */
    @Override
    public boolean compare(ThingsRules.TriggerCondition condition, Object propertyValue) {
        return Convert.convert(Collection.class, condition.getValue()).contains(propertyValue);
    }

    /**
     * 获取操作符
     *
     * @return 操作符字符串
     */
    @Override
    public String getType() {
        return "in";
    }
}