package com.meibanlu.driver.tool;

import android.location.Location;

import com.amap.api.location.AMapLocation;
import com.meibanlu.driver.activity.HomePageActivity;
import com.meibanlu.driver.bean.TaskDetail;

import java.util.List;

/**
 * 运行状态
 *
 * @author lhq
 * @date 2018-03-02
 */

public class UtilState {
    /**
     * 处理班次信息
     * 返回状态
     *
     * @return state
     */
    public synchronized static String getState() {
        HolderTrip holderTrip = CommonData.holderTrip;
        if (holderTrip == null) {
            //没有当前班次
            List<TaskDetail> taskList = HomePageActivity.getInstance().notFinishTask;
            if (taskList != null && taskList.size() > 0) {
                int tripState = getTripState(taskList);
                switch (tripState) {
                    case StateConstants.ALL_FIXED_TRIP:
                        //未完成全是固定班次
                        return StateConstants.NO_TRIP_FIXED;
                    case StateConstants.ALL_ROLLING_TRIP:
                        //未完成全是滚动班次
                        return StateConstants.NO_TRIP_ROLLING;
                    case StateConstants.ROLLING_FIXED_TRIP:
                        //未完成全是滚动加上固定班次
                        return StateConstants.NO_TRIP_ROLLING_FIXED;
                    default:
                }
            }
        } else {
            //有当前班次
            TaskDetail trip = holderTrip.getTrip();
            if (trip != null) {
                if (trip.getStatus() == Constants.STATUS_DEPART) {
                    //运行中
                    return StateConstants.RUN_TRIP;
                } else {
                    //未运行
                    boolean isRoll = trip.isRoll();
                    if (isRoll) {
                        //滚动班次
                        return StateConstants.HAVE_TRIP_ROLLING;
                    } else {
                        //固定班次
                        return fixedSignConditions(trip);
                    }
                }
            }
        }
        return StateConstants.GET_STATE_FAIL;
    }

    /**
     * @param taskList 当前未完成所有的班次
     * @return 状态
     */
    private static int getTripState(List<TaskDetail> taskList) {
        //是否有固定班次
        boolean haveFixed = false;
        //是都有滚动班次
        boolean haveRolling = false;
        for (TaskDetail item : taskList) {
            if (item.isRoll()) {
                haveRolling = true;
            } else {
                haveFixed = true;
            }
        }
        if (haveFixed && !haveRolling) {
            //仅有固定班次1
            return StateConstants.ALL_FIXED_TRIP;
        } else if (haveRolling && !haveFixed) {
            return StateConstants.ALL_ROLLING_TRIP;
        } else {
            return StateConstants.ROLLING_FIXED_TRIP;
        }
    }


    /**
     * 固定的打卡条件
     *
     * @return str
     */
    private static String fixedSignConditions(TaskDetail item) {
        Location aMapLocation = CommonData.aMapLocation;
        if (aMapLocation != null) {
            boolean in = GpsTool.checkPointInPolygon(aMapLocation.getLongitude() + ";" +
                    aMapLocation.getLatitude(), item.getDepartStation().getLngLat(), item.getDepartStation().getAreaRadius());
            if (in) {
                //当前时间与班次时间差，前者减去后者
                String scheduleTime = item.getSchedule();
                boolean isLate = item.isLate();
                if (isLate) {
                    //迟到时间
                    scheduleTime = item.getLastArriveTime();
                }
                int durationWithNow = TimeTool.getDistanceTimesSign(TimeTool.getCurrentTime("HH:mm"), scheduleTime);
                int time = Constants.TRIP_SELECT_TIME_RIGHT - durationWithNow;
                //迟到,或者要到发车的5-10分钟的状态
                if (time == 0) {
                    time = 1;
                }
                String carState = StateConstants.HAVE_TRIP_FIXED + StateConstants.RUN_NOW + Math.abs(time) + StateConstants.TOTAL_SCOPE_SIGN;
                if (isLate) {
                    //迟到
                    return carState;
                } else {
                    //没有迟到
                    if (durationWithNow < Constants.DEPART_TIME_LEFT) {
                        //小于-2分钟
                        int autoTime = durationWithNow + 2;
                        return StateConstants.HAVE_TRIP_FIXED + Math.abs(autoTime) + StateConstants.TOTAL_TIME_SIGN +
                                ",或者" + Math.abs(time) + StateConstants.TOTAL_SCOPE_SIGN;
                    } else if (durationWithNow > Constants.DEPART_TIME_RIGHT) {
                        //5分钟到10分钟
                        return carState;
                    }
                }
            } else {
                return StateConstants.HAVE_TRIP_NOT_FIXED;
            }
        }
        return StateConstants.GET_STATE_FAIL;
    }


}
