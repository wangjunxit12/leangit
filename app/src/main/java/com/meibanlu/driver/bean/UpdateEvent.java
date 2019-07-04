package com.meibanlu.driver.bean;



public class UpdateEvent {
    private boolean show;

    public UpdateEvent(boolean show) {
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}
