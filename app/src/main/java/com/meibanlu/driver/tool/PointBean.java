package com.meibanlu.driver.tool;

import com.amap.api.maps.model.LatLng;

/**
 * 点，由经度纬度构成
 * Created by leigang on 16/9/5.
 */
public class PointBean {

    private Double x; //经度
    private Double y; //纬度
    public PointBean() {
    }
    public PointBean(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 通过经纬度对象构建Point
     * @param polygon 类似 120.23,123.123
     */
    public PointBean(String polygon) {
        if (polygon != null) {
            setX(Double.valueOf(polygon.split(",")[0]));
            setY(Double.valueOf(polygon.split(",")[1]));
        }
    }
    public PointBean(LatLng latLng) {
        if (latLng != null) {
            setX(latLng.longitude);
            setY(latLng.latitude);
        }
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    /**
     * 判断是否参数异常
     * @return 是否参数异常
     */
    public boolean isNull(){
        return getX() == null || getY() == null;
    }
}
