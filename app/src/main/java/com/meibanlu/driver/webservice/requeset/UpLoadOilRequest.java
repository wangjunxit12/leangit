package com.meibanlu.driver.webservice.requeset;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name = "urn:insert_oil_record",strict = false)
public class UpLoadOilRequest {
    @Attribute(name = "soapenv:encodingStyle",required = false)
    public String style="http://schemas.xmlsoap.org/soap/encoding/";

    @Element(name = "plate",required = false)
    private String carNumber;

    @Element(name = "driver_id",required = false)
    private String id;

    @Element(name = "oil_l",required = false)
    private String oil_l;

    @Element(name = "oil_fee",required = false)
    private String oil_fee;

    @Element(name = "oil_time",required = false)
    private String oil_time;

    @Element(name = "add_time",required = false)
    private String add_time;

    @Element(name = "handdle",required = false)
    private String handle;

    @Element(name = "remark",required = false)
    private String remark;

    @Element(name = "kmnumber",required = false)
    private String km;

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOil_l() {
        return oil_l;
    }

    public void setOil_l(String oil_l) {
        this.oil_l = oil_l;
    }

    public String getOil_fee() {
        return oil_fee;
    }

    public void setOil_fee(String oil_fee) {
        this.oil_fee = oil_fee;
    }

    public String getOil_time() {
        return oil_time;
    }

    public void setOil_time(String oil_time) {
        this.oil_time = oil_time;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
