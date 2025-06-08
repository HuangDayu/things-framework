package cn.huangdayu.things.sofaark;

import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.api.ClientResponse;
import com.alipay.sofa.ark.api.ResponseCode;
import com.alipay.sofa.ark.spi.model.BizInfo;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangdayu
 */
@Slf4j
public class ThingsSofaArkJarListener implements ThingsSofaArkJarMonitor.JarChangeListener {

    private static final Map<ThingsSofaArkJarMonitor.JarFileInfo, BizInfo> BIZ_INFO_MAP = new ConcurrentHashMap<>();

    @Override
    public void onChange(List<ThingsSofaArkJarMonitor.JarFileInfo> changedFiles, List<Path> existingCopies) {
        changedFiles.forEach(jarFileInfo -> {
            ThingsSofaArkJarMonitor.ChangeType changeType = jarFileInfo.getChangeType();
            switch (changeType) {
                case ADDED:
                    installBiz(jarFileInfo);
                    break;
                case REMOVED:
                    uninstallBiz(jarFileInfo);
                    break;
                case UPDATED:
                    uninstallBiz(jarFileInfo);
                    installBiz(jarFileInfo);
                    break;
                default:
                    break;
            }
        });
    }

    private void installBiz(ThingsSofaArkJarMonitor.JarFileInfo jarFileInfo) {
        try {
            ClientResponse clientResponse = ArkClient.installBiz(jarFileInfo.getCopyPath().toFile());
            if (clientResponse.getCode() == ResponseCode.SUCCESS) {
                BizInfo bizInfo = clientResponse.getBizInfos().iterator().next();
                BIZ_INFO_MAP.put(jarFileInfo, bizInfo);
                log.info("Things SofaArk install biz success , name: {} , version: {}", bizInfo.getBizName(), bizInfo.getBizVersion());
            } else {
                log.error("Things SofaArk install biz [{}] error: {}", jarFileInfo.getOriginalName(), clientResponse.getMessage());
            }
        } catch (Throwable e) {
            log.error("Things SofaArk install biz [{}] error", jarFileInfo.getOriginalName(), e);
        }
    }


    private void uninstallBiz(ThingsSofaArkJarMonitor.JarFileInfo jarFileInfo) {
        try {
            BizInfo bizInfo = BIZ_INFO_MAP.get(jarFileInfo);
            if (bizInfo != null) {
                ClientResponse clientResponse = ArkClient.uninstallBiz(bizInfo.getBizName(), bizInfo.getBizVersion());
                if (clientResponse.getCode() == ResponseCode.SUCCESS) {
                    BIZ_INFO_MAP.remove(jarFileInfo);
                    log.info("Things SofaArk uninstall biz success , name: {} , version: {}", bizInfo.getBizName(), bizInfo.getBizVersion());
                } else {
                    log.error("Things SofaArk uninstall biz [{}] error: {}", jarFileInfo.getOriginalName(), clientResponse.getMessage());
                }
            }
        } catch (Throwable e) {
            log.error("Things SofaArk uninstall biz [{}] error", jarFileInfo.getOriginalName(), e);
        }
    }
}
