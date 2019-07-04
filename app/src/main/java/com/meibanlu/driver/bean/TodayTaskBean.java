package com.meibanlu.driver.bean;

import java.util.List;

/**
 * TodayTaskBean 今天任务
 * Created by lhq on 2017/9/26.
 */

public class TodayTaskBean {
    private List<TaskDetail> trips;

    public List<TaskDetail> getTrips() {
        return trips;
    }

    public void setTrips(List<TaskDetail> trips) {
        this.trips = trips;
    }

}
