package com.meibanlu.driver.tool;

import com.iflytek.cloud.thirdparty.S;

/**
 * Constants 常量的工具类
 *
 * @author lhq
 * @date 2017/9/13
 */

public interface Constants {

    /**
     * 关闭播放进度条
     */
    int INSTALL_APK = 0x4;
    /**
     * 更新软件到状态栏的notificationId
     */
    int UPDATE_NOTIFICATION_ID = 1234;

    /**
     * 正常班次判断为前后 10 分钟
     */
    int TRIP_TOLERATE_TIME = 10;

    /**
     * 待出发状态
     */
    int STATUS_PRE_DEPART = 0;
    /**
     * 出发打卡成功
     */
    int STATUS_DEPART = 1;
    /**
     * 到站打卡成功
     */
    int STATUS_ARRIVE = 2;
    /**
     * 定位回调时间
     */
    int LOCATION_TIME = 2;
    /**
     * 打开app上传经纬度时间间隔为30秒
     */
    int GSP_UPLOAD_INTERVAL = 30;

    /**
     * 5秒判断一次
     */
    int SIGN_TIME_INTERVAL = 5;

    /**
     * 首页选择已经完成
     */
    int FINISH = 2;
    /**
     * 首页选择未完成
     */
    int WAIT_FINISH = 1;
    /**
     * 自动打卡
     */
    int MODE_AUTO = 1;
    /**
     * 手动打卡
     */
    int MODE_MAN_SUCCESS = 2;
    /**
     * 异常打卡
     */
    int MODE_MAN_ABNORMAL = 4;

    /**
     * 选择当前班次的时间最小值
     */
    int TRIP_SELECT_TIME_LEFT = -10;

    /**
     * 选择当前班次的时间最大值
     */
    int TRIP_SELECT_TIME_RIGHT = 10;

    /**
     * 设置当前班次的时间最小值
     */
    int SET_TRIP_SELECT_TIME_LEFT = -25;

    /**
     * 设置当前班次的时间最大值
     */
    int SET_TRIP_SELECT_TIME_RIGHT = 25;

    /**
     * 定时间打卡的时间最小值
     */
    int DEPART_TIME_LEFT = -8;

    /**
     * 定时间打卡的时间最大值
     */
    int DEPART_TIME_RIGHT = 5;

    /**
     * 重启定位的时间
     */
    int RESTART_TIME = 120;

    interface KEYS {
        String UNPOST_TRIP = "tripUploadFail";
    }

    String Update_URL="http://114.115.175.229:8887/update/new/app-release.apk";

}
