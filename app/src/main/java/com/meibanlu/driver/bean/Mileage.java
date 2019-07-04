package com.meibanlu.driver.bean;

/**
 * Mileage 油耗里程
 * Created by lhq on 2018-01-08.
 */

public class Mileage {
    private String id;
    private String driverId;
    private String logTime;
    private String mileage;
    private String carNumber;

    private String oil_l;


    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

}
