package cn.huangdayu.things.api.rules;

import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.common.message.ThingsRequestMessage;

import java.util.Set;

/**
 * 规则模板加载器
 *
 * @author huangdayu
 */
public interface ThingsRulesTemplateLoader extends ThingsRulesHandler {

    /**
     * 加载规则
     *
     * @param trm
     * @return
     */
    Set<ThingsRules> loader(ThingsRequestMessage trm);


    /**
     * 加载规则
     *
     * @param thingsRulesId
     * @return
     */
    ThingsRules loader(String thingsRulesId);

}
