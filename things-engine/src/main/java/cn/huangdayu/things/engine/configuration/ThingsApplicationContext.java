package cn.huangdayu.things.engine.configuration;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.core.ThingsManageEngine;
import cn.huangdayu.things.engine.wrapper.ThingsContainer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsApplicationContext implements ApplicationContextAware {

    @Getter
    private static ApplicationContext context;
    private final ThingsManageEngine thingsManageEngine;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ThingsApplicationContext.context = applicationContext;
        thingsManageEngine.register(new SpringThingsContainer(applicationContext));
    }


    @RequiredArgsConstructor
    public static class SpringThingsContainer extends ThingsContainer {

        private final ApplicationContext context;

        @Override
        public String name() {
            return context.getApplicationName();
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

}
