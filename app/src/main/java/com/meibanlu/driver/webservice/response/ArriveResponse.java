package com.meibanlu.driver.webservice.response;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name ="sijishoucheResponse",strict = false)
public class ArriveResponse {
    @Element(name = "return",required = false)
    public ResponseModel model;
}
