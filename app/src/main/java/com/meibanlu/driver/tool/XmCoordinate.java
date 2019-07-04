package com.meibanlu.driver.tool;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;

/**
 * 坐标对象，用于处理经纬度坐标的问题
 * Created by leigang on 2016/11/16.
 */

public class XmCoordinate {

    //异常打印标记
    private final String ERROR_LOG = "XmError";

    /**
     * 属性列表
     */
    private double longitude; //经度

    private double latitude; //纬度

    /**
     * 空的构造函数
     */
    public XmCoordinate() {

    }

    /**
     * 使用AMapLocation初始化
     */
    public XmCoordinate(AMapLocation mapLocation) {
        this.longitude = mapLocation.getLongitude();
        this.latitude = mapLocation.getLatitude();
    }

    public XmCoordinate(LatLng latLng) {
        this.longitude = latLng.longitude;
        this.latitude = latLng.latitude;
    }

    /**
     * 使用字符串初始化
     * @param lngLat
     */
    public XmCoordinate(String lngLat) {
        if (lngLat != null) {
            try {
                String[] lngLatArr = lngLat.split(",");
                this.longitude = Double.valueOf(lngLatArr[0]);
                this.latitude = Double.valueOf(lngLatArr[1]);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                //出现异常，取消赋值
                this.latitude = 0;
                this.longitude = 0;
                Log.e(ERROR_LOG, e.toString());
            }
        }
    }

    /**
     * 使用double初始化
     * @param longitude
     * @param latitude
     */
    public XmCoordinate(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * 装换成LatLng对象
     * @return LatLng对象
     */
    public LatLng toLatlng() {
        return new LatLng(this.latitude, this.longitude);
    }

    /**
     * 判断是否是空的
     * @return true表示不为空，可以正常使用
     */
    public boolean notEmpty() {
        return this.latitude != 0;
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
}
