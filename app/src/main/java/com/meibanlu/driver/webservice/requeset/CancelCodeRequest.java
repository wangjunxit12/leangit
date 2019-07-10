package com.meibanlu.driver.webservice.requeset;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name = "urn:quxiaoupdatexiaofeima",strict = false)
public class CancelCodeRequest {
    @Attribute(name = "soapenv:encodingStyle",required = false)
    public String style="http://schemas.xmlsoap.org/soap/encoding/";

    @Element(name = "tel",required = false)
    private String phone;

    @Element(name = "ysch_id",required = false)
    private String schId;

    @Element(name = "xiaofeima",required = false)
    private String code;

    public String getSchId() {
        return schId;
    }

    public void setSchId(String schId) {
        this.schId = schId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
