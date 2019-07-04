package com.meibanlu.driver.bean;

import com.meibanlu.driver.tool.HolderTrip;

/**
 * 保存未上传成功的Tirp
 *
 * @author Administrator
 */
public class SavedTrip {

    private String tripId;

    private Integer status;

    private Integer signArriveId;

    private String lngLat;

    private Integer mode;

    private String signStationName;

    private HolderTrip holderTrip;

    public String getSignStationName() {
        return signStationName;
    }

    public void setSignStationName(String signStationName) {
        this.signStationName = signStationName;
    }

    public HolderTrip getHolderTrip() {
        return holderTrip;
    }

    public void setHolderTrip(HolderTrip holderTrip) {
        this.holderTrip = holderTrip;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSignArriveId() {
        return signArriveId;
    }

    public void setSignArriveId(Integer signArriveId) {
        this.signArriveId = signArriveId;
    }

    public String getLngLat() {
        return lngLat;
    }

    public void setLngLat(String lngLat) {
        this.lngLat = lngLat;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }
}
