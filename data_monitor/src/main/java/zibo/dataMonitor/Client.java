package zibo.dataMonitor;

import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import zibo.fileTrans.ConfigFile;
import zibo.fileTrans.FtpFile;
import zibo.fileTrans.FtpFileInterface;
import zibo.utils.FTPUtils;
import zibo.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <监控启动入口>
 *
 * @author BONC
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class Client {
    public static Integer enableValue = 0;
    private static final Logger logger = Logger.getLogger(Client.class);

    /**
     * 程序主入口
     */
    public static void main(String[] args) {
        //此种方式备份的文件是以文件为最小单位，如果中间有一个文件备份失败，不影响其他文件
        // 断线续传
        continueRunBody();
        //每次启动时把所有巡检配置文件中巡检值归零
        returnZero();
        // 开启监控
        startMonitor();
        //开启上传ftp定时任务
//        startUploadToProvince();
    }

    /**
     * 开启上传文件至省煤监局ftp的定时器
     */
    private static void startUploadToProvince() {
        Runnable runnable = () -> uploadFTP();
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 0, PropertiesInfo.getInt("timer_interval"), TimeUnit.SECONDS);
        logger.info("山东省煤监局FTP上传定时器开启...");
    }

    /**
     * 上传FTP
     */
    private static void uploadFTP() {
        List<File> list = FileUtils.getAllFilesFromDir(PropertiesInfo.get("ftpDir"));
        for (File file : list) {
            String fileSystemType = new FtpFile(file.getAbsolutePath()).getFileSystemType();
            if ("ws".equals(fileSystemType) || "ry".equals(fileSystemType)) {
                //通过巡检值控制上传的文件
                uploadFTPWithEnable(file);
            } else {
                uploadFTPWithOutEnable(file);
            }
        }
    }
    /**
     * 不涉及巡检值的上传FTP
     */
    private static void uploadFTPWithEnable(File file){
        //1、下载ftp上的
    }
    /**
     * 不涉及巡检值的上传FTP
     */
    private static void uploadFTPWithOutEnable(File file){
        boolean flag = FTPUtils.uploadFile(file.getAbsolutePath());
        if (flag) {
            logger.info(file.getAbsolutePath()+":上传成功！");
            file.delete();
        }
    }

    /**
     * <断线续传>
     */
    private static void continueRunBody() {
        List<File> fileList = FileUtils.getAllFilesFromDir(PropertiesInfo.get("monitor"));
        if (listIsEmpty(fileList)) {
            return;
        }
        for (File file : fileList) {
            try {
                runBody(file, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 每次启动时把所有巡检配置文件中巡检值归零
     */
    private static void returnZero() {
        List<File> list = FileUtils.getFixedFilesFromDir(PropertiesInfo.get("monitor"), "参数文件");
        String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        String content = "[Set]\nEnable=0\nStationNumber=0\nTime=" + time;
        for (File file : list) {
            try {
                new ConfigFile(file.getAbsolutePath()).writeConfigFile(content);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * <开启监控>
     */
    private static void startMonitor() {
        logger.info("54FTP监控开启中...");
        final File file = new File(PropertiesInfo.get("monitor"));
        new Thread(() -> {
            try {
                new WatchDir(file, true, new FileActionCallback() {
                    @Override
                    public void create(File file1) {
                        if (isConfig(file1))
                            return;
                        logger.info("54FTP创建:" + file1.getAbsolutePath());
                    }

                    @Override
                    public void delete(File file1) {
                        if (isConfig(file1))
                            return;
                        logger.info("54FTP删除:" + file1.getAbsolutePath());
                    }

                    @Override
                    public void modify(File file1) {
                        try {
                            synchronized (file) {
                                runBody(file1, false);
                            }
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                    }
                });
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        }).start();
        logger.info("54FTP正在监视文件夹:" + file.getAbsolutePath() + "的变化");
    }

    /**
     * <运行主体内容>
     */
    private static void runBody(File file, boolean isIgnoreEnable) throws Exception {
        //如果是系统文件，不处理
        if (file.getName().startsWith(".")) return;
        if (FileUtils.isFileAndExistsAndIsNotNull(file)) {
            //参数文件不参加处理文件，只处理config文件
            if (isConfig(file)) {
                new ConfigFile(file.getAbsolutePath()).operConfigWhenChange();
                return;
            }
            FtpFileInterface ftpFile = new FtpFile(file.getAbsolutePath());
            //判断文件是否完全上传成功,如果没有完全上传成功，返回！
            boolean flag = ftpFile.completeUploadOfFile();
            if (!flag) return;
            //复制文件
            copyFiles(file);
            // 修改配置文件并删除文件
            if (!isIgnoreEnable) {
                updateConfigFile(ftpFile);
            }
            file.delete();
        }
    }

    /**
     * 处理完文件之后修改配置文件
     *
     * @param ftpFile 处理的文件对象
     * @throws Exception 的
     */
    private static void updateConfigFile(FtpFileInterface ftpFile) throws Exception {
        //查询配置文件中的Enable值，小于等于零时，累计存入变量而不修改巡检配置文件，否则修改配置文件
        String systemDirOfMine = ftpFile.getSystemDirOfMine();
        String configPath = PropertiesInfo.get("monitor") + FileUtils.getSystemSeparator() + systemDirOfMine + FileUtils.getSystemSeparator() + "参数文件";
        //如果该文件所在目录没有巡检配置文件，跳过
        if (new File(configPath).exists()) {
            Integer oldValue = new ConfigFile(configPath).getEnableValueOfConfig();
            Integer fileValue = ftpFile.getEnableValue();
            if (oldValue == null) {
                oldValue = 0;
            }
            if (oldValue <= 0) {
                enableValue = enableValue + fileValue;
            } else {
                ftpFile.newEnableValueBeforeDelete();
            }
        }
    }

    /**
     * 复制3份数据文件，一份为备份文件，一份给flume处理，还有一份为省煤监局
     *
     * @param file 源文件
     * @throws IOException IOException
     */
    private static void copyFiles(File file) throws IOException {
        boolean isLinux = Integer.valueOf(PropertiesInfo.get("is_linux")) == 1;
        String srcFileName = file.getAbsolutePath();
        String fileName = FileUtils.getDestFileNameWhenCopy(file.getName(), true);
        List<String> list = new ArrayList<>();
        String backupsDir = Client.combinationDir(PropertiesInfo.get("monitor"), PropertiesInfo.get("backupsDir"), srcFileName, isLinux);
        String backupsFullFileName = combinationFullFileName(fileName, backupsDir, isLinux);
        list.add(backupsFullFileName);
        String flumeDir = Client.combinationDir(PropertiesInfo.get("monitor"), PropertiesInfo.get("flumeDir"), srcFileName, isLinux);
        String flumeFullFileName = combinationFullFileName(fileName, flumeDir, isLinux);
        list.add(flumeFullFileName);
        //查看该文件是否需要上传给省煤监局
        if (new FtpFile(srcFileName).isUploadFTP()) {
            String ftpDir = Client.combinationDir(PropertiesInfo.get("monitor"), PropertiesInfo.get("ftpDir"), srcFileName, isLinux);
            String ftpFullFileName = combinationFullFileName(file.getName(), ftpDir, isLinux);
            list.add(ftpFullFileName);
        }
        copyFile(srcFileName, list);
    }

    /**
     * <文件向多个位置复制>
     *
     * @param srcFileName  源文件
     * @param destPathList 目录目录集合
     * @throws IOException IOException
     */
    private static void copyFile(String srcFileName, List<String> destPathList) throws IOException {
        if (listIsEmpty(destPathList)) {
            return;
        }
        for (String destPath : destPathList) {
            FileUtils.copyFileWithChannel(srcFileName, destPath);
        }
    }

    /**
     * <组合目录>
     *
     * @param monitorDir   监控目录
     * @param destRootDir  目录根目录
     * @param fileFullName 文件全路劲
     * @return String
     */
    private static String combinationDir(String monitorDir, String destRootDir, String fileFullName, boolean isLinux) {
        String separator = isLinux ? "/" : "\\";
        String child = fileFullName.replace(monitorDir, "");
        child = child.startsWith(separator) ? child.substring(1) : child;
        child = child.substring(0, child.lastIndexOf(separator));
        String destDir = destRootDir + separator + child;
        File file = new File(destDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return destDir;
    }

    /**
     * 制定文件的全路径
     *
     * @param fileName 文件名称
     * @param destPath 目标路径
     * @param isLinux  是否在linux系统下
     * @return String
     */
    private static String combinationFullFileName(String fileName, String destPath, boolean isLinux) {
        String separator = isLinux ? "/" : "\\";
        destPath = destPath.endsWith(separator) ? destPath : destPath + separator;
        return destPath + fileName;
    }

    /**
     * 判断一个文件是否为参数文件
     *
     * @param file 源文件
     * @return boolean
     */
    private static boolean isConfig(File file) {
        return file.exists() && file.getName().equals("参数文件");
    }

    /**
     * <list是否为空>
     *
     * @param list list
     * @return boolean
     */
    private static boolean listIsEmpty(List<?> list) {
        if (null == list) {
            return true;
        }
        if (list.size() == 0) {
            return true;
        }
        return false;
    }
}
