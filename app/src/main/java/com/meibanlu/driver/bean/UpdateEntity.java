package com.meibanlu.driver.bean;



public class UpdateEntity {
    private int versionCode;
    private String type;

    public UpdateEntity(int versionCode, String type) {
        this.versionCode = versionCode;
        this.type = type;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
