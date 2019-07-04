package com.meibanlu.driver.bean;

/**
 * 绩效bean
 * Created by lhq on 2017-11-23.
 */

public class Bonus {
    private long createTime; //创建时间
    private String depart;//离开站点
    private String arrive; //到达站点
    private double amount; //奖金

    private String createDate; //创建时间

    private String type;

    private String todayAmount;

    private String monthAmount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getArrive() {
        return arrive;
    }

    public void setArrive(String arrive) {
        this.arrive = arrive;
    }


    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTodayAmount() {
        return todayAmount;
    }

    public void setTodayAmount(String todayAmount) {
        this.todayAmount = todayAmount;
    }

    public String getMonthAmount() {
        return monthAmount;
    }

    public void setMonthAmount(String monthAmount) {
        this.monthAmount = monthAmount;
    }
}
