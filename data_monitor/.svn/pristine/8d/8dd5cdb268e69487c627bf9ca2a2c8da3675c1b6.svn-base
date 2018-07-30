package zibo.fileTrans;

import org.apache.commons.io.IOUtils;
import zibo.dataMonitor.PropertiesInfo;
import zibo.dataMonitor.UploadConfig;
import zibo.utils.FileUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FTP监控目录文件实体类
 */
public class FtpFile implements FtpFileInterface {
    /**
     * 文件的绝对路径
     */
    String fileAbsolutePath;

    /**
     * 获取文件绝对路径
     */
    public String getFileAbsolutePath() {
        return fileAbsolutePath;
    }

    /**
     * 获取文件对象
     */
    public File getFile() {
        return new File(fileAbsolutePath);
    }

    /**
     * 获取文件名称
     */
    public String getFileName() {
        return getFile().getName();
    }

    /**
     * 该文件是否需要上传给煤监局
     */
    public boolean isUploadFTP() {
        String[] arr = PropertiesInfo.getList("upload_ftp_sys_file");
        if (arr == null) {
            return false;
        }
        if (arr.length == 0) {
            return false;
        }
        for (int i = 0; i < arr.length; i++) {
            if (getFileSystemType().equals(arr[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件编码
     */
    public String getCodeByName() {
        if ("ws".equals(getFileSystemType())) {
            return "gb2312";
        }
        if ("ry".equals(getFileSystemType())) {
            return "gb2312";
        }
        if ("wz".equals(getFileSystemType())) {
            return "gb2312";
        }
        if ("yl".equals(getFileSystemType())) {
            return "gb2312";
        }
        return "UTF-8";
    }

    /**
     * 根据文件路径获取文件的内容
     */
    public String getContentOfFile() throws IOException {
        throw new RuntimeException(fileAbsolutePath + ":不是配置文件，不允许调用此方法！");
    }

    @Override
    public Integer getEnableValueOfConfig() throws IOException {
        throw new IOException(fileAbsolutePath + ":此文件不是配置文件，获取巡检值失败！");
    }

    @Override
    public void writeConfigFile(String content) throws IOException {
        throw new IOException(fileAbsolutePath + ":此文件不是配置文件，写入配置内容失败");
    }

    @Override
    public void operConfigWhenChange() throws IOException {
        throw new IOException(fileAbsolutePath + ":此文件不是配置文件，当文件变化时操作失败！");
    }

    /**
     * 构造函数
     *
     * @param fileAbsolutePath fileAbsolutePath
     */
    public FtpFile(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
    }

    /**
     * 修改配置文件巡检值
     */
    public void newEnableValueBeforeDelete() throws IOException {
        String systemDirOfMine = getSystemDirOfMine();
        String configPath = PropertiesInfo.get("monitor") + FileUtils.getSystemSeparator() + systemDirOfMine + FileUtils.getSystemSeparator() + "参数文件";
        //如果配置文件不存在，返回
        if (!new File(configPath).exists()) {
            return;
        }
        //获取配置文件Enable值
        Integer oldValue = getEnableValueOfConfig();
        if (oldValue == -1) {
            throw new RuntimeException(fileAbsolutePath + "：该配置文件无内容！");
        }
        //获取文件巡检值
        Integer fileValue;
        fileValue = getEnableValue();
        //获取值
        Integer value = oldValue - fileValue;
        //更改配置文件
        String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        String content = "[Set]\nEnable=" + value + "\nStationNumber=0\nTime=" + time;
        new ConfigFile(configPath).writeConfigFile(content);
    }

    /**
     * 判断FTP上传的文件是否完全上传成功
     * 如果文件没有完全上传成功，此时获取输入输出流的时候会报FileNotFoundException异常，只有成功的时候才能获取到
     */
    public boolean completeUploadOfFile() {
        OutputStream output = null;
        try {
            output = new FileOutputStream(getFile(), true);
        } catch (IOException e) {
            return false;
        } finally {
            IOUtils.closeQuietly(output);
        }
        InputStream input = null;
        try {
            input = new FileInputStream(getFile());
        } catch (IOException e) {
            return false;
        } finally {
            IOUtils.closeQuietly(input);
        }
        return true;
    }

    /**
     * 获取文件的煤矿系统目录文件夹
     */
    public String getSystemDirOfMine() {
        String monitorRootPath = PropertiesInfo.get("monitor");
        if (!fileAbsolutePath.startsWith(monitorRootPath)) {
            throw new RuntimeException("此文件不是被监控的文件");
        }
        fileAbsolutePath = fileAbsolutePath.replace(monitorRootPath, "").substring(1);
        fileAbsolutePath = fileAbsolutePath.substring(0, fileAbsolutePath.indexOf(FileUtils.getSystemSeparator()));
        return fileAbsolutePath;
    }

    /**
     * 根据文件名称获取巡检值
     */
    public Integer getEnableValue() {
        String fileSystemType = getFileSystemType();
        String key = fileSystemType + "_" + getFileName();
        return UploadConfig.valueOf(key).getValue();
    }

    /**
     * <获取文件系统类型>
     */
    public String getFileSystemType() {
        String monitorRootPath = PropertiesInfo.get("monitor");
        String ftpRootPath = PropertiesInfo.get("ftpDir");
        if ((!fileAbsolutePath.startsWith(monitorRootPath))&&(!fileAbsolutePath.startsWith(ftpRootPath))) {
            throw new RuntimeException(fileAbsolutePath + ":此文件不是被监控获取ftp目录下的文件，无法获取文件类型！");
        }
        String result = fileAbsolutePath;
        if (fileAbsolutePath.startsWith(monitorRootPath)){
            result = result.replace(monitorRootPath, "").substring(1);
        }else if (fileAbsolutePath.startsWith(ftpRootPath)){
            result = result.replace(ftpRootPath, "").substring(1);
        }
        result = result.substring(0, result.indexOf(FileUtils.getSystemSeparator()));
        result = result.substring(0, result.length() - 1);
        if (result.endsWith("sw")) {
            return "sw";
        } else if (result.endsWith("yl")) {
            return "yl";
        } else if (result.endsWith("wz")) {
            return "wz";
        } else if (result.endsWith("ry")) {
            return "ry";
        } else if (result.endsWith("ws")) {
            return "ws";
        } else if (result.endsWith("sb")) {
            return "sb";
        } else if (result.endsWith("dl")) {
            return "dl";
        }
        throw new RuntimeException(fileAbsolutePath + ":此文件获取文件所属系统类型失败");
    }

    /**
     * <获取文件标识>
     */
    public String getFileIdentification() {
        String fileSystemType = getFileSystemType();
        String fileName = new File(fileAbsolutePath).getName();
        if ("sw".equals(fileSystemType)) {
            return getFileTpyeOfSW(fileName);
        } else if ("yl".equals(fileSystemType)) {
            return getFileTpyeOfYL(fileName);
        } else if ("wz".equals(fileSystemType)) {
            return "ms_sj";
        } else if ("ry".equals(fileSystemType)) {
            return getFileTpyeOfRY(fileName);
        } else if ("ws".equals(fileSystemType)) {
            return getFileTpyeOfWS(fileName);
        } else if ("zs".equals(fileSystemType)) {
            throw new RuntimeException("获取文件类型失败,敬请期待,待开发");
        } else if ("dl".equals(fileSystemType)) {
            throw new RuntimeException("获取文件类型失败,敬请期待,待开发");
        }
        throw new RuntimeException("文件:" + fileAbsolutePath + "获取文件类型失败");
    }

    /**
     * <获取水文系统文件标识>
     *
     * @param fileName 文件路径名称
     * @return String
     */
    private String getFileTpyeOfSW(String fileName) {
        if (fileName.startsWith("降雨量配置")) {
            return "hy_jy_pz";
        } else if (fileName.startsWith("降雨量监测")) {
            return "hy_jy_sj";
        } else if (fileName.startsWith("水文点配置")) {
            return "hy_sw_pz";
        } else if (fileName.startsWith("水文监测")) {
            return "hy_sw_sj";
        } else if (fileName.startsWith("抽水井配置")) {
            return "hy_cs_pz";
        } else if (fileName.startsWith("抽水监测")) {
            return "hy_cs_sj";
        } else if (fileName.startsWith("涌水量采集点")) {
            return "hy_ys_pz";
        } else if (fileName.startsWith("涌水量")) {
            return "hy_ys_sj";
        } else if (fileName.startsWith("排水点配置信息")) {
            return "hy_ps_pz";
        } else if (fileName.startsWith("排水量")) {
            return "hy_ps_sj";
        }
        throw new RuntimeException("获取水文监测系统文件类型失败");
    }

    /**
     * <获取安全监测系统文件标识>
     *
     * @param fileName 文件路径名称
     * @return String
     */
    private String getFileTpyeOfWS(String fileName) {
        if (fileName.startsWith("模拟量显示")) {
            return "sm_mnlxs_sj";
        } else if (fileName.startsWith("开关量显示")) {
            return "sm_kglxs_sj";
        } else if (fileName.startsWith("分站状态显示")) {
            return "sm_fzztxs_sj";
        } else if (fileName.startsWith("分钟数据")) {
            return "sm_fz_sj";
        } else if (fileName.startsWith("开关量")) {
            return "sm_kgl_sj";
        } else if (fileName.startsWith("分站状态")) {
            return "sm_fzzt";
        } else if (fileName.startsWith("手动控制断电复电记录")) {
            return "sm_ddfd_sj";
        } else if (fileName.startsWith("监控系统参数定义日志")) {
            return "sm_csdy_sj";
        } else if (fileName.startsWith("累积量")) {
            return "sm_ljl_sj";
        } else if (fileName.startsWith("Stationdefine")) {
            return "sm_stationdefine_pz";
        } else if (fileName.startsWith("InOutDefine")) {
            return "sm_inOutDefine_pz";
        } else if (fileName.startsWith("模拟量异常")) {
            return "sm_mnlyc_sj";
        }
        throw new RuntimeException("获取安全监测系统文件类型失败");
    }
    /**
     * <获取人员定位系统文件标识>
     *
     * @param fileName 文件路径名称
     * @return String
     */
    private String getFileTpyeOfRY(String fileName) {
        if (fileName.startsWith("人员在线监测")) {
            return "pp_ryzxjc_sj";
        } else if (fileName.startsWith("煤矿井下人员在线统计")) {
            return "pp_mkjxry_sj";
        } else if (fileName.startsWith("重点区域人员在线监测")) {
            return "pp_zdqyry_sj";
        } else if (fileName.startsWith("分站人员在线监测")) {
            return "pp_fzry_sj";
        } else if (fileName.startsWith("人员分界出入标识监测")) {
            return "pp_ryfj_sj";
        } else if (fileName.startsWith("超时报警监测")) {
            return "pp_csbj_sj";
        } else if (fileName.startsWith("超员报警监测")) {
            return "pp_cybj_sj";
        } else if (fileName.startsWith("限制区域人员报警监测")) {
            return "pp_xzqy_sj";
        } else if (fileName.startsWith("特种作业人员行程监测")) {
            return "pp_tzzyry_sj";
        } else if (fileName.startsWith("人员救助报警监测")) {
            return "pp_ryjz_sj";
        } else if (fileName.startsWith("矿井参数文件")) {
            return "pp_mj_pz";
        } else if (fileName.startsWith("井口参数文件")) {
            return "pp_jk_pz";
        }else if (fileName.startsWith("区域参数文件")) {
            return "pp_qy_pz";
        }else if (fileName.startsWith("分站参数文件")) {
            return "pp_fz_pz";
        }else if (fileName.startsWith("班组参数文件")) {
            return "pp_bz_pz";
        }else if (fileName.startsWith("人员参数文件")) {
            return "pp_ry_pz";
        }else if (fileName.startsWith("照片参数文件")) {
            return "pp_zp_pz";
        }else if (fileName.startsWith("监测图参数文件")) {
            return "pp_jct_pz";
        }else if (fileName.startsWith("视频动画参数文件")) {
            return "pp_spdh_pz";
        }else if (fileName.startsWith("特殊工种路线预设文件")) {
            return "pp_tsgz_pz";
        }else if (fileName.startsWith("分站状态")) {
            return "pp_fzzt_sj";
        }
        throw new RuntimeException("获取人员定位系统文件类型失败");
    }
    /**
     * <获取应力系统文件标识>
     *
     * @param fileName 文件路径名称
     * @return String
     */
    private String getFileTpyeOfYL(String fileName) {
        fileName = fileName.substring(0, fileName.indexOf(".txt"));
        if (fileName.endsWith("_data")) {
            return "stress_data_sj";
        } else if (fileName.endsWith("_config")) {
            return "stress_config_pz";
        }
        throw new RuntimeException("获取应力监测系统文件类型失败");
    }
}
