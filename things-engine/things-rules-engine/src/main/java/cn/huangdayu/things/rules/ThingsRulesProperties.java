package cn.huangdayu.things.rules;

import cn.huangdayu.things.api.rules.ThingsRulesHandler;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangdayu
 */
@Data
public class ThingsRulesProperties {

    private Map<Class<? extends ThingsRulesHandler>, String> thingsRulesHandler = new ConcurrentHashMap<>();

}
