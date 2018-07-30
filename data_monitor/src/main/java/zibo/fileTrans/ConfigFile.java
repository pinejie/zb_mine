package zibo.fileTrans;

import org.apache.log4j.Logger;
import zibo.dataMonitor.Client;
import zibo.utils.IOUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FTP中的的配置文件
 */
public class ConfigFile extends FtpFile {
    /**
     * 构造函数
     *
     * @param fileAbsolutePath 文件绝对路径
     */
    public ConfigFile(String fileAbsolutePath) {
        super(fileAbsolutePath);
    }

    /**
     * 获取配置文件巡检值
     */
    public Integer getEnableValueOfConfig() throws IOException {
        //获取文件内容
        String content = getContentOfFile();
        //如果内容为空，返回-1
        if (content == null) {
            return -1;
        }
        //获取值
        String[] arr = content.split("\n");
        if (arr.length < 2)
            throw new RuntimeException(fileAbsolutePath + ":配置文件格式有误！");
        content = arr[1];
        String[] values = content.split("=");
        if (values == null)
            throw new RuntimeException(fileAbsolutePath + ":解析巡检配置文件有误！");
        if (values.length < 2)
            throw new RuntimeException(fileAbsolutePath + ":解析巡检配置文件有误！");
        return Integer.valueOf(values[1]);
    }

    /**
     * 修改配置文件
     *
     */
    public void writeConfigFile(String content) throws IOException {
        Writer writer = new FileWriter(fileAbsolutePath);
        writer.write(content);
        writer.flush();
        writer.close();
    }

    /**
     * 当配置文件变化时操作
     */
    public void operConfigWhenChange() throws IOException {
        Integer configEnable = getEnableValueOfConfig();
        if (configEnable <= 0) {
            return;
        }
        if (Client.enableValue <= 0) {
            return;
        }
        String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        Integer value = configEnable - Client.enableValue;
        String content = "[Set]\nEnable=" + value + "\nStationNumber=0\nTime=" + time;
        Client.enableValue = 0;
        writeConfigFile(content);
    }

    public void newEnableValueBeforeDelete() throws IOException {
        throw new IOException(fileAbsolutePath + ":该文件是配置文件，修改失败！");
    }
    /**
     * 根据文件路径获取文件的内容
     * @return String
     * @throws IOException IOException
     */
    public String getContentOfFile() throws IOException {
        File file = getFile();
        if (!file.exists()){
            return null;
        }
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bReader = IOUtil.getBufferedReader(fileAbsolutePath, getCodeByName());
        String s;
        while (null != (s = bReader.readLine())) {
            stringBuffer.append(s).append("\n");
        }
        return stringBuffer.toString();
    }
}
