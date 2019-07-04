package com.meibanlu.driver.bean;

public class LineStation {

    private Integer id;

    private String name;

    private String shortName;

    private Float longitude;

    private Float latitude;

    private Float areaRadius; //半径

    private String keywords;

    private String type;

    private String comment; //注释

    private String lngLat;


    private String distance;//距离
    private String elapsedTime;//运行时间


    public LineStation() {
    }

    /**
     * 通过redis缓存的字符串初始化
     *
     * @param redisCacheString 换成字符串   站点名称|经纬度|半径
     */
    public LineStation(Integer id, String redisCacheString) {
        this.id = id;
        String[] params;
        if (redisCacheString != null && (params = redisCacheString.split("\\|")).length == 3) {
            this.name = params[0];
            this.lngLat = params[1];
            this.areaRadius = Float.valueOf(params[2]);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getAreaRadius() {
        return areaRadius;
    }

    public void setAreaRadius(Float areaRadius) {
        this.areaRadius = areaRadius;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLngLat() {
        return lngLat;
    }

    public void setLngLat(String lngLat) {
        this.lngLat = lngLat;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}