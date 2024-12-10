package cn.huangdayu.things.client.proxy;

import cn.huangdayu.things.common.annotation.ThingsClient;
import cn.huangdayu.things.common.annotation.ThingsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * @author huangdayu
 */
@Slf4j
@Data
public class ThingsClientsHandler implements InvocationHandler {

    private final Class<?> interfaceClass;
    private final ThingsClient thingsClient;
    private final Supplier<ThingsClientsProxy> thingsClientsProxySupplier;
    private static ThingsClientsProxy thingsClientsProxy;

    private ThingsClientsProxy getThingsClientsProxy() {
        if (thingsClientsProxy == null) {
            synchronized (ThingsClientsHandler.class) {
                thingsClientsProxy = thingsClientsProxySupplier.get();
            }
        }
        return thingsClientsProxy;
    }

    public ThingsClientsHandler(Class<?> interfaceClass, Supplier<ThingsClientsProxy> thingsClientsProxySupplier) {
        this.interfaceClass = interfaceClass;
        this.thingsClient = interfaceClass.getAnnotation(ThingsClient.class);
        this.thingsClientsProxySupplier = thingsClientsProxySupplier;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ThingsService thingsService = method.getAnnotation(ThingsService.class);
        if (thingsService != null) {
            return getThingsClientsProxy().invokeService(thingsClient, thingsService, method, args);
        }
        throw new UnsupportedOperationException("Things client invoke unsupported operation");
    }


}
