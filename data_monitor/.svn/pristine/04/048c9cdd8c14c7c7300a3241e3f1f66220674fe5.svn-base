package zibo.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.log4j.Logger;
import zibo.dataMonitor.Client;
import zibo.dataMonitor.PropertiesInfo;
import zibo.fileTrans.FtpFile;
import zibo.utils.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class FTPUtils {
    private static final Logger logger = Logger.getLogger(FTPUtils.class);
    // 需要连接到的ftp端的ip
    private static final String IP = PropertiesInfo.get("ftp_ip");
    // 连接端口，默认21
    private static final int PORT = PropertiesInfo.getInt("ftp_port");
    // 要连接到的ftp端的名字
    private static final String NAME = PropertiesInfo.get("ftp_login_name");
    // 要连接到的ftp端的对应得密码
    private static final String PWD = PropertiesInfo.get("ftp_pwd");
    //
    private static final FTPClient FTP = new FTPClient();
    /**
     * 设置缓冲区大小4M
     **/
    private static final int BUFFER_SIZE = 1024 * 1024 * 4;
    /**
     * 系统分隔符
     */
    private static final String SEPERATE = PropertiesInfo.getInt("is_linux") == 0 ? "\\" : "/";

    static {
        //启动类时打开连接FTP服务器
        FTP.setCharset(Charset.forName("UTF-8"));
        FTP.setControlEncoding("UTF-8");
        try {
            FTP.connect(IP, PORT);
            FTP.login(NAME, PWD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件 *
     *
     * @param pathName FTP服务器文件目录 *
     * @param filename 文件名称 *
     * @param destPath 下载后的文件路径 *
     * @return 下载的文件路径
     */
    public static String downloadFile(String pathName, String filename, String destPath) {
        pathName = StringUtils.trim(pathName, "/");
        String result = "";
        boolean flag = false;
        OutputStream os;
        try {
            //切换FTP目录
            boolean isSuccess = FTP.changeWorkingDirectory(pathName);
            //找不到指定的FTP目录，返回
            if (!isSuccess) {
                return result;
            }
            FTPFile[] ftpFiles = FTP.listFiles();
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {
                    File localFile = new File(destPath + "/" + file.getName());
                    os = new FileOutputStream(localFile);
                    FTP.retrieveFile(file.getName(), os);
                    os.close();
                    flag = true;
                    result = destPath + "/" + file.getName();
                }
            }
            if (!flag) {
                throw new Exception("下载文件失败");
            }
            return result;
        } catch (Exception e) {
            logger.error("下载文件失败");
        }
        return null;
    }

    /**
     * 上传文件-->（需要从新写，支持断线续传）
     *
     * @param pathName    ftp服务保存路径地址
     * @param fileName    上传到ftp的文件名
     * @param srcFileName 待上传文件的名称（绝对地址）
     * @return boolean
     */
    public static boolean uploadFile(String pathName, String fileName, String srcFileName) {
        boolean flag = false;
        pathName = StringUtils.trim(pathName, "/");
        InputStream inputStream;
        try {
            //每次上传之前都返回到FTP服务器根目录
            FTP.changeWorkingDirectory(PropertiesInfo.get("ftp_path"));
            inputStream = new FileInputStream(new File(srcFileName));
            FTP.setBufferSize(BUFFER_SIZE);
            FTP.setFileType(FTPClient.BINARY_FILE_TYPE);
            // 目录不存在，则递归创建
            boolean exist = FTP.changeWorkingDirectory(pathName);
            if (!exist) {
                createDirectorys(pathName);
                FTP.changeWorkingDirectory(pathName);
            }
            FTP.storeFile(fileName, inputStream);
            //每次上传结束都必须关闭流对象，否则无法再获取该文件的流对象
            inputStream.close();
            flag = true;
        } catch (FileNotFoundException e) {
            logger.error(srcFileName + ":上传至FTP服务器失败！原因为找不到" + srcFileName + "文件!");
        } catch (IOException e) {
            logger.error(srcFileName + ":上传至FTP服务器失败！原因为IOException");
        }
        return flag;
    }

    public static boolean uploadFile(String pathName, String srcFileName) {
        return uploadFile(pathName, new File(srcFileName).getName(), srcFileName);
    }

    public static boolean uploadFile(String srcFileName) {
        String pathName = getPatnNameFromSrcFileName(srcFileName);
        return uploadFile(pathName, srcFileName);
    }

    /**
     * 根据源文件获取FTP路径
     * @param srcFileName 源文件路径
     * @return String
     */
    public static String getPatnNameFromSrcFileName(String srcFileName){
        String pathName = srcFileName.substring(PropertiesInfo.get("ftpDir").length());
        pathName = StringUtils.trim(pathName, SEPERATE);
        if (!pathName.contains(SEPERATE)) {
            pathName = "";
        } else {
            pathName = pathName.substring(0, pathName.lastIndexOf(SEPERATE));
            pathName = pathName.replace(SEPERATE, "/");
        }
        return pathName;
    }

    /**
     * 创建多层级FTP目录
     * <p>
     * 进入总目录，如果进不去，进入第一级目录，如果进不去，创建一级目录，进入一级目录，。。。
     *
     * @param pathName 多层级目录
     * @throws IOException IOException
     */
    private static void createDirectorys(String pathName) throws IOException {
        if (StringUtils.isEmpty(pathName)) {
            return;
        }
        pathName = StringUtils.trim(pathName, "/");
        if (!FTP.changeWorkingDirectory(pathName)) {
            String dir = getFirstDir(pathName);
            boolean flag = FTP.changeWorkingDirectory(dir);
            if (!flag) {
                FTP.makeDirectory(dir);
                FTP.changeWorkingDirectory(dir);
            }
            pathName = pathName.substring(dir.length());
            createDirectorys(pathName);
        }
    }

    /**
     * 获取dir下一级目录
     *
     * @param dir 目录
     * @return String
     */
    private static String getFirstDir(String dir) {
        dir = StringUtils.trim(dir, "/");
        if (!dir.contains("/")) {
            return dir;
        }
        dir = dir.substring(0, dir.indexOf("/"));
        return dir;
    }


}
