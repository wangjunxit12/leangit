package com.meibanlu.driver.webservice.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name ="ck_userResponse",strict = false)
public class DriverResponse {
    @Element(name = "return",required = false)
    public ResponseModel model;
}
