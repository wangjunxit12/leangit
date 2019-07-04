package com.meibanlu.driver.bean;

/**
 * UserBean 登录用户信心
 * Created by lhq on 2017/9/22.
 */


public class UserBean {

    private String id;

    private String name;

    private String pinYin;

    private String driverType;

    private String sex;

    private String idCard;

    private Integer driveYear;

    private String tel;

    private String photo;

    private Double salaryLevel;

    private String password;

    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String pinYin) {
        this.pinYin = pinYin;
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }


    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getDriveYear() {
        return driveYear;
    }

    public void setDriveYear(Integer driveYear) {
        this.driveYear = driveYear;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Double getSalaryLevel() {
        return salaryLevel;
    }

    public void setSalaryLevel(Double salaryLevel) {
        this.salaryLevel = salaryLevel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
