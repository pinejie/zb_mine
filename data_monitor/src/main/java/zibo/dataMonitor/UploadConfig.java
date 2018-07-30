package zibo.dataMonitor;

public enum UploadConfig {
    ws_模拟量显示(16), ws_开关量显示(32), ws_分站状态显示(256), ws_分钟数据(2), ws_开关量(8),
    ws_分站状态(512), ws_手动控制断电复电记录(1024), ws_监控系统参数定义日志(2048), ws_累积量(4096),
    ws_stationdefine(128), ws_InOutDefine(8192), ws_模拟量异常(16384),

    ry_人员在线监测(2),ry_煤矿井下人员在线统计(8),ry_重点区域人员在线监测(16),
    ry_分站人员在线监测(32),ry_人员分界出入标识监测(128),ry_超时报警监测(256), ry_超员报警监测(512),
    ry_限制区域人员报警监测(1024), ry_特种作业人员行程监测(2048), ry_人员救助报警监测(8192),
    ry_矿井参数文件(4096),ry_井口参数文件(4096), ry_区域参数文件(4096),ry_分站参数文件(4096),
    ry_班组参数文件(4096),ry_人员参数文件(4096),ry_照片参数文件(4096), ry_监测图参数文件(4096),
    ry_视频动画参数文件(4096),ry_特殊工种路线预设文件(4096),ry_分站状态(4096);
    private Integer value;

    UploadConfig(Integer value) {
        this.value = value;
    }


    public Integer getValue() {
        return value;
    }
}
