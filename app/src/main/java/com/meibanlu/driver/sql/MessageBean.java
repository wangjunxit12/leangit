package com.meibanlu.driver.sql;

/**
 * MessageBean 工具类
 * Created by lhq on 2017/8/29.
 */

public class MessageBean {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    private int id;// id
    private int isRead;//是否阅读 0未读，1已读1
    private String receiveTime;//接收时间
    private String msgId;//信息的id
    private String information;//接收的信息


}
