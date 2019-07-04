package com.meibanlu.driver.webservice.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name ="weizhiinsertResponse",strict = false)
public class LocationResponse {
    @Element(name = "return",required = false)
    public ResponseModel model;
}
