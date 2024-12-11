package cn.huangdayu.things.common.retry;

public class Main {
    public static void main(String[] args) {
        RetryQueue retryQueue = new RetryQueue(5000); // 设置重试间隔为5秒
        retryQueue.enqueue(new RetryMessage("Message 1", 3));
        retryQueue.enqueue(new RetryMessage("Message 2", 3));

        // 模拟程序运行一段时间后关闭
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        retryQueue.shutdown();
    }
}
