package cn.huangdayu.things.api.rules;

import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import com.alibaba.fastjson2.JSONObject;

/**
 * 外部数据源访问器接口
 * 用于从不同类型的外部数据源获取数据以支持条件匹配
 */
public interface ThingsRulesExternalAccessor extends ThingsRulesHandler {

    /**
     * 从外部数据源获取数据
     *
     * @param externalData 外部数据配置
     * @param message      当前消息对象
     * @return 获取到的数据
     * @throws Exception 获取数据时可能发生的异常
     */
    JSONObject accessor(ThingsRules.ExternalData externalData, ThingsRequestMessage message) throws Exception;


}