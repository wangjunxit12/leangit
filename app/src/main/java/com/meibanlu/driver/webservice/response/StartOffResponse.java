package com.meibanlu.driver.webservice.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name ="sijichufaResponse",strict = false)
public class StartOffResponse {
    @Element(name = "return",required = false)
    public ResponseModel model;
}
