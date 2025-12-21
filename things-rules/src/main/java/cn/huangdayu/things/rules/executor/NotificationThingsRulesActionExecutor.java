package cn.huangdayu.things.rules.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesActionExecutor;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 通知动作执行器
 * 负责执行通知类型的动作
 * 处理向用户发送通知消息的逻辑
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class NotificationThingsRulesActionExecutor implements ThingsRulesActionExecutor {

    /**
     * 执行通知动作
     * 根据动作参数向用户发送通知消息
     *
     * @param params 动作参数
     * @return 执行结果
     */
    @Override
    public JSONObject execute(ThingsRules.ActionParams params) {
        // 检查参数是否有效
        if (isInvalidParams(params)) {
            return null;
        }

        // 获取通知参数
        ThingsRules.NotificationParams notification = params.getNotification();

        // 执行通知发送
        return executeNotification(notification);
    }

    /**
     * 获取动作类型
     *
     * @return 动作类型字符串
     */
    @Override
    public String getType() {
        return "notification";
    }

    /**
     * 检查参数是否无效
     *
     * @param params 动作参数
     * @return 如果参数无效返回true，否则返回false
     */
    private boolean isInvalidParams(ThingsRules.ActionParams params) {
        return params == null || params.getNotification() == null;
    }

    /**
     * 执行通知发送
     * 实际执行通知发送逻辑
     *
     * @param notification 通知参数
     * @return 执行结果
     */
    private JSONObject executeNotification(ThingsRules.NotificationParams notification) {
        try {
            log.info("Sending notification: {}", notification);

            // 在实际项目中，这里会发送通知消息到用户
            // 模拟通知发送结果
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("message", "Notification sent successfully");
            result.put("type", notification.getType());
            result.put("title", notification.getTitle());
            result.put("content", notification.getContent());
            result.put("recipients", notification.getRecipients());

            // 模拟通知发送耗时
            Thread.sleep(50);

            log.info("Notification sent successfully: {}", notification);
            return result;
        } catch (Exception e) {
            log.error("Error sending notification: {}", notification, e);
            JSONObject errorResult = new JSONObject();
            errorResult.put("success", false);
            errorResult.put("message", "Error sending notification: " + e.getMessage());
            errorResult.put("notification", notification);
            return errorResult;
        }
    }
}