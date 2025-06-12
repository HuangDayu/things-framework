package cn.huangdayu.things.sofaark.singleton;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.util.Map;

import static cn.huangdayu.things.common.utils.ThingsUtils.getUUID;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public class ThingsSofaArkContainer implements ThingsContainer {

    private static String contextName;
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
