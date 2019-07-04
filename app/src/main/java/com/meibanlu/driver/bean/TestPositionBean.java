package com.meibanlu.driver.bean;

/**
 * testPosition
 * Created by lhq on 2017/9/27.
 */

public class TestPositionBean {
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {

        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double longitude;//经度
    private double latitude;//纬度


}
