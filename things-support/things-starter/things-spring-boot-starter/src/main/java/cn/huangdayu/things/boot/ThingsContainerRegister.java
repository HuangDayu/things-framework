package cn.huangdayu.things.boot;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.api.register.ThingsRegister;
import cn.hutool.core.util.StrUtil;
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
public class ThingsContainerRegister implements ApplicationContextAware {

    @Getter
    private static ApplicationContext context;
    private static String contextName;
    private final ThingsRegister thingsRegister;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ThingsContainerRegister.context = applicationContext;
        thingsRegister.register(new SpringThingsContainer(applicationContext));
    }


    @RequiredArgsConstructor
    public static class SpringThingsContainer extends ThingsContainer {

        private final ApplicationContext context;

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

}
