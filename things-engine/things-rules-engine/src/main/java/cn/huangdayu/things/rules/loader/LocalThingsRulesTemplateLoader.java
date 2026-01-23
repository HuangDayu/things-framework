package cn.huangdayu.things.rules.loader;

import cn.huangdayu.things.api.rules.ThingsRulesTemplateLoader;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 本地规则模板加载器
 * 基于内存存储和加载规则模板
 * 支持规则的注册、查询和匹配
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class LocalThingsRulesTemplateLoader implements ThingsRulesTemplateLoader {

    private final Map<String, ThingsRules> rulesCache = new ConcurrentHashMap<>();

    @Override
    public Set<ThingsRules> loader(ThingsRequestMessage trm) {
        String method = trm.getMethod();
        return rulesCache.values().stream()
                .filter(rule -> isRuleMatchMessage(rule, method, trm))
                .collect(Collectors.toSet());
    }

    @Override
    public ThingsRules loader(String thingsRulesId) {
        return rulesCache.get(thingsRulesId);
    }

    @Override
    public String getType() {
        return "local_loader";
    }

    public void registerRule(ThingsRules rule) {
        if (rule != null && rule.getId() != null) {
            rulesCache.put(rule.getId(), rule);
            log.info("Registered rule: {} - {}", rule.getId(), rule.getName());
        }
    }

    public void unregisterRule(String ruleId) {
        if (ruleId != null) {
            ThingsRules removed = rulesCache.remove(ruleId);
            if (removed != null) {
                log.info("Unregistered rule: {}", ruleId);
            }
        }
    }

    public void clearRules() {
        rulesCache.clear();
        log.info("Cleared all rules");
    }

    public int getRulesCount() {
        return rulesCache.size();
    }

    private boolean isRuleMatchMessage(ThingsRules rule, String method, ThingsRequestMessage trm) {
        if (rule.getTriggers() == null || rule.getTriggers().isEmpty()) {
            return false;
        }
        return rule.getTriggers().stream().anyMatch(trigger -> matchesMethod(trigger, method));
    }

    private boolean matchesMethod(ThingsRules.Trigger trigger, String method) {
        if (trigger.getCondition() == null || trigger.getCondition().getDeviceInfo() == null) {
            return false;
        }
        ThingsRules.DeviceInfo deviceInfo = trigger.getCondition().getDeviceInfo();
        String expectedMethod = buildExpectedMethod(deviceInfo);
        return expectedMethod.equals(method);
    }

    private String buildExpectedMethod(ThingsRules.DeviceInfo deviceInfo) {
        return String.format("%s/%s/%s/%s/%s",
                deviceInfo.getProductCode(),
                deviceInfo.getDeviceCode(),
                deviceInfo.getMessageType(),
                deviceInfo.getIdentifier(),
                deviceInfo.getAction());
    }
}
