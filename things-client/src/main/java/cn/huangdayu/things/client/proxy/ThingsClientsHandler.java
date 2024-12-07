package cn.huangdayu.things.client.proxy;

import cn.huangdayu.things.common.annotation.ThingsClient;
import cn.huangdayu.things.common.annotation.ThingsService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import cn.huangdayu.things.client.ThingsClientContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author huangdayu
 */
@Slf4j
@Data
public class ThingsClientsHandler implements InvocationHandler {

    private final Class<?> interfaceClass;
    private final ThingsClient thingsClient;
    private static ThingsClientsProxyInvoke thingsClientsProxyInvoke;

    public ThingsClientsHandler(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        this.thingsClient = interfaceClass.getAnnotation(ThingsClient.class);
    }

    public static ThingsClientsProxyInvoke getThingsClientsProxyInvoke() {
        if (thingsClientsProxyInvoke == null) {
            thingsClientsProxyInvoke = ThingsClientContext.getContext().getBean(ThingsClientsProxyInvoke.class);
        }
        return thingsClientsProxyInvoke;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ThingsService thingsService = method.getAnnotation(ThingsService.class);
        if (thingsService != null) {
            return getThingsClientsProxyInvoke().invokeService(thingsClient, thingsService, method, args);
        }
        throw new UnsupportedOperationException("Things client invoke unsupported operation");
    }


}
