package com.meibanlu.driver.tool;

/**
 * 显示状态的描述信息
 *
 * @author lhq
 * @date 2018-03-02
 */

public interface StateConstants {
    /**
     * 未完成的全为滚动班次
     */
    int ALL_ROLLING_TRIP = 0;
    /**
     * 未完成的全为固定班次
     */
    int ALL_FIXED_TRIP = 1;
    /**
     * 未完成的为固定加上滚动班次
     */
    int ROLLING_FIXED_TRIP = 2;


    /**
     * 提醒尽快发车
     */
    String RUN_NOW = "请尽快发车,";
    /**
     * 定时范围打卡
     */
    String TOTAL_TIME_SIGN = "分钟后将会自动打卡";
    /**
     * 出范围打卡
     */
    String TOTAL_SCOPE_SIGN = "分钟内开出范围自动打卡";


    /**
     * 无当前班次，仅有固定班次
     */
    String NO_TRIP_FIXED = "未到发车时间";
    /**
     * 无当前班次，有滚动班次和固定班次
     */
    String NO_TRIP_ROLLING_FIXED = "固定班次没到发车时间,并且滚动班次没有在打卡范围内";
    /**
     * 无当前班次，仅有滚动班次
     */
    String NO_TRIP_ROLLING = "没有在打卡范围内";


    /**
     * 运行中,未到达终点范围
     */
    String RUN_TRIP = "运行中,未到达终点范围";
    /**
     * 有当前班次，当前为滚动班次班次
     */
    String HAVE_TRIP_ROLLING = "出范围打卡,检查班次是否正确,如果错误点击\"设为当前\",选择班次";
    /**
     * 有当前班次,不在范围内
     */
    String HAVE_TRIP_NOT_FIXED = "不在打卡范围内";
    /**
     * 有当前班次，当前班次为固定班次，在范围内
     */
    String HAVE_TRIP_FIXED = "在范围内";

    /**
     * 获取状态失败
     */
    String GET_STATE_FAIL = "状态获取中...";
}
