package com.meibanlu.driver.bean;

public class Line {
    private String distance;//距离
    private String elapsedTime;//运行时间
    private String signStartTime;//计算时间范围的，出发时间；2017-09-21 10:10

    public String getSignStartTime() {
        return signStartTime;
    }

    public void setSignStartTime(String signStartTime) {
        this.signStartTime = signStartTime;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


}
