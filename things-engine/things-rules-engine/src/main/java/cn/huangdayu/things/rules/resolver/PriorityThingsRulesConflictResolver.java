package cn.huangdayu.things.rules.resolver;

import cn.huangdayu.things.api.rules.ThingsRulesConflictResolver;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huangdayu
 */
@ThingsBean
public class PriorityThingsRulesConflictResolver implements ThingsRulesConflictResolver {
    @Override
    public Set<ThingsRules> resolve(Set<ThingsRules> triggeredRules) {
        return triggeredRules.stream()
                .sorted(Comparator.comparingInt(ThingsRules::getPriority).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String getType() {
        return "priority";
    }
}
