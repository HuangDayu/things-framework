package cn.huangdayu.things.mcp.rules;

import cn.huangdayu.things.api.rules.ThingsRulesEngineExecutor;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
import cn.huangdayu.things.mcp.ThingsMcpApplication;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 联动规则引擎测试类
 * 专门测试things-linkage目录中的规则
 *
 * @author huangdayu
 */
@SpringBootTest(classes = ThingsMcpApplication.class)
public class LinkageDefaultThingsRulesEngineExecutorTest {

    @Resource
    private ThingsRulesEngineExecutor thingsRulesEngineExecutor;
    
    @Test
    public void testAirConditionerCurtainLinkageRule() throws Exception {
        // 从JSON文件加载空调窗帘联动规则
        List<ThingsRules> thingsRules = loadRulesFromJsonFile("/things-linkage/air_conditioner_curtain_linkage.json");
        ThingsRules thingsRule = thingsRules.get(0); // 取第一个规则
        
        // 创建温度过高的消息
        ThingsRequestMessage message = new ThingsRequestMessage();
        message.setId(UUID.randomUUID().toString());
        message.setJsonrpc("2.0");
        message.setMethod("AC_PROD_001/AC001/properties/temperature/post");
        message.setTime(System.currentTimeMillis());
        message.setTimeout(5000);
        
        JSONObject params = new JSONObject();
        params.put("temperature", 35.0);
        message.setParams(params);
        
        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(thingsRule, message);
        
        // 验证结果
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }
    
    @Test
    public void testFanAirConditionerLinkageRule() throws Exception {
        // 从JSON文件加载风扇空调联动规则（包含多个规则）
        List<ThingsRules> thingsRules = loadRulesFromJsonFile("/things-linkage/fan_air_conditioner_linkage.json");
        
        // 测试第一个规则 - 温度高于28度时开启风扇
        ThingsRules thingsRules1 = thingsRules.get(0);
        ThingsRequestMessage message1 = new ThingsRequestMessage();
        message1.setId(UUID.randomUUID().toString());
        message1.setJsonrpc("2.0");
        message1.setMethod("AC_PROD_001/AC001/properties/temperature/post");
        message1.setTime(System.currentTimeMillis());
        message1.setTimeout(5000);
        
        JSONObject params1 = new JSONObject();
        params1.put("temperature", 29.0);
        message1.setParams(params1);
        
        ThingsResponseMessage response1 = thingsRulesEngineExecutor.executeRule(thingsRules1, message1);
        assertNotNull(response1);
        assertTrue(response1.isSuccess());

        // 测试第二个规则 - 复合触发器规则
        ThingsRules thingsRules2 = thingsRules.get(1);
        
        // 先触发风扇开启状态（满足第一个条件）
        ThingsRequestMessage fanPowerOnMessage = new ThingsRequestMessage();
        fanPowerOnMessage.setId(UUID.randomUUID().toString());
        fanPowerOnMessage.setJsonrpc("2.0");
        fanPowerOnMessage.setMethod("FAN_PROD_001/FAN001/properties/powerSwitch/post");
        fanPowerOnMessage.setTime(System.currentTimeMillis());
        fanPowerOnMessage.setTimeout(5000);
        
        JSONObject fanPowerOnParams = new JSONObject();
        fanPowerOnParams.put("powerSwitch", true);
        fanPowerOnMessage.setParams(fanPowerOnParams);
        
        // 执行风扇开启消息，标记第一个条件满足
        ThingsResponseMessage fanPowerOnResponse = thingsRulesEngineExecutor.executeRule(thingsRules2, fanPowerOnMessage);
        assertNotNull(fanPowerOnResponse);
        assertFalse(fanPowerOnResponse.isSuccess());
        
        // 再触发温度高于30度的消息（满足第二个条件）
        ThingsRequestMessage tempMessage = new ThingsRequestMessage();
        tempMessage.setId(UUID.randomUUID().toString());
        tempMessage.setJsonrpc("2.0");
        tempMessage.setMethod("AC_PROD_001/AC001/properties/temperature/post");
        tempMessage.setTime(System.currentTimeMillis());
        tempMessage.setTimeout(5000);
        
        JSONObject tempParams = new JSONObject();
        tempParams.put("temperature", 31.0);
        tempMessage.setParams(tempParams);
        
        // 执行规则，现在应该两个条件都满足了
        ThingsResponseMessage response2 = thingsRulesEngineExecutor.executeRule(thingsRules2, tempMessage);
        assertNotNull(response2);
        assertTrue(response2.isSuccess());
    }
    
    @Test
    public void testAwayFromHomeLinkageRule() throws Exception {
        // 从JSON文件加载离家场景联动规则
        List<ThingsRules> thingsRules = loadRulesFromJsonFile("/things-linkage/away_from_home_linkage.json");
        ThingsRules thingsRule = thingsRules.get(0); // 取第一个规则
        
        // 创建用户离家事件消息
        ThingsRequestMessage message = new ThingsRequestMessage();
        message.setId(UUID.randomUUID().toString());
        message.setJsonrpc("2.0");
        message.setMethod("GATEWAY_PROD_001/GATEWAY001/events/userAway/post");
        message.setTime(System.currentTimeMillis());
        message.setTimeout(5000);
        
        JSONObject params = new JSONObject();
        params.put("userId", "user001");
        params.put("time", System.currentTimeMillis());
        params.put("event", "userAway"); // 添加事件参数
        message.setParams(params);
        
        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(thingsRule, message);
        
        // 验证结果
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }
    
    @Test
    public void testComeHomeLinkageRule() throws Exception {
        // 从JSON文件加载回家场景联动规则
        List<ThingsRules> thingsRules = loadRulesFromJsonFile("/things-linkage/come_home_linkage.json");
        ThingsRules thingsRule = thingsRules.get(0); // 取第一个规则
        
        // 创建用户回家事件消息
        ThingsRequestMessage message = new ThingsRequestMessage();
        message.setId(UUID.randomUUID().toString());
        message.setJsonrpc("2.0");
        message.setMethod("GATEWAY_PROD_001/GATEWAY001/events/userComeHome/post");
        message.setTime(System.currentTimeMillis());
        message.setTimeout(5000);
        
        JSONObject params = new JSONObject();
        params.put("userId", "user001");
        params.put("time", System.currentTimeMillis());
        params.put("event", "userComeHome"); // 添加事件参数
        message.setParams(params);
        
        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(thingsRule, message);
        
        // 验证结果
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }
    
    @Test
    public void testEnergySavingLinkageRule() throws Exception {
        // 从JSON文件加载节能模式联动规则（包含多个规则）
        List<ThingsRules> thingsRules = loadRulesFromJsonFile("/things-linkage/energy_saving_linkage.json");
        
        // 测试第一个规则（夜间节能模式）
        ThingsRules thingsRules1 = thingsRules.get(0);
        
        // 修改执行条件以适应当前时间
        ThingsRules.ExecutionCondition execCondition1 = new ThingsRules.ExecutionCondition();
        execCondition1.setDaysOfWeek(List.of(1, 2, 3, 4, 5, 6, 7)); // 允许所有天
        
        // 设置一个包含当前时间的时间范围
        ThingsRules.TimeRange timeRange1 = new ThingsRules.TimeRange();
        timeRange1.setStart("00:00:00");
        timeRange1.setEnd("23:59:59");
        execCondition1.setTimeRange(timeRange1);
        thingsRules1.setExecutionCondition(execCondition1);
        
        ThingsRequestMessage message1 = new ThingsRequestMessage();
        message1.setId(UUID.randomUUID().toString());
        message1.setJsonrpc("2.0");
        message1.setMethod("TIMER");
        message1.setTime(System.currentTimeMillis());
        message1.setTimeout(5000);
        
        JSONObject params1 = new JSONObject();
        message1.setParams(params1);
        
        ThingsResponseMessage response1 = thingsRulesEngineExecutor.executeRule(thingsRules1, message1);
        assertNotNull(response1);
        assertTrue(response1.isSuccess());

        // 测试第二个规则（空闲设备自动断电）
        ThingsRules thingsRules2 = thingsRules.get(1);
        
        // 修改执行条件以适应当前时间
        ThingsRules.ExecutionCondition execCondition2 = new ThingsRules.ExecutionCondition();
        execCondition2.setDaysOfWeek(List.of(1, 2, 3, 4, 5, 6, 7)); // 允许所有天
        
        // 设置一个包含当前时间的时间范围
        ThingsRules.TimeRange timeRange2 = new ThingsRules.TimeRange();
        timeRange2.setStart("00:00:00");
        timeRange2.setEnd("23:59:59");
        execCondition2.setTimeRange(timeRange2);
        thingsRules2.setExecutionCondition(execCondition2);
        
        ThingsRequestMessage message2 = new ThingsRequestMessage();
        message2.setId(UUID.randomUUID().toString());
        message2.setJsonrpc("2.0");
        message2.setMethod("SOCKET_PROD_001/SOCK001/properties/power/post");
        message2.setTime(System.currentTimeMillis());
        message2.setTimeout(5000);
        
        JSONObject params2 = new JSONObject();
        params2.put("power", 3.0);
        message2.setParams(params2);
        
        ThingsResponseMessage response2 = thingsRulesEngineExecutor.executeRule(thingsRules2, message2);
        assertNotNull(response2);
        assertTrue(response2.isSuccess());
    }
    
    @Test
    public void testDisabledRule() throws Exception {
        // 从JSON文件加载空调窗帘联动规则并禁用
        List<ThingsRules> thingsRules = loadRulesFromJsonFile("/things-linkage/air_conditioner_curtain_linkage.json");
        ThingsRules thingsRule = thingsRules.get(0); // 取第一个规则
        thingsRule.setStatus("disabled");

        // 创建温度过高的消息
        ThingsRequestMessage message = new ThingsRequestMessage();
        message.setId(UUID.randomUUID().toString());
        message.setJsonrpc("2.0");
        message.setMethod("AC_PROD_001/AC001/properties/temperature/post");
        message.setTime(System.currentTimeMillis());
        message.setTimeout(5000);

        JSONObject params = new JSONObject();
        params.put("temperature", 35.0);
        message.setParams(params);

        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(thingsRule, message);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Rule is disabled", response.getError().getMessage());
    }

    public static List<ThingsRules> loadRulesFromJsonFile(String resourcePath) throws Exception {
        try (InputStream inputStream = LinkageDefaultThingsRulesEngineExecutorTest.class.getResourceAsStream(resourcePath)) {
            assertNotNull(inputStream, "Failed to load resource: " + resourcePath);
            String jsonString = new String(inputStream.readAllBytes());
            JSONObject jsonObject = JSON.parseObject(jsonString);
            
            // 获取rules数组
            List<ThingsRules> thingsRules = new ArrayList<>();
            for (int i = 0; i < jsonObject.getJSONArray("rules").size(); i++) {
                JSONObject ruleJson = jsonObject.getJSONArray("rules").getJSONObject(i);
                ThingsRules thingsRule = JSON.toJavaObject(ruleJson, ThingsRules.class);
                thingsRules.add(thingsRule);
            }
            
            return thingsRules;
        }
    }
}