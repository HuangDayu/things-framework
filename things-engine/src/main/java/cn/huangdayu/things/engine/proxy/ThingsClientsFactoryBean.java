package cn.huangdayu.things.engine.proxy;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author huangdayu
 */
@Slf4j
@Data
public class ThingsClientsFactoryBean<T> implements FactoryBean<T> {
    private Class<T> interfaceClass;

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ThingsClientsHandler(interfaceClass));
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
