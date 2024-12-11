package cn.huangdayu.things.common.retry;

import lombok.Data;

/**
 * RetryMessage类封装了消息的内容和重试次数。
 * @author huangdayu
 */
@Data
public class RetryMessage {
    private final String content;
    private int retryCount;
    private final int maxRetries;

    public RetryMessage(String content, int maxRetries) {
        this.content = content;
        this.retryCount = 0;
        this.maxRetries = maxRetries;
    }

    public boolean canRetry() {
        return retryCount < maxRetries;
    }

    public void incrementRetryCount() {
        retryCount++;
    }
}
