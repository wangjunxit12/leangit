package com.meibanlu.driver.tool;

import android.location.Location;

import com.amap.api.location.AMapLocation;
import com.meibanlu.driver.bean.PositionVo;
import com.meibanlu.driver.bean.TaskDetail;

import java.util.List;

/**
 * CommonData 全局数据
 *
 * @author lhq
 * @date 2017/9/13
 */

public class CommonData {
    public static int windowWidth;
    public static int WindowHeight;
    /**
     * 所有的打卡信息
     */
    public static List<TaskDetail> listTask;
    /**
     * 出发打卡保存的信息
     */
    public static HolderTrip holderTrip;
    /**
     * 消息未读条数
     */
    public static int notReadNumber;
    /**
     * 定位的那个
     */
    public static Location aMapLocation;
    public static PositionVo judgePosition;
    /**
     * 关闭app
     */
    public static boolean openApp;
    /**
     * 是否允许访问网络
     */
    public static boolean netAccess;
    /**
     * 版本号
     */
    public static String version;
    /**
     * 定位异常
     */
    public static Integer locationErrorCode = 0;

    public static boolean isLogin=false;
}
