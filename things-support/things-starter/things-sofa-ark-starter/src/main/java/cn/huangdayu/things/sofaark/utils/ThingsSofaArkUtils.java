package cn.huangdayu.things.sofaark.utils;

import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.container.model.BizModel;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.koupleless.common.BizRuntimeContextRegistry;
import com.alipay.sofa.koupleless.common.api.SpringServiceFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * @author huangdayu
 */
@Slf4j
public class ThingsSofaArkUtils {

    public static <T> T getBizService(BizModel bizModel, Class<T> serviceType) {
        return getBizService(bizModel.getBizName(), bizModel.getBizVersion(), serviceType);
    }

    public static <T> T getBizService(String bizName, String bizVersion, Class<T> serviceType) {
        try {
            return SpringServiceFinder.getModuleService(bizName, bizVersion, serviceType);
        } catch (Exception e) {
            log.warn("Things SofaArk get biz service [{}:{}] - [{}] error: {}", bizName, bizVersion, serviceType.getName(), e.getMessage());
        }
        return getBizContext(bizName, bizVersion).getBean(serviceType);
    }

    public static ApplicationContext getBizContext(String bizName, String bizVersion) {
        return getBizContext(ArkClient.getBizManagerService().getBiz(bizName, bizVersion));
    }

    public static ApplicationContext getBizContext(BizModel bizModel) {
        return (ApplicationContext) BizRuntimeContextRegistry.getBizRuntimeContext(bizModel).getApplicationContext().get();
    }

    public static ApplicationContext getBizContext(Biz biz) {
        return (ApplicationContext) BizRuntimeContextRegistry.getBizRuntimeContextByClassLoader(biz.getBizClassLoader()).getApplicationContext().get();
    }

    public static <T> T getArkService(Class<T> serviceType) {
        try {
            return SpringServiceFinder.getBaseService(serviceType);
        } catch (Exception e) {
            log.warn("Things SofaArk get module ark service [{}] error: {}", serviceType.getName(), e.getMessage());
        }
        return getArkContext().getBean(serviceType);
    }

    public static <T> T getArkBean(Class<T> beanType) {
        return getArkContext().getBean(beanType);
    }

    public static ApplicationContext getArkContext() {
        return (ApplicationContext) BizRuntimeContextRegistry.getMasterBizRuntimeContext().getApplicationContext().get();
    }

}
