package com.meibanlu.driver.webservice.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name ="updatexiaofeimaResponse",strict = false)
public class CodeResponse {
    @Element(name = "return",required = false)
    public ResponseModel model;
}
