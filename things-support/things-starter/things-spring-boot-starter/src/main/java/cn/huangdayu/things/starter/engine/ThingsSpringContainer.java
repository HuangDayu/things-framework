package cn.huangdayu.things.starter.engine;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.api.container.ThingsRegister;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.Map;

import static cn.huangdayu.things.common.utils.ThingsUtils.getUUID;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public class ThingsSpringContainer implements ApplicationContextAware, ThingsContainer {

    @Getter
    private static ApplicationContext context;
    private static String contextName;
    private final ThingsRegister thingsRegister;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ThingsSpringContainer.context = applicationContext;
        thingsRegister.register(this);
    }

    @PreDestroy
    public void destroy() {
        thingsRegister.unregister(this);
    }


    @Override
    public String name() {
        if (StrUtil.isNotBlank(contextName)) {
            return contextName;
        }
        contextName = StrUtil.isNotBlank(context.getApplicationName()) ? context.getApplicationName() : getUUID();
        return contextName;
    }

    @Override
    public Map<String, Object> getBeans(Class<? extends Annotation> annotationType) {
        return context.getBeansWithAnnotation(annotationType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

}
