package com.meibanlu.driver.webservice.response;

import org.simpleframework.xml.Element;


public class BaseResponse {
    @Element(name = "return",required = false)
    public ResponseModel model;
}
