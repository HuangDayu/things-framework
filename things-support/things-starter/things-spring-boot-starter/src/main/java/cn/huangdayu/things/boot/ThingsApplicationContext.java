package cn.huangdayu.things.boot;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.engine.core.ThingsContainer;
import cn.huangdayu.things.engine.core.ThingsManager;
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
@ThingsBean
public class ThingsApplicationContext implements ApplicationContextAware {

    @Getter
    private static ApplicationContext context;
    private static String contextName;
    private final ThingsManager thingsManager;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ThingsApplicationContext.context = applicationContext;
        thingsManager.register(new SpringThingsContainer(applicationContext));
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
