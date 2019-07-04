package com.meibanlu.driver.tool.web;

/**
 * 接口的枚举
 * 内部需要替换的参数使用 PARAM+序号构成，后面配注解
 *
 * @author meibanlu
 */
public enum WebInterface {

    LOGIN("driver/login"), //登录
    UPLOAD_GPS("driver/uploadGps"), //上传实时的gps
    UPLOAD_TXT("driver/uploadTripFile"), //完成上传txt
    TODAY_TASK("driver/todayTrips"), //今天的任务
    DRIVER_SIGN("driver/sign"), //司机打卡
    GET_LINE_GPS("driver/lineGps"), //获取预定路线的Gps
    DRIVE_RECORD("driver/userTrips"), //获取行车记录
    RECORD_GPS("driver/fileStream"), //获取行车记录具体的Gps
    CHANGE_PASSWORD("driver/password"), //修改密码
    VERSION_UPDATE("driver/android/latestVersion"),//版本更新
    GET_BONE("driver/bonus"),//获取绩效奖金
    UPLOAD_POSITION("driver/uploadDriverGps"),//实时上传司机的经纬度位置信息
    BATCH_UPLOAD_POSITION("driver/batchUploadDriverGps"),//实时上传司机的经纬度位置信息
    UPLOAD_OIL("driver/logMileage"),//油耗
    UPLOAD_ERROR("driver/errorLog")//油耗

    ;
    //成员变量
    private String url;

    WebInterface(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
