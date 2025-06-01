package cn.huangdayu.things.starter.client;

import cn.huangdayu.things.client.proxy.ThingsClientsHandler;
import cn.huangdayu.things.client.proxy.ThingsClientsProxy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;
import java.util.function.Supplier;

/**
 * @author huangdayu
 */
@Slf4j
@Data
public class ThingsClientsFactoryBean<T> implements FactoryBean<T> {
    private Class<T> interfaceClass;
    private Supplier<ThingsClientsProxy> thingsClientsProxySupplier;

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ThingsClientsHandler(interfaceClass, thingsClientsProxySupplier));
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
