package com.meibanlu.driver.tool;

import com.amap.api.maps.model.LatLng;
import com.meibanlu.driver.bean.TaskDetail;

/**
 * HolderTrip 出发打卡的当前班次
 *
 * @author lhq
 * @date 2017/10/27
 */

public class HolderTrip {

    /** 持有任务对象 */
    private TaskDetail trip;

    private boolean endSign;

    private boolean startIn;

    private boolean startOut;

    /** 是否是手动设置 */
    private boolean setManual;

    /** 出发打卡经纬度 */
    private String startSignPosition;

    /** 到达打卡经纬度 */
    private String endSignPosition;

    public TaskDetail getTrip() {
        return trip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HolderTrip that = (HolderTrip) o;

        return trip.equals(that.trip);
    }

    @Override
    public int hashCode() {
        return trip.hashCode();
    }

    public void setTrip(TaskDetail trip) {
        if (trip.getStatus() == Constants.STATUS_DEPART) {
            this.startIn = true;
            this.startOut = true;
        }
        this.trip = trip;
    }

    public boolean isEndSign() {
        return endSign;
    }

    public void setEndSign(boolean endSign) {
        this.endSign = endSign;
    }

    public boolean isStartIn() {
        return startIn;
    }

    public void setStartIn(boolean startIn) {
        this.startIn = startIn;
    }

    public boolean isStartOut() {
        return startOut;
    }

    public void setStartOut(boolean startOut) {
        this.startOut = startOut;
    }

    public boolean isSetManual() {
        return setManual;
    }

    public void setSetManual(boolean setManual) {
        this.setManual = setManual;
    }

    public String getStartSignPosition() {
        return startSignPosition;
    }

    public void setStartSignPosition(String startSignPosition) {
        this.startSignPosition = startSignPosition;
    }

    public String getEndSignPosition() {
        return endSignPosition;
    }

    public void setEndSignPosition(String endSignPosition) {
        this.endSignPosition = endSignPosition;
    }
}
