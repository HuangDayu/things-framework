package cn.huangdayu.things.camel.test;


import cn.huangdayu.things.camel.mqtt.ThingsSofaBusTopicValidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author huangdayu
 */
public class TopicValidatorTest {


    public static void main(String[] args) {
        test();
    }

    /**
     * 单元测试验证
     */
    public static void test() {
        ThingsSofaBusTopicValidator validator = new ThingsSofaBusTopicValidator();

        // 添加订阅
        validator.addTopic("sensor/+/temperature");
        validator.addTopic("device/#");
        validator.addTopic("control/+/fan/+");

        // 测试用例
        System.out.println("Test 1 (sensor/123/temperature): " +
                validator.isDuplicateSubscription("sensor/123/temperature")); // true ✅
        assertTrue(validator.isDuplicateSubscription("sensor/123/temperature")); // 应返回true


        System.out.println("Test 2 (device/123/status): " +
                validator.isDuplicateSubscription("device/123/status"));      // true ✅
        assertTrue(validator.isDuplicateSubscription("device/123/status")); // 应返回true


        System.out.println("Test 3 (control/office/fan): " +
                validator.isDuplicateSubscription("control/office/fan"));     // false ✅
        assertFalse(validator.isDuplicateSubscription("control/office/fan")); // 应返回false


        // 边界测试
        validator.addTopic("edge/#");
        System.out.println("Test 4 (edge/): " +
                validator.isDuplicateSubscription("edge/"));                 // true ✅
        assertTrue(validator.isDuplicateSubscription("edge/")); // 应返回true


        validator.addTopic("test/+/final");
        System.out.println("Test 5 (test/abc/final): " +
                validator.isDuplicateSubscription("test/abc/final"));        // true ✅
        assertTrue(validator.isDuplicateSubscription("test/abc/final")); // 应返回true


        // 测试多次订阅/取消
        validator.addTopic("a/b/c");
        validator.addTopic("a/b/c");
        validator.removeTopic("a/b/c");
        assertTrue(validator.isDuplicateSubscription("a/b/c")); // 应返回true
        validator.removeTopic("a/b/c");
        assertFalse(validator.isDuplicateSubscription("a/b/c")); // 应返回false

        // 添加并移除通配符
        validator.addTopic("device/#");
        validator.removeTopic("device/#");

        // 应返回false
        assertFalse(validator.isDuplicateSubscription("device/123"));

        // 验证缓存重建
        validator.addTopic("sensor/+/temp");
        assertTrue(validator.isDuplicateSubscription("sensor/1/temp"));
    }
}
