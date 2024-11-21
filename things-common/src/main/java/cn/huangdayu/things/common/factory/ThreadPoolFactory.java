package cn.huangdayu.things.common.factory;

import cn.hutool.core.thread.ExecutorBuilder;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author huangdayu
 */
public class ThreadPoolFactory {

    public static final ExecutorService THINGS_EXECUTOR = ThreadPoolFactory.newExecutor(3);

    public static ThreadPoolExecutor newExecutor(int corePoolSize) {
        ExecutorBuilder builder = ExecutorBuilder.create();
        if (corePoolSize > 0) {
            builder.setCorePoolSize(corePoolSize);
        }
        builder.setThreadFactory(tryGetVirtualThreadFactory());
        return builder.build();
    }

    /**
     * 尝试用反射获取虚拟线程工程，如果失败则用默认线程工厂
     *
     * @return
     */
    public static ThreadFactory tryGetVirtualThreadFactory() {
        try {
            Method ofVirtualMethod = Thread.class.getMethod("ofVirtual");
            Object threadBuilderOfVirtual = ofVirtualMethod.invoke(null);
            Method factoryMethod = threadBuilderOfVirtual.getClass().getMethod("factory");
            return (ThreadFactory) factoryMethod.invoke(threadBuilderOfVirtual);
        } catch (Exception e) {
            return Executors.defaultThreadFactory();
        }
    }

}
