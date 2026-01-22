package cn.huangdayu.things.rules.test;

import cn.huangdayu.things.api.rules.ThingsRulesEngineExecutor;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * 跨时间条件处理测试
 * 演示如何处理在不同时间点满足的规则条件
 */
@SpringBootTest(classes = ThingsRulesTestApplication.class)
public class CrossTimeConditionsTest {

    @Resource
    private ThingsRulesEngineExecutor thingsRulesEngineExecutor;

    @Test
    public void testCrossTimeConditions() throws Exception {
        List<ThingsRules> thingsRules = LinkageDefaultThingsRulesEngineExecutorTest.loadRulesFromJsonFile("/things-linkage/cross_time_conditions_example.json");
        ThingsRules thingsRule = thingsRules.get(0);

        // 第一步：发送userAway事件，应该不触发规则（因为只满足了一个条件）
        ThingsResponseMessage response1 = processFirstCondition(thingsRule);
        assertFalse(response1.isSuccess()); // 第一个条件满足，但不是所有条件

        // 第二步：发送temperature消息，应该触发规则（因为现在两个条件都满足了）
        ThingsResponseMessage response2 = processSecondCondition(thingsRule);
        assertTrue(response2.isSuccess()); // 所有条件都满足，规则执行
    }

    private ThingsResponseMessage processFirstCondition(ThingsRules thingsRules) throws Exception {
        ThingsRequestMessage message = createUserAwayMessage();
        return thingsRulesEngineExecutor.executeRule(thingsRules, message);
    }

    private ThingsResponseMessage processSecondCondition(ThingsRules thingsRules) throws Exception {
        ThingsRequestMessage message = createTemperatureMessage();
        return thingsRulesEngineExecutor.executeRule(thingsRules, message);
    }

    private ThingsRequestMessage createDummyMessage() {
        ThingsRequestMessage message = createBaseMessage();
        message.setMethod("GATEWAY_PROD_001/GATEWAY001/events/dummy/post");

        JSONObject params = new JSONObject();
        params.put("event", "dummy");
        message.setParams(params);

        return message;
    }

    private ThingsRequestMessage createUserAwayMessage() {
        ThingsRequestMessage message = createBaseMessage();
        message.setMethod("GATEWAY_PROD_001/GATEWAY001/events/userAway/post");

        JSONObject params = new JSONObject();
        params.put("event", "userAway");
        message.setParams(params);

        return message;
    }

    private ThingsRequestMessage createTemperatureMessage() {
        ThingsRequestMessage message = createBaseMessage();
        message.setMethod("AC_PROD_001/AC001/properties/temperature/post");

        JSONObject params = new JSONObject();
        params.put("temperature", 28.0);
        message.setParams(params);

        return message;
    }

    private ThingsRequestMessage createBaseMessage() {
        ThingsRequestMessage message = new ThingsRequestMessage();
        message.setId(UUID.randomUUID().toString());
        message.setJsonrpc("2.0");
        message.setTime(System.currentTimeMillis());
        message.setTimeout(5000);
        return message;
    }
}