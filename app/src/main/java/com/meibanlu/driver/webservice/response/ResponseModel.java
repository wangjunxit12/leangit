package com.meibanlu.driver.webservice.response;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;


@Root(name = "return",strict = false)
public class ResponseModel {
    @Attribute(name = "xsi:type",required = false)
    public String type="xsd:string";
    @Text
    public String value;
}
