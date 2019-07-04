package com.meibanlu.driver.bean;

import java.io.Serializable;


public class Passenger implements Serializable{
    private String phone;
    private String state;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
