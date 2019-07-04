package com.meibanlu.driver.webservice.response;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 线路请求的返回字段
 */
@Root(name ="xianlubiaoResponse",strict = false)
public class LinesResponse {
    @Element(name = "return",required = false)
    public ResponseModel model;
}
