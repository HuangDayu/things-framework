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
        Map<String, Object> beansWithAnnotation = beanFactory.getBeansWithAnnotation(ThingsBean.class);
        beansWithAnnotation.entrySet().parallelStream().filter(entry -> {
            Object bean = entry.getValue();
            ThingsBean thingsBean = AnnotationUtils.findAnnotation(bean.getClass(), ThingsBean.class);
            return thingsBean != null && thingsBean.primary();
        }).forEach(entry -> {
            BeanDefinition primaryBeanDefinition = beanFactory.getBeanDefinition(entry.getKey());
            primaryBeanDefinition.setPrimary(true);
        });
    }
}