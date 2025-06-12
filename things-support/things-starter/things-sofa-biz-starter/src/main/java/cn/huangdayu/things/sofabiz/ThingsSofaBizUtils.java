package cn.huangdayu.things.sofabiz;

import com.alipay.sofa.koupleless.common.BizRuntimeContextRegistry;
import com.alipay.sofa.koupleless.common.api.SpringServiceFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Optional;

/**
 * @author huangdayu
 */
@Slf4j
public class ThingsSofaBizUtils {


    public static <T> T getArkService(Class<T> serviceType) {
        try {
            return SpringServiceFinder.getBaseService(serviceType);
        } catch (Exception e) {
            log.warn("Things SofaArk get module ark service [{}] error: {}", serviceType.getName(), e.getMessage());
        }
        ApplicationContext applicationContext = (ApplicationContext) BizRuntimeContextRegistry.getMasterBizRuntimeContext().getApplicationContext().get();
        return applicationContext.getBean(serviceType);
    }

    public static ClassLoader getCurrentBizClassloader(ApplicationContext applicationContext) {
        Optional<Map.Entry<String, Object>> first = applicationContext.getBeansWithAnnotation(SpringBootApplication.class).entrySet().stream().findFirst();
        if (first.isPresent()) {
            return first.get().getValue().getClass().getClassLoader();
        }
        return applicationContext.getClassLoader();
    }

}
