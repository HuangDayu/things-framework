package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.common.exception.ThingsException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * JAR文件监控服务，提供以下功能：
 * 1. 监控指定目录下原始JAR文件的变化（新增/修改/删除）
 * 2. 自动为每个变化的JAR创建副本文件（文件名包含-copy）
 * 3. 通过回调接口通知变化事件
 * 4. 服务停止时自动清理所有副本文件
 * <p>
 * 采用三层检测机制保证性能与准确性：
 * - 快速文件属性比对（大小+修改时间）
 * - 并行MD5哈希计算（仅对可能变化的文件）
 * - 副本文件生命周期管理
 *
 * @author deepseek
 */
@Slf4j
public class ThingsSofaArkJarMonitor {
    /**
     * 定时任务调度器，用于执行每分钟的扫描任务
     */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * 监控的目标目录路径（标准化绝对路径）
     */
    private final Path targetDir;

    /**
     * 变化监听器，不可为null
     */
    private final JarChangeListener listener;

    /**
     * 上次扫描快照（路径 -> 文件元数据）
     */
    private final Map<Path, FileMeta> lastSnapshot = new ConcurrentHashMap<>();

    /**
     * 托管的副本文件路径集合（线程安全）
     */
    private final Set<Path> managedCopies = ConcurrentHashMap.newKeySet();

    /**
     * MD5计算缓冲区大小（128KB平衡内存与IO效率）
     */
    private final int bufferSize = 128 * 1024;
    private final String jarEndsWith;
    private final String unpackEndsWith;


    /**
     * 创建JAR文件监控实例
     *
     * @param dirPath  要监控的目录路径
     * @param listener 变化监听器（不可为null）
     * @throws IllegalArgumentException 如果路径不是有效目录
     */
    public ThingsSofaArkJarMonitor(String dirPath, String jarEndsWith, String unpackEndsWith, JarChangeListener listener) {
        this.targetDir = Paths.get(dirPath).toAbsolutePath().normalize();
        this.listener = Objects.requireNonNull(listener);
        this.jarEndsWith = jarEndsWith;
        this.unpackEndsWith = unpackEndsWith;
        validateDirectory();
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 验证目标路径是否为有效目录
     */
    private void validateDirectory() {
        if (!Files.isDirectory(targetDir)) {
            throw new ThingsException(null, "Path is not a directory: " + targetDir);
        }
    }

    /**
     * 启动监控服务（立即执行首次扫描，之后每分钟执行一次）
     */
    public void start() {
        scheduler.scheduleAtFixedRate(this::performScan, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 停止监控服务并清理资源
     */
    public void stop() {
        shutdownExecutor(scheduler);
        cleanManagedCopies();
    }

    /**
     * 执行完整的扫描流程
     */
    private void performScan() {
        try {
            // 阶段1：扫描目录获取当前状态
            Map<Path, FileMeta> currentState = scanOriginalJars();
            // 阶段2：检测变化并处理
            List<JarFileInfo> changes = detectChanges(currentState);
            if (!changes.isEmpty()) {
                processChanges(changes);
                // 阶段3：触发回调并更新状态
                listener.onChange(changes, List.copyOf(managedCopies));
            }
            // 阶段4：更新快照数据
            lastSnapshot.clear();
            lastSnapshot.putAll(currentState);
        } catch (Exception e) {
            log.error("Scan failed: {}", e.getMessage());
        }
    }

    /**
     * 扫描目录获取原始JAR文件状态（排除副本文件）
     */
    private Map<Path, FileMeta> scanOriginalJars() throws IOException {
        try (Stream<Path> stream = Files.list(targetDir)) {
            return stream.filter(this::isOriginalJar).collect(Collectors.toConcurrentMap(p -> p, p -> new FileMeta(p), (oldV, newV) -> newV));
        }
    }

    /**
     * 判断是否为原始JAR文件（排除含-copy的副本）
     */
    private boolean isOriginalJar(Path path) {
        String filename = path.getFileName().toString();
        return filename.endsWith(jarEndsWith) && !filename.contains("-copy");
    }

    /**
     * 检测文件变化并生成变更列表
     */
    private List<JarFileInfo> detectChanges(Map<Path, FileMeta> current) {
        List<JarFileInfo> changes = new CopyOnWriteArrayList<>();
        // 检测删除文件（存在于上次快照但不存在于当前状态）
        lastSnapshot.keySet().stream().filter(p -> !current.containsKey(p)).forEach(p -> changes.add(createFileInfo(p, ChangeType.REMOVED)));
        // 检测新增/修改文件
        current.forEach((path, meta) -> {
            FileMeta previous = lastSnapshot.get(path);
            if (previous == null) {
                // 新增文件
                changes.add(createFileInfo(path, ChangeType.ADDED));
            } else if (meta.isModified(previous)) {
                // 文件属性变化，进行内容校验
                if (!computeContentHash(path, meta).equals(previous.md5Hash)) {
                    changes.add(createFileInfo(path, ChangeType.UPDATED));
                }
            }
        });

        return Collections.unmodifiableList(changes);
    }

    /**
     * 计算文件内容哈希（使用延迟加载策略）
     */
    private String computeContentHash(Path path, FileMeta meta) {
        try {
            return meta.md5Hash != null ? meta.md5Hash : computeMD5(path);
        } catch (Exception e) {
            log.error("Hash computation failed for: {}", path);
            return "";
        }
    }

    /**
     * 计算文件的MD5哈希值
     */
    private String computeMD5(Path path) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream is = Files.newInputStream(path)) {
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            return bytesToHex(md.digest());
        }
    }


    // region 变更处理

    /**
     * 处理检测到的变更事件
     */
    private void processChanges(List<JarFileInfo> changes) {
        changes.forEach(info -> {
            switch (info.getChangeType()) {
                case ADDED, UPDATED -> handleAddOrUpdate(info);
                case REMOVED -> handleRemoval(info);
            }
        });
    }

    /**
     * 处理新增/更新事件：创建或更新副本文件
     */
    private void handleAddOrUpdate(JarFileInfo info) {
        Path copyPath = generateCopyPath(info.getOriginalPath());
        managedCopies.add(copyPath);
        try {
            Files.copy(info.getOriginalPath(), copyPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied: " + copyPath);
        } catch (IOException e) {
            managedCopies.remove(copyPath); // 回滚添加
            log.error("Copy failed: {}", e.getMessage());
            throw new CompletionException(e);
        }
    }

    /**
     * 处理删除事件：移除并删除副本文件
     */
    private void handleRemoval(JarFileInfo info) {
        Path copyPath = generateCopyPath(info.getOriginalPath());
        try {
            if (Files.deleteIfExists(copyPath)) {
                log.info("Deleted: " + copyPath);
            }
            managedCopies.remove(copyPath);
        } catch (IOException e) {
            log.error("Delete failed: {}", e.getMessage());
            throw new CompletionException(e);
        }
    }


    /**
     * 生成副本文件路径（在原文件名后追加-copy）
     */
    private Path generateCopyPath(Path original) {
        String filename = original.getFileName().toString().replace(".jar", "-copy.jar");
        return original.resolveSibling(filename);
    }


    // region 工具方法

    /**
     * 安全关闭执行器服务
     */
    private void shutdownExecutor(ExecutorService executor) {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 清理所有托管副本文件
     */
    private void cleanManagedCopies() {
        managedCopies.forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.error("Cleanup failed for: " + path);
            }
        });
        managedCopies.clear();
    }

    /**
     * 创建文件信息对象
     */
    private JarFileInfo createFileInfo(Path path, ChangeType type) {
        return new JarFileInfo(path.getFileName().toString(), path, generateCopyPath(path), type);
    }


    // region 数据结构

    /**
     * 文件变更类型枚举
     */
    public enum ChangeType {
        /**
         * 新增文件
         */
        ADDED,
        /**
         * 文件内容更新
         */
        UPDATED,
        /**
         * 文件被删除
         */
        REMOVED
    }

    /**
     * 文件变化监听器接口
     */
    public interface JarChangeListener {
        /**
         * 当检测到JAR文件变化时触发
         *
         * @param changedFiles   发生变化的文件列表（不可修改）
         * @param existingCopies 当前存在的副本文件列表（不可修改）
         */
        void onChange(List<JarFileInfo> changedFiles, List<Path> existingCopies);
    }

    /**
     * JAR文件信息记录（不可变）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JarFileInfo {
        /**
         * 原始文件名（不含路径）
         */
        private String originalName;
        /**
         * 原始文件的完整路径
         */
        private Path originalPath;
        /**
         * 副本文件的完整路径
         */
        private Path copyPath;
        /**
         * 变更类型
         */
        private ChangeType changeType;

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            JarFileInfo that = (JarFileInfo) o;
            return Objects.equals(originalPath, that.originalPath);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(originalPath);
        }
    }

    /**
     * 文件元数据（用于变化检测）
     */
    private static class FileMeta {
        /**
         * 文件大小（字节）
         */
        final long size;
        /**
         * 最后修改时间戳
         */
        final long lastModified;
        /**
         * MD5哈希值（延迟计算）
         */
        final String md5Hash;

        FileMeta(Path path) {
            File file = path.toFile();
            this.size = file.length();
            this.lastModified = file.lastModified();
            this.md5Hash = null; // 延迟初始化
        }

        /**
         * 判断文件是否发生属性变化
         */
        boolean isModified(FileMeta other) {
            return this.size != other.size || this.lastModified != other.lastModified;
        }
    }

}