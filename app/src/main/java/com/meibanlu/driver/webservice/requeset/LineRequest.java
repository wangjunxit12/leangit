package com.meibanlu.driver.webservice.requeset;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * 线路请求
 */
@Root(name = "urn:car_sch",strict = false)
public class LineRequest {
    @Attribute(name = "soapenv:encodingStyle",required = false)
    public String style="http://schemas.xmlsoap.org/soap/encoding/";
    @Element(name ="csbh" ,required = false)
    public String cityNumber;
    @Element(name ="leibie" ,required = false)
    public String type;
    @Element(name ="sjsjh" ,required = false)
    public String phoneNumber;

}
