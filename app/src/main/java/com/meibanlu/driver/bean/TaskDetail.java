package com.meibanlu.driver.bean;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.meibanlu.driver.tool.Constants;
import com.meibanlu.driver.tool.HolderTrip;
import com.meibanlu.driver.tool.T;

import java.util.List;

public class TaskDetail implements Comparable<TaskDetail> {
    private String id;

    private Line line;

    private Integer arriveId;

    private Integer departId;

    private Integer busId;//线路id

    private Integer seats;

    private String carNumber;

    private String categoryName;

    private String date;

    private int distance;

    private String schedule;

    private Integer driverId;

    private String driverName;

    private String driverTel;

    /**
     * 是否是环线
     */

    private boolean isCircle;


    /**
     * 上一班是否迟到
     */
    private boolean isLate;

    /**
     * 上一班迟到到站打卡时间
     * 09:20
     */
    private String lastArriveTime;

    private String instructorName;//讲解员姓名

    private String tripLineFile; //路线的详细gps文件路径

    private Long departTime; //实际出发时间

    private Long arriveTime; // 实际到达时间

    private Integer status;  //状态包括未打卡，出发打卡，到达打卡

    private Integer shiftingTimes; //偏移次数

    private Integer occupiedSeats; //座位数

    private LineStation departStation;

    private LineStation arriveStation;

    /**
     * 是否是滚动发车
     */
    private boolean roll;

    private String backupArriveIds;

    private String total;

    private String remaining;

    private List<LineStation> backupStations;

    public HolderTrip toHolderTrip() {
        return toHolderTrip(false);
    }

    public HolderTrip toHolderTrip(boolean setManual) {
        HolderTrip holderTrip = new HolderTrip();
        holderTrip.setTrip(this);
        holderTrip.setSetManual(setManual);
        if(this.getStatus()== Constants.STATUS_DEPART){
            holderTrip.setStartOut(true);
        }
        return holderTrip;
    }

    public TaskDetail() {

    }

    public TaskDetail(Parcel in) {
        id = in.readString();
        carNumber = in.readString();
        categoryName = in.readString();
        date = in.readString();
        distance = in.readInt();
        schedule = in.readString();
        driverName = in.readString();
        driverTel = in.readString();
        tripLineFile = in.readString();
        departTime = in.readLong();
        arriveTime = in.readLong();
    }


    public boolean isCircle() {
        return isCircle;
    }

    public void setCircle(boolean circle) {
        isCircle = circle;
    }

    public Integer getOccupiedSeats() {
        return occupiedSeats;
    }

    public void setOccupiedSeats(Integer occupiedSeats) {
        this.occupiedSeats = occupiedSeats;
    }

    public Integer getShiftingTimes() {
        return shiftingTimes;
    }

    public void setShiftingTimes(Integer shiftingTimes) {
        this.shiftingTimes = shiftingTimes;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Long getDepartTime() {
        return departTime;
    }

    public void setDepartTime(Long departTime) {
        this.departTime = departTime;
    }

    public Long getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(Long arriveTime) {
        this.arriveTime = arriveTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getArriveId() {
        return arriveId;
    }

    public void setArriveId(Integer arriveId) {
        this.arriveId = arriveId;
    }

    public Integer getDepartId() {
        return departId;
    }

    public void setDepartId(Integer departId) {
        this.departId = departId;
    }

    public Integer getBusId() {
        return busId;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverTel() {
        return driverTel;
    }

    public void setDriverTel(String driverTel) {
        this.driverTel = driverTel;
    }


    public String getTripLineFile() {
        return tripLineFile;
    }

    public void setTripLineFile(String tripLineFile) {
        this.tripLineFile = tripLineFile;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LineStation getDepartStation() {
        return departStation;
    }

    public void setDepartStation(LineStation departStation) {
        this.departStation = departStation;
    }

    public LineStation getArriveStation() {
        return arriveStation;
    }

    public void setArriveStation(LineStation arriveStation) {
        this.arriveStation = arriveStation;
    }

    public boolean isRoll() {
        return roll;
    }

    public void setRoll(boolean roll) {
        this.roll = roll;
    }

    public String getBackupArriveIds() {
        return backupArriveIds;
    }

    public void setBackupArriveIds(String backupArriveIds) {
        this.backupArriveIds = backupArriveIds;
    }

    public List<LineStation> getBackupStations() {
        return backupStations;
    }

    public void setBackupStations(List<LineStation> backupStations) {
        this.backupStations = backupStations;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public boolean isLate() {
        return isLate;
    }

    public void setLate(boolean late) {
        isLate = late;
    }

    public String getLastArriveTime() {
        return lastArriveTime;
    }

    public void setLastArriveTime(String lastArriveTime) {
        this.lastArriveTime = lastArriveTime;
    }

    @Override
    public int compareTo(@NonNull TaskDetail oldTaskDetail) {
        T.log("compareTo:   "+getSchedule().compareTo(oldTaskDetail.getSchedule()));
        return getSchedule().compareTo(oldTaskDetail.getSchedule());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskDetail that = (TaskDetail) o;

        return id.equals(that.id);
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getRemaining() {
        return remaining;
    }

    public void setRemaining(String remaining) {
        this.remaining = remaining;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}