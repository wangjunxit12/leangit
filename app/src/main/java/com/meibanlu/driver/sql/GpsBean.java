package com.meibanlu.driver.sql;

/**
 * 实时司机位置的记录
 *
 * @author lhq
 * @date 2017/11/15
 */
public class GpsBean {
    private Integer id;
    private String gps;
    private Long time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
