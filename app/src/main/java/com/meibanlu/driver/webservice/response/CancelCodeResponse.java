package com.meibanlu.driver.webservice.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name ="quxiaoupdatexiaofeimaResponse",strict = false)
public class CancelCodeResponse {
    @Element(name = "return",required = false)
    public ResponseModel model;

}
