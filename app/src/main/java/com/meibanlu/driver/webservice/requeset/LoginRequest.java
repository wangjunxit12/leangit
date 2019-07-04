package com.meibanlu.driver.webservice.requeset;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 登录请求实体
 */
@Root(name = "urn:ck_user",strict = false)
public class LoginRequest {
    @Attribute(name = "soapenv:encodingStyle",required = false)
    public String style="http://schemas.xmlsoap.org/soap/encoding/";
    @Element(name = "yhm",required = false)
    private String phone;
    @Element(name = "yhmm",required = false)
    private String password;
    @Element(name = "uid",required = false)
    private String uid;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
