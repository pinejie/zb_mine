package zibo.utils;

import zibo.dataMonitor.PropertiesInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    /**
     * <获取目录下所有文件>
     *
     * @param dir 目录
     * @return List
     */
    public static List<File> getAllFilesFromDir(String dir) {
        File file = new File(dir);
        List<File> list = new ArrayList<>();
        if (file.isFile()) {
            list.add(file);
            return list;
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File file1 : files) {
                if (file1.isFile()) {
                    list.add(file1);
                } else if (file1.isDirectory()) {
                    list.addAll(getAllFilesFromDir(file1.getAbsolutePath()));
                }
            }
        }
        return list;
    }

    /**
     * 获取指定目录下的所有某个同名文件
     *
     * @param dir 目录
     * @param fileName 文件名
     * @return List
     */
    public static List<File> getFixedFilesFromDir(String dir, String fileName) {
        List<File> oldlist = getAllFilesFromDir(dir);
        List<File> list = new ArrayList<>();
        for (File file : oldlist) {
            if (file.getName().equals(fileName)) {
                list.add(file);
            }
        }
        return list;
    }

    /**
     * 复制文件（FileChannel）
     * @param srcFileName 来源文件路径
     * @param destFileName 目标文件路径
     * @throws  IOException IOException
     */
    public static void copyFileWithChannel(String srcFileName,String destFileName) throws IOException {
        FileInputStream in ;
        FileOutputStream out ;
        in = new FileInputStream(new File(srcFileName));
        out = new FileOutputStream(new File(destFileName));
        FileChannel channelIn = in.getChannel();
        FileChannel channelOut = out.getChannel();
        channelOut.transferFrom(channelIn, 0, channelIn.size());
        channelOut.close();
        channelIn.close();
        in.close();
        out.close();
    }
    /**
     * <获取目标文件的文件名称>
     *
     * @param srcFileName 源文件路径名
     * @param isChangeName 是否加时间戳
     * @return String
     */
    public static String getDestFileNameWhenCopy(String srcFileName, boolean isChangeName) {
        String preName ;
        String suffName ;
        long currentTime = System.currentTimeMillis();
        if (null == srcFileName) {
            return null;
        }
        String name = new File(srcFileName).getName();
        if (!isChangeName) {
            return name;
        }
        if (name.contains(".")) {
            preName = name.substring(0, name.lastIndexOf("."));
            suffName = name.substring(name.lastIndexOf("."));
            return preName + "_" + currentTime + suffName;
        }
        preName = name;
        return preName + "_" + currentTime;

    }

    /**
     * 获取系统分隔符
     *
     * @return 系统分隔符
     */
    public static String getSystemSeparator() {
        boolean isLinux = PropertiesInfo.get("is_linux").equals("1");
        return isLinux ? "/" : "\\";
    }

    /**
     * <判断file是文件且存在且长度不为零>
     *
     * @param file 文件对象
     * @return Boolean
     */
    public static boolean isFileAndExistsAndIsNotNull(File file) {
        return file.isFile() && file.exists() && file.length() > 0;
    }
}
