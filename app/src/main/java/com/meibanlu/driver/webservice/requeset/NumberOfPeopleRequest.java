package com.meibanlu.driver.webservice.requeset;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name ="urn:getchezhanrenshu" ,strict = false)
public class NumberOfPeopleRequest {

    @Attribute(name = "soapenv:encodingStyle",required = false)
    public String style="http://schemas.xmlsoap.org/soap/encoding/";
    @Element(name ="rq" ,required = false)
    private String date;
    @Element(name ="xl" ,required = false)
    private String line;
    @Element(name ="zm" ,required = false)
    private String siteName;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
