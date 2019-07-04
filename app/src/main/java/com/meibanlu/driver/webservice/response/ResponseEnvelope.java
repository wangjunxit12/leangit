package com.meibanlu.driver.webservice.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

/**
 * 用户角色返回总信息
 * Created by SmileXie on 16/7/15.
 */
@Root(name = "SOAP-ENV:Envelope",strict = false)
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance",prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema",prefix = "xsd"),
        @Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "SOAP-ENV"),
        @Namespace(reference = "http://schemas.xmlsoap.org/soap/encoding/",prefix = "SOAP-ENC")

})
public class ResponseEnvelope {
    @Element(name = "Body",required = false)
    public ResponseBody body;

}
