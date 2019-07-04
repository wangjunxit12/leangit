package com.meibanlu.driver.webservice.requeset;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "urn:sijichufa",strict = false)
public class StartOffRequest {
    @Attribute(name = "soapenv:encodingStyle",required = false)
    public String style="http://schemas.xmlsoap.org/soap/encoding/";
    @Element(name = "sch_id",required = false)
    private String lineNumber;

    @Element(name = "real_depart_time",required = false)
    private String time;

    @Element(name = "real_depart_lnglat",required = false)
    private String lnglat;

    @Element(name = "depart_status",required = false)
    private String status;
    public String getLineNumber() {
        return lineNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLnglat() {
        return lnglat;
    }

    public void setLnglat(String lnglat) {
        this.lnglat = lnglat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }
}
