package com.meibanlu.driver.webservice.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name ="line_stationResponse",strict = false)
public class StationResponse {
    @Element(name = "return",required = false)
    public ResponseModel model;
}
