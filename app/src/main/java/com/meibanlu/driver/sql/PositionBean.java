package com.meibanlu.driver.sql;

/**
 * 数据库position位置的表格
 * Created by lhq on 2017/11/15.
 */

public class PositionBean {
    private int id;//ID
    private int tripId;//班次id
    private double latitude; //纬度
    private double longitude;//经度
    private float speed;// 速度
    private float bearing; //方位角
    private long time; //时间

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "tripId" + tripId + "   latlng" + latitude;
    }
}
