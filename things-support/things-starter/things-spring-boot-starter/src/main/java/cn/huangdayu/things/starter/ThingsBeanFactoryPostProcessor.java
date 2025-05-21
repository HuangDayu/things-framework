package cn.huangdayu.things.starter;

import cn.huangdayu.things.common.annotation.ThingsBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 出现多个bean冲突异常时，通过primary字段来选择主要的bean
 * @author huangdayu
 */
@Component
public class ThingsBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanNamesForAnnotation(ThingsBean.class); // 直接获取 Bean 名称
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            // 获取原始类（绕过代理类）
            Class<?> targetClass = getTargetClass(beanFactory, beanName);
            if (targetClass == null) continue;

            // 从原始类获取注解
            ThingsBean thingsBean = AnnotationUtils.findAnnotation(targetClass, ThingsBean.class);
            if (thingsBean != null) {
                beanDefinition.setPrimary(thingsBean.primary());
            }
        }
    }

    /**
     * 解析原始类（绕过代理）
     */
    private Class<?> getTargetClass(ConfigurableListableBeanFactory beanFactory, String beanName) {
        if (beanFactory.containsBeanDefinition(beanName)) {
            return beanFactory.getBeanDefinition(beanName).getResolvableType().toClass();
        }
        return null;
    }
}