package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.infrastructure.ThingsConfigurator;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.events.ThingsPropertiesUpdatedEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.properties.ThingsSystemProperties;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class ThingsConfiguratorExecutor implements ThingsConfigurator {

    private volatile ThingsSystemProperties thingsSystemProperties;
    private final ThingsEventObserver thingsEventObserver;
    private static final DateTimeFormatter BACKUP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public ThingsConfiguratorExecutor(ThingsSystemProperties thingsSystemProperties, ThingsEventObserver thingsEventObserver) {
        this.thingsSystemProperties = thingsSystemProperties;
        this.thingsEventObserver = thingsEventObserver;
    }

    @Override
    public ThingsSystemProperties getProperties() {
        return thingsSystemProperties != null ? thingsSystemProperties : loadObjectFromJson(new File(thingsSystemProperties.getConfigPath()), ThingsSystemProperties.class);
    }

    @Override
    public void updateProperties(ThingsSystemProperties properties) {
        thingsEventObserver.notifyObservers(new ThingsPropertiesUpdatedEvent(this, this.thingsSystemProperties, properties));
        this.thingsSystemProperties = properties;
        saveObjectWithBackup(new File(thingsSystemProperties.getConfigPath()), properties, null);
    }

    /**
     * 持久化对象到JSON文件并备份旧文件
     *
     * @param targetFile 目标文件路径
     * @param object     要持久化的对象
     * @param backupDir  备份目录路径（若为null则使用目标文件所在目录）
     */
    public static void saveObjectWithBackup(File targetFile, Object object, File backupDir) {
        try {
            // 确保目录存在
            File parentDir = targetFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            // 备份已有文件
            if (targetFile.exists()) {
                File backupDirectory = (backupDir != null) ? backupDir : parentDir;
                String timestamp = LocalDateTime.now().format(BACKUP_FORMAT);
                String backupFileName = targetFile.getName().replace(".json",
                        "_" + timestamp + ".json");

                File backupFile = new File(backupDirectory, backupFileName);
                Files.copy(targetFile.toPath(), backupFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            // 序列化并写入文件（美化输出）
            String jsonString = JSON.toJSONString(object, JSONWriter.Feature.PrettyFormat);
            Files.write(targetFile.toPath(), jsonString.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            log.error("Things save properties object to local error", e);
        }
    }

    /**
     * 从JSON文件恢复对象
     *
     * @param sourceFile JSON文件路径
     * @param valueType  目标对象类型
     * @return 反序列化后的对象
     */
    public static <T> T loadObjectFromJson(File sourceFile, Class<T> valueType) {
        try {
            if (!sourceFile.exists()) {
                return null;
            }
            byte[] bytes = Files.readAllBytes(sourceFile.toPath());
            return JSON.parseObject(bytes, valueType);
        } catch (Exception e) {
            log.error("Things load local properties to object error", e);
        }
        return null;
    }
}
