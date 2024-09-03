package cn.huangdayu.things.engine.async;

import ch.qos.logback.core.util.EnvUtil;
import cn.hutool.core.thread.ExecutorBuilder;

import java.lang.reflect.InvocationTargetException;
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
        builder.setThreadFactory(makeThreadFactory());
        return builder.build();
    }

    /**
     * A thread factory which may be a virtual thread factory the JDK supports it.
     *
     * @return
     * @author ch.qos.logback.core.util.ExecutorServiceUtil.makeThreadFactory()
     */
    public static ThreadFactory makeThreadFactory() {
        if (EnvUtil.isJDK21OrHigher()) {
            try {
                Method ofVirtualMethod = Thread.class.getMethod("ofVirtual");
                Object threadBuilderOfVirtual = ofVirtualMethod.invoke(null);
                Method factoryMethod = threadBuilderOfVirtual.getClass().getMethod("factory");
                return (ThreadFactory) factoryMethod.invoke(threadBuilderOfVirtual);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                return Executors.defaultThreadFactory();
            }
        } else {
            return Executors.defaultThreadFactory();
        }
    }

}
