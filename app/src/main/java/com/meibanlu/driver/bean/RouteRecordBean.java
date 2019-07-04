//package com.meibanlu.driver.bean;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
///**
// * RouteRecordBean //轨迹
// * Created by lhq on 2017/9/29.
// */
//
//public class RouteRecordBean implements Parcelable {
//
//    private String id;
//    private String arriveId;
//    private String carNumber;
//    private String schedule;
//    private String tripLineFile;
//    private String departTime;
//    private String arriveTime;
//    private int distance;
//    private Integer shiftingTimes;
//    private LineBean line;
//    private LineStation departStation;
//    private LineStation arriveStation;
//
//
//    public static final Creator<RouteRecordBean> CREATOR = new Creator<RouteRecordBean>() {
//        @Override
//        public RouteRecordBean createFromParcel(Parcel in) {
//            return new RouteRecordBean(in);
//        }
//
//        @Override
//        public RouteRecordBean[] newArray(int size) {
//            return new RouteRecordBean[size];
//        }
//    };
//
//    public int getDistance() {
//        return distance;
//    }
//
//    public void setDistance(int distance) {
//        this.distance = distance;
//    }
//
//    protected RouteRecordBean(Parcel in) {
//        id = in.readString();
//        arriveId = in.readString();
//        carNumber = in.readString();
//        schedule = in.readString();
//        tripLineFile = in.readString();
//        departTime = in.readString();
//        arriveTime = in.readString();
//    }
//
//
//    public LineBean getLine() {
//        return line;
//    }
//
//    public void setLine(LineBean line) {
//        this.line = line;
//    }
//
//    public String getDepartTime() {
//        return departTime;
//    }
//
//    public void setDepartTime(String departTime) {
//        this.departTime = departTime;
//    }
//
//    public String getArriveTime() {
//        return arriveTime;
//    }
//
//    public void setArriveTime(String arriveTime) {
//        this.arriveTime = arriveTime;
//    }
//
//    public String getTripLineFile() {
//        return tripLineFile;
//    }
//
//    public void setTripLineFile(String tripLineFile) {
//        this.tripLineFile = tripLineFile;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getArriveId() {
//        return arriveId;
//    }
//
//    public Integer getShiftingTimes() {
//        return shiftingTimes;
//    }
//
//    public void setShiftingTimes(Integer shiftingTimes) {
//        this.shiftingTimes = shiftingTimes;
//    }
//
//    public void setArriveId(String arriveId) {
//        this.arriveId = arriveId;
//    }
//
//    public String getCarNumber() {
//        return carNumber;
//    }
//
//    public void setCarNumber(String carNumber) {
//        this.carNumber = carNumber;
//    }
//
//    public String getSchedule() {
//        return schedule;
//    }
//
//    public void setSchedule(String schedule) {
//        this.schedule = schedule;
//    }
//
//    public LineStation getDepartStation() {
//        return departStation;
//    }
//
//    public void setDepartStation(LineStation departStation) {
//        this.departStation = departStation;
//    }
//
//    public LineStation getArriveStation() {
//        return arriveStation;
//    }
//
//    public void setArriveStation(LineStation arriveStation) {
//        this.arriveStation = arriveStation;
//    }
//
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(id);
//        parcel.writeString(arriveId);
//        parcel.writeString(carNumber);
//        parcel.writeString(schedule);
//        parcel.writeString(tripLineFile);
//        parcel.writeString(departTime);
//        parcel.writeString(arriveTime);
//    }
//}
