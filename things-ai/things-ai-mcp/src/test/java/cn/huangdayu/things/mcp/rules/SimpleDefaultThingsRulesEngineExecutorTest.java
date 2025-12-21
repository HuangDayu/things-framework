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

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简单规则引擎测试
 *
 * @author huangdayu
 */
@SpringBootTest(classes = ThingsMcpApplication.class)
public class SimpleDefaultThingsRulesEngineExecutorTest {


    @Resource
    private ThingsRulesEngineExecutor thingsRulesEngineExecutor;

    @Test
    public void testRuleEngineExecution() throws Exception {


        // 创建规则
        ThingsRules thingsRules = new ThingsRules();
        thingsRules.setId("test-rule-001");
        thingsRules.setName("Test Rule");
        thingsRules.setDescription("Simple test rule");
        thingsRules.setStatus("enabled");

        // 创建简单的触发器
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
        thingsRules.setTriggers(Arrays.asList(trigger));

        // 创建简单的动作
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
        thingsRules.setActions(Arrays.asList(action));

        // 创建物模型消息
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


        // 创建禁用的规则
        ThingsRules thingsRules = new ThingsRules();
        thingsRules.setId("test-rule-002");
        thingsRules.setName("Disabled Test Rule");
        thingsRules.setDescription("Disabled test rule");
        thingsRules.setStatus("disabled");

        // 创建简单的触发器
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
        thingsRules.setTriggers(Arrays.asList(trigger));

        // 创建物模型消息
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
}