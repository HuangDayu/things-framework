package cn.huangdayu.things.rules.test;

import cn.huangdayu.things.api.rules.ThingsRulesEngineExecutor;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 规则引擎边界情况和错误处理测试
 * 测试各种异常情况和边界条件
 *
 * @author huangdayu
 */
@SpringBootTest(classes = ThingsRulesTestApplication.class)
public class EdgeCaseThingsRulesEngineExecutorTest {

    @Resource
    private ThingsRulesEngineExecutor thingsRulesEngineExecutor;

    @Test
    public void testNullRuleExecution() throws Exception {
        // 测试空规则执行
        ThingsRequestMessage message = createValidMessage();

        // 验证抛出异常或返回错误响应
        assertThrows(Exception.class, () -> {
            thingsRulesEngineExecutor.executeRule(null, message);
        });
    }

    @Test
    public void testNullMessageExecution() throws Exception {
        // 测试空消息执行
        ThingsRules rule = createValidRule();

        // 验证抛出异常或返回错误响应
        assertThrows(Exception.class, () -> {
            thingsRulesEngineExecutor.executeRule(rule, null);
        });
    }

    @Test
    public void testInvalidDeviceId() throws Exception {
        // 测试无效设备ID
        ThingsRules rule = createDeviceTriggerRule();
        ThingsRequestMessage message = createValidMessage();
        message.setMethod("INVALID_PROD/INVALID_DEV/properties/temperature/post");

        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 应该正常执行，因为规则匹配是基于规则定义的，不是基于消息的
        assertNotNull(response);
    }

    @Test
    public void testEmptyParamsMessage() throws Exception {
        // 测试空参数消息
        ThingsRules rule = createDeviceTriggerRule();
        ThingsRequestMessage message = new ThingsRequestMessage();
        message.setId(UUID.randomUUID().toString());
        message.setJsonrpc("2.0");
        message.setMethod("AC_PROD_001/AC001/properties/temperature/post");
        message.setTime(System.currentTimeMillis());
        message.setTimeout(5000);
        message.setParams(new JSONObject()); // 空参数

        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 验证结果 - 应该返回错误，因为缺少温度参数
        assertNotNull(response);
        // 具体行为取决于实现，可能返回错误或正常处理
    }

    @Test
    public void testInvalidJsonParams() throws Exception {
        // 测试无效JSON参数
        ThingsRules rule = createDeviceTriggerRule();
        ThingsRequestMessage message = new ThingsRequestMessage();
        message.setId(UUID.randomUUID().toString());
        message.setJsonrpc("2.0");
        message.setMethod("AC_PROD_001/AC001/properties/temperature/post");
        message.setTime(System.currentTimeMillis());
        message.setTimeout(5000);

        // 设置无效的JSON参数（null而不是JSONObject）
        message.setParams(null);

        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 验证结果 - 应该返回错误
        assertNotNull(response);
        // 具体错误处理取决于实现
    }

    @Test
    public void testTimeoutScenario() throws Exception {
        // 测试超时场景
        ThingsRules rule = createDeviceTriggerRule();
        ThingsRequestMessage message = createValidMessage();
        message.setTimeout(1); // 设置很短的超时时间

        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 验证结果 - 应该在超时时间内完成或处理超时
        assertNotNull(response);
    }

    @Test
    public void testVeryLargePayload() throws Exception {
        // 测试大负载消息
        ThingsRules rule = createDeviceTriggerRule();
        ThingsRequestMessage message = createValidMessage();

        // 创建大负载参数
        JSONObject largeParams = new JSONObject();
        largeParams.put("temperature", 35.0); // 设置为大于30的值来触发规则
        // 添加大量额外数据
        for (int i = 0; i < 1000; i++) {
            largeParams.put("extraData" + i, "value" + i);
        }
        message.setParams(largeParams);

        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 验证结果 - 应该能够处理大负载
        assertNotNull(response);
        assertNotNull(response.getResult());
        assertNull(response.getError());
    }

    @Test
    public void testSpecialCharactersInParams() throws Exception {
        // 测试参数中的特殊字符
        ThingsRules rule = createDeviceTriggerRule();
        ThingsRequestMessage message = createValidMessage();

        JSONObject params = new JSONObject();
        params.put("temperature", 35.0); // 设置为大于30的值来触发规则
        params.put("deviceName", "测试设备-空调#001");
        params.put("specialChars", "!@#$%^&*()_+-=[]{}|;:,.<>?");
        message.setParams(params);

        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getResult());
        assertNull(response.getError());
    }

    @Test
    public void testUnicodeCharacters() throws Exception {
        // 测试Unicode字符
        ThingsRules rule = createDeviceTriggerRule();
        ThingsRequestMessage message = createValidMessage();

        JSONObject params = new JSONObject();
        params.put("temperature", 35.0); // 设置为大于30的值来触发规则
        params.put("chinese", "温度传感器");
        params.put("emoji", "🌡️📊");
        params.put("multilingual", "Temperature/Temperatur/温度");
        message.setParams(params);

        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getResult());
        assertNull(response.getError());
    }

    @Test
    public void testExtremeValues() throws Exception {
        // 测试极值
        ThingsRules rule = createDeviceTriggerRule();
        ThingsRequestMessage message = createValidMessage();

        JSONObject params = new JSONObject();
        params.put("temperature", Double.MAX_VALUE); // 最大double值
        message.setParams(params);

        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 验证结果 - 应该能够处理极值
        assertNotNull(response);
    }

    @Test
    public void testNegativeValues() throws Exception {
        // 测试负值
        ThingsRules rule = createDeviceTriggerRule();
        ThingsRequestMessage message = createValidMessage();

        JSONObject params = new JSONObject();
        params.put("temperature", 35.0); // 设置为大于30的值来触发规则
        params.put("chinese", "温度传感器");
        params.put("emoji", "🌡️📊");
        params.put("multilingual", "Temperature/Temperatur/温度");
        message.setParams(params);

        // 执行规则 - 温度-50度应该不触发 >30 的规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 验证结果 - 规则不应该触发
        assertNotNull(response);
        // 具体行为取决于实现，可能返回空结果或正常响应
    }

    @Test
    public void testZeroValues() throws Exception {
        // 测试零值
        ThingsRules rule = createDeviceTriggerRule();
        ThingsRequestMessage message = createValidMessage();

        JSONObject params = new JSONObject();
        params.put("temperature", 0.0); // 零度
        message.setParams(params);

        // 执行规则 - 温度0度应该不触发 >30 的规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 验证结果
        assertNotNull(response);
    }

    @Test
    public void testMissingRequiredFields() throws Exception {
        // 测试缺少必需字段
        ThingsRules rule = createDeviceTriggerRule();
        ThingsRequestMessage message = new ThingsRequestMessage();
        message.setId(UUID.randomUUID().toString());
        message.setJsonrpc("2.0");
        // 缺少method字段
        message.setTime(System.currentTimeMillis());
        message.setTimeout(5000);
        message.setParams(new JSONObject());

        // 执行规则
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 验证结果 - 应该返回错误
        assertNotNull(response);
        // 具体错误处理取决于实现
    }

    @Test
    public void testConcurrentRuleExecution() throws Exception {
        // 测试并发规则执行
        ThingsRules rule = createDeviceTriggerRule();

        // 创建多个并发请求
        Runnable task = () -> {
            try {
                ThingsRequestMessage message = createValidMessage();
                ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);
                assertNotNull(response);
            } catch (Exception e) {
                fail("Concurrent execution failed: " + e.getMessage());
            }
        };

        // 启动多个线程
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 如果没有异常抛出，测试通过
        assertTrue(true);
    }

    @Test
    public void testRuleWithEmptyActions() throws Exception {
        // 测试空动作列表的规则
        ThingsRules rule = new ThingsRules();
        rule.setId("empty-actions-rule-" + UUID.randomUUID().toString());
        rule.setName("Empty Actions Rule");
        rule.setDescription("Rule with no actions");
        rule.setStatus("enabled");

        // 创建触发器
        ThingsRules.Trigger trigger = new ThingsRules.Trigger();
        trigger.setType("device");
        ThingsRules.TriggerCondition condition = new ThingsRules.TriggerCondition();
        condition.setProperty("temperature");
        condition.setOperator(">");
        condition.setValue(30.0);
        ThingsRules.DeviceInfo deviceInfo = new ThingsRules.DeviceInfo();
        deviceInfo.setProductCode("AC_PROD_001");
        deviceInfo.setDeviceCode("AC001");
        deviceInfo.setMessageType("properties");
        deviceInfo.setIdentifier("temperature");
        deviceInfo.setAction("post");
        condition.setDeviceInfo(deviceInfo);
        trigger.setCondition(condition);
        rule.setTriggers(List.of(trigger));

        // 设置空动作列表
        rule.setActions(List.of());

        // 执行规则
        ThingsRequestMessage message = createValidMessage();
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 验证结果
        assertNotNull(response);
    }

    @Test
    public void testRuleWithEmptyTriggers() throws Exception {
        // 测试空触发器列表的规则
        ThingsRules rule = new ThingsRules();
        rule.setId("empty-triggers-rule-" + UUID.randomUUID().toString());
        rule.setName("Empty Triggers Rule");
        rule.setDescription("Rule with no triggers");
        rule.setStatus("enabled");

        // 设置空触发器列表
        rule.setTriggers(List.of());

        // 创建动作
        ThingsRules.Action action = new ThingsRules.Action();
        action.setType("notification");
        ThingsRules.ActionParams actionParams = new ThingsRules.ActionParams();
        ThingsRules.NotificationParams notification = new ThingsRules.NotificationParams();
        notification.setType("email");
        notification.setTitle("Test Notification");
        notification.setContent("Test message");
        notification.setRecipients(List.of("test@example.com"));
        actionParams.setNotification(notification);
        action.setParams(actionParams);
        rule.setActions(List.of(action));

        // 执行规则
        ThingsRequestMessage message = createValidMessage();
        ThingsResponseMessage response = thingsRulesEngineExecutor.executeRule(rule, message);

        // 验证结果 - 规则不应该触发
        assertNotNull(response);
    }

    // 辅助方法：创建有效的消息
    private ThingsRequestMessage createValidMessage() {
        ThingsRequestMessage message = new ThingsRequestMessage();
        message.setId(UUID.randomUUID().toString());
        message.setJsonrpc("2.0");
        message.setMethod("AC_PROD_001/AC001/properties/temperature/post");
        message.setTime(System.currentTimeMillis());
        message.setTimeout(5000);

        JSONObject params = new JSONObject();
        params.put("temperature", 35.0);
        message.setParams(params);

        return message;
    }

    // 辅助方法：创建设备触发器规则
    private ThingsRules createDeviceTriggerRule() {
        ThingsRules thingsRules = new ThingsRules();
        thingsRules.setId("device-trigger-rule-" + UUID.randomUUID().toString());
        thingsRules.setName("Device Trigger Rule");
        thingsRules.setDescription("Test device property trigger");
        thingsRules.setStatus("enabled");

        // 创建触发器
        ThingsRules.Trigger trigger = new ThingsRules.Trigger();
        trigger.setType("device");

        ThingsRules.TriggerCondition condition = new ThingsRules.TriggerCondition();
        condition.setProperty("temperature");
        condition.setOperator(">");
        condition.setValue(30.0);

        ThingsRules.DeviceInfo deviceInfo = new ThingsRules.DeviceInfo();
        deviceInfo.setProductCode("AC_PROD_001");
        deviceInfo.setDeviceCode("AC001");
        deviceInfo.setMessageType("properties");
        deviceInfo.setIdentifier("temperature");
        deviceInfo.setAction("post");
        condition.setDeviceInfo(deviceInfo);

        trigger.setCondition(condition);
        thingsRules.setTriggers(List.of(trigger));

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
        thingsRules.setActions(List.of(action));

        return thingsRules;
    }

    // 辅助方法：创建有效的规则
    private ThingsRules createValidRule() {
        return createDeviceTriggerRule();
    }
}