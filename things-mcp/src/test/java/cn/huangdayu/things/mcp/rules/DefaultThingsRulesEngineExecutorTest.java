package cn.huangdayu.things.mcp.rules;

import cn.huangdayu.things.api.rules.ThingsRulesEngineExecutor;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
import cn.huangdayu.things.mcp.ThingsMcpApplication;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 规则引擎单元测试
 *
 * @author huangdayu
 */
@SpringBootTest(classes = ThingsMcpApplication.class)
public class DefaultThingsRulesEngineExecutorTest {

    @Resource
    private ThingsRulesEngineExecutor thingsRulesEngineExecutor;
    
    @Test
    public void testSimpleRuleExecution() throws Exception {
        // 创建一个简单的规则用于测试
        ThingsRules thingsRules = createSimpleRule();
        
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
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(thingsRules, message);
        
        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getResult());
        assertNull(response.getError());
    }
    
    @Test
    public void testDisabledRule() throws Exception {
        // 创建一个简单的规则并禁用
        ThingsRules thingsRules = createSimpleRule();
        thingsRules.setStatus("disabled");
        
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
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(thingsRules, message);
        
        // 验证结果
        assertNotNull(response);
        assertNull(response.getResult());
        assertNotNull(response.getError());
        assertEquals("Rule is disabled", response.getError().getMessage());
    }
    
    private ThingsRules createSimpleRule() {
        ThingsRules thingsRules = new ThingsRules();
        thingsRules.setId("simple-rule-" + UUID.randomUUID().toString());
        thingsRules.setName("Simple Test Rule");
        thingsRules.setDescription("Simple rule for unit testing");
        thingsRules.setStatus("enabled");
        
        // 创建触发器
        ThingsRules.Trigger trigger = new ThingsRules.Trigger();
        trigger.setType("device");
        
        ThingsRules.TriggerCondition condition = new ThingsRules.TriggerCondition();
        condition.setProperty("temperature");
        condition.setOperator(">");
        condition.setValue(30);
        
        ThingsRules.DeviceInfo deviceInfo = new ThingsRules.DeviceInfo();
        deviceInfo.setProductCode("AC_PROD_001");
        deviceInfo.setDeviceCode("AC001");
        deviceInfo.setMessageType("properties");
        deviceInfo.setIdentifier("temperature");
        deviceInfo.setAction("post");
        condition.setDeviceInfo(deviceInfo);
        
        trigger.setCondition(condition);
        thingsRules.setTriggers(java.util.Collections.singletonList(trigger));
        
        // 创建动作
        ThingsRules.Action action = new ThingsRules.Action();
        action.setType("device_control");
        
        ThingsRules.ActionParams actionParams = new ThingsRules.ActionParams();
        ThingsRules.DeviceControlParams deviceControl = new ThingsRules.DeviceControlParams();
        ThingsRules.DeviceInfo targetDevice = new ThingsRules.DeviceInfo();
        targetDevice.setProductCode("CURTAIN_PROD_001");
        targetDevice.setDeviceCode("CURT001");
        targetDevice.setMessageType("actions");
        targetDevice.setIdentifier("controlCurtain");
        targetDevice.setAction("request");
        deviceControl.setTargetDevice(targetDevice);
        deviceControl.setService("controlCurtain");
        actionParams.setDeviceControl(deviceControl);
        action.setParams(actionParams);
        thingsRules.setActions(java.util.Collections.singletonList(action));
        
        return thingsRules;
    }
}