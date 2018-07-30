package zibo.dataMonitor;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

/**
 * 文件夹监控
 *
 * @author Goofy <a href="http://www.xdemo.org/">http://www.xdemo.org/</a>
 * @Date 2015年7月3日 上午9:21:33
 */
public class WatchDir {
    private static final Logger logger = Logger.getLogger(WatchDir.class);

    /**
     * 监视器
     */
    private final WatchService watcher;

    /**
     * 监视的对象集合
     */
    private final Map<WatchKey, Path> keys;

    /**
     * 是否有子目录
     */
    private final boolean subDir;

    /**
     * 构造方法
     *
     * @param file   文件目录，不可以是文件
     * @param subDir 文件目录
     * @throws Exception Exception
     */
    WatchDir(File file, boolean subDir, FileActionCallback callback) throws Exception {
        if (!file.isDirectory())
            throw new Exception(file.getAbsolutePath() + "is not a directory!");
        //初始化参数
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.subDir = subDir;
        //把需要监测的文件注册到监测中心
        Path dir = Paths.get(file.getAbsolutePath());
        if (subDir) {
            registerAll(dir);
        } else {
            register(dir);
        }
        processEvents(callback);
    }

    public boolean listIsEmpty(List<?> list) {
        if (null == list) {
            return true;
        }
        if (list.size() == 0) {
            return true;
        }
        return false;
    }

    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * 观察指定的目录
     *
     * @param dir 指定目录
     * @throws IOException IOException
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
        keys.put(key, dir);
    }

    /**
     * 观察指定的目录，并且包括子目录
     */
    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 发生文件变化的回调函数
     */
    private void processEvents(FileActionCallback callback) {
        for (; ; ) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
            Path dir = keys.get(key);
            if (dir == null) {
                logger.error("操作未识别");
                continue;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                Kind kind = event.kind();
                // 事件可能丢失或遗弃
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                // 目录内的变化可能是文件或者目录
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
                File file = child.toFile();
                if (kind.name().equals(FileAction.DELETE.getValue())) {
                    if (!file.isDirectory()) {
                        callback.delete(file);
                    }
                } else if (kind.name().equals(FileAction.CREATE.getValue())) {
                    if (!file.isDirectory()) {
                        callback.create(file);
                    }
                } else if (kind.name().equals(FileAction.MODIFY.getValue())) {
                    if (!file.isDirectory()) {
                        callback.modify(file);
                    }
                } else {
                    continue;
                }

                //如果新建的是文件夹，加入监控
                if (subDir && (kind == StandardWatchEventKinds.ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {

                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                // 移除不可访问的目录
                // 因为有可能目录被移除，就会无法访问
                keys.remove(key);
                // 如果待监控的目录都不存在了，就中断执行
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

}
