package cn.huangdayu.things.mcp.rules;

import cn.huangdayu.things.api.rules.ThingsRulesEngineExecutor;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.mcp.ThingsMcpApplication;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static cn.huangdayu.things.mcp.rules.LinkageDefaultThingsRulesEngineExecutorTest.loadRulesFromJsonFile;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 跨时间条件处理测试
 * 演示如何处理在不同时间点满足的规则条件
 */
@SpringBootTest(classes = ThingsMcpApplication.class)
public class CrossTimeConditionsTest {

    @Resource
    private ThingsRulesEngineExecutor thingsRulesEngineExecutor;

    @Test
    public void testCrossTimeConditions() throws Exception {
        List<ThingsRules> thingsRules = loadRulesFromJsonFile("/things-linkage/cross_time_conditions_example.json");
        ThingsRules thingsRule = thingsRules.get(0);
        processFirstCondition(thingsRule);
        processSecondCondition(thingsRule);
        assertTrue(thingsRulesEngineExecutor.executeRule(thingsRule, createDummyMessage()).isSuccess());
    }

    private void processFirstCondition(ThingsRules thingsRules) {
        ThingsRequestMessage message = createUserAwayMessage();
        thingsRulesEngineExecutor.executeRule(thingsRules, message);
    }

    private void processSecondCondition(ThingsRules thingsRules) {
        ThingsRequestMessage message = createTemperatureMessage();
        thingsRulesEngineExecutor.executeRule(thingsRules, message);
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