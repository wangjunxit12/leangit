package com.meibanlu.driver.webservice.requeset;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "urn:getchengke",strict = false)
public class PassengersRequest {
    @Attribute(name = "soapenv:encodingStyle",required = false)
    public String style="http://schemas.xmlsoap.org/soap/encoding/";

    @Element(name ="classes" ,required = false)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
