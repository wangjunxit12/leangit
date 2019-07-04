package com.meibanlu.driver.bean;

import com.amap.api.location.AMapLocation;
import com.meibanlu.driver.tool.AMapDistanceUtil;
import com.meibanlu.driver.tool.T;

/**
 * 位置信息封装类
 */
public class PositionVo {

    private long enterTime;

    private double longitude;

    private double latitude;

    private float bearing;

    /**
     * 是否标记异常
     */
    private boolean abnormal;

    public double calSpeed(PositionVo vo) {
        //距离/时间
        T.log(AMapDistanceUtil.calculateLineDistance(vo.longitude, vo.latitude, this.longitude, this.latitude) + "," + Math.abs(vo.enterTime / 1000 - this.enterTime / 1000) + ";");
        if (vo.enterTime - this.enterTime == 0) {
            return 0;
        }
        return AMapDistanceUtil.calculateLineDistance(vo.longitude, vo.latitude, this.longitude, this.latitude)
                / Math.abs(vo.enterTime / 1000 - this.enterTime / 1000);
    }

    public PositionVo() {
    }

    public PositionVo(AMapLocation aMapLocation) {
        this.latitude = aMapLocation.getLatitude();
        this.longitude = aMapLocation.getLongitude();
        this.enterTime = System.currentTimeMillis();
    }

    public long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean isAbnormal() {
        return abnormal;
    }

    public void setAbnormal(boolean abnormal) {
        this.abnormal = abnormal;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }
}