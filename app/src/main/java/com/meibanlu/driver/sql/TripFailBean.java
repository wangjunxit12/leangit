package com.meibanlu.driver.sql;

/**
 * 打卡失败
 *
 * @author lhq
 * @date 2017/11/15
 */
public class TripFailBean {

    private Integer id;

    private Integer tripId;

    private Integer status;

    private Integer mode;

    private String lngLat;

    private Integer signArriveId;

    private Long time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getLngLat() {
        return lngLat;
    }

    public void setLngLat(String lngLat) {
        this.lngLat = lngLat;
    }

    public Integer getSignArriveId() {
        return signArriveId;
    }

    public void setSignArriveId(Integer signArriveId) {
        this.signArriveId = signArriveId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
