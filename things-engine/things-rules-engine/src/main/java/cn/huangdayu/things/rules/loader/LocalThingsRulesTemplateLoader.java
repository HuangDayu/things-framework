package cn.huangdayu.things.rules.loader;

import cn.huangdayu.things.api.rules.ThingsRulesTemplateLoader;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class LocalThingsRulesTemplateLoader implements ThingsRulesTemplateLoader {
    @Override
    public Set<ThingsRules> loader(ThingsRequestMessage trm) {
        return Set.of();
    }

    @Override
    public ThingsRules loader(String thingsRulesId) {
        return null;
    }

    @Override
    public String getType() {
        return "local_loader";
    }
}
