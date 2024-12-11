package cn.huangdayu.things.common.retry;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * RetryQueue类管理消息队列，并使用ScheduledExecutorService来定期检查并重试队列中的消息。
 * @author huangdayu
 */
public class RetryQueue {
    private final Queue<RetryMessage> queue = new LinkedList<>();
    private final ScheduledExecutorService scheduler;
    private final int retryInterval; // 重试间隔时间，单位毫秒

    public RetryQueue(int retryInterval) {
        this.retryInterval = retryInterval;
        this.scheduler = Executors.newScheduledThreadPool(1);
        startRetrying();
    }

    /**
     * enqueue方法用于添加消息，processMessages方法用于处理消息。
     * @param message
     */
    public void enqueue(RetryMessage message) {
        synchronized (queue) {
            queue.add(message);
        }
    }

    private void processMessages() {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                RetryMessage message = queue.peek();
                if (message.canRetry()) {
                    try {
                        // 处理消息的逻辑
                        processMessage(message.getContent());
                        queue.poll(); // 成功处理后移除消息
                    } catch (Exception e) {
                        message.incrementRetryCount();
                        System.out.println("Failed to process message: " + message.getContent() + ", retry count: " + message.getRetryCount());
                    }
                } else {
                    System.out.println("Max retries reached for message: " + message.getContent());
                    queue.poll(); // 移除无法重试的消息
                }
            }
        }
    }

    private void processMessage(String content) throws Exception {
        // 模拟消息处理逻辑
        System.out.println("Processing message: " + content);
        // 如果需要模拟失败，可以抛出异常
        // throw new Exception("Simulated failure");
    }

    /**
     * startRetrying方法使用scheduler.scheduleAtFixedRate来定期执行processMessages方法。
     */
    private void startRetrying() {
        scheduler.scheduleAtFixedRate(this::processMessages, 0, retryInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * shutdown方法用于关闭调度器，释放资源。
     */
    public void shutdown() {
        scheduler.shutdown();
    }
}
