package com.meibanlu.driver.webservice.requeset;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "urn:chengshi",strict = false)
public class CityNumberRequest {
    @Attribute(name = "soapenv:encodingStyle",required = false)
    public String style="http://schemas.xmlsoap.org/soap/encoding/";
}
