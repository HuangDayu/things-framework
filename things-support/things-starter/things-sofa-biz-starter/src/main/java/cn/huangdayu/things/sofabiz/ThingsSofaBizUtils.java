package cn.huangdayu.things.sofabiz;

import com.alipay.sofa.koupleless.common.BizRuntimeContextRegistry;
import com.alipay.sofa.koupleless.common.api.SpringServiceFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

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

}
