package cn.huangdayu.things.sofabiz;

import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public class ThingsSofaBizParentBeanFactory implements BeanFactory {

    private final ApplicationContext applicationContext;
    private final ThingsEngineProperties thingsEngineProperties;

    private Object getParentBean(String name, Class<?> requiredType, Object... args) {
        Object bean = null;
        if (StrUtil.isNotBlank(name)) {
            if (requiredType != null) {
                bean = applicationContext.getBean(name, requiredType);
            } else {
                bean = applicationContext.getBean(name, args);
            }
        } else if (requiredType != null) {
            bean = applicationContext.getBean(requiredType, args);
        }
        if (bean != null && sharedBean(bean.getClass())) {
            return bean;
        }
        throw new NoSuchBeanDefinitionException(name != null ? name : requiredType != null ? requiredType.getName() : "null");
    }

    private boolean sharedBean(Class<?> clazz) {
        for (String sharedBeanPackage : thingsEngineProperties.getSofaBiz().getSharedBeanPackages()) {
            if (clazz.getPackageName().startsWith(sharedBeanPackage)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Object getBean(String name) throws BeansException {
        return getParentBean(name, null);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return (T) getParentBean(name, requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return getParentBean(name, null, args);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return (T) getParentBean(null, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return (T) getParentBean(null, requiredType, args);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        if (sharedBean(requiredType)) {
            return applicationContext.getBeanProvider(requiredType);
        }
        return null;
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        if (sharedBean(requiredType.getRawClass())) {
            return applicationContext.getBeanProvider(requiredType);
        }
        return null;
    }

    @Override
    public boolean containsBean(String name) {
        if (applicationContext.containsBean(name)) {
            Object bean = applicationContext.getBean(name);
            return sharedBean(bean.getClass());
        }
        return false;
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return containsBean(name);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        if (applicationContext.isPrototype(name)) {
            return containsBean(name);
        }
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        if (applicationContext.isTypeMatch(name, typeToMatch)) {
            return sharedBean(typeToMatch.getRawClass());
        }
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        if (applicationContext.isTypeMatch(name, typeToMatch)) {
            return sharedBean(typeToMatch);
        }
        return false;
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        Class<?> type = applicationContext.getType(name);
        if (sharedBean(type)) {
            return type;
        }
        throw new NoSuchBeanDefinitionException(name);
    }

    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        Class<?> type = applicationContext.getType(name, allowFactoryBeanInit);
        if (sharedBean(type)) {
            return type;
        }
        throw new NoSuchBeanDefinitionException(name);
    }

    @Override
    public String[] getAliases(String name) {
        return applicationContext.getAliases(name);
    }
}
