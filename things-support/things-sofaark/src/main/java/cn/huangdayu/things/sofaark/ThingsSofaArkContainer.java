package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.api.container.ThingsContainer;
import com.alipay.sofa.ark.spi.model.Biz;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public class ThingsSofaArkContainer implements ThingsContainer {

    public static final Map<Biz, ThingsSofaArkContainer> ARK_CONTAINER_MAP = new ConcurrentHashMap<>();

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
