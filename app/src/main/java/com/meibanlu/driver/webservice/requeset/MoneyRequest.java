package com.meibanlu.driver.webservice.requeset;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 用户钱包请求
 */
@Root(name = "urn:getsijiqianbao",strict = false)
public class MoneyRequest {
    @Attribute(name = "soapenv:encodingStyle",required = false)
    public String style="http://schemas.xmlsoap.org/soap/encoding/";
    @Element(name ="driverid" ,required = false)
    public String userName;

    @Element(name ="wrk_time" ,required = false)
    public String date;
}
