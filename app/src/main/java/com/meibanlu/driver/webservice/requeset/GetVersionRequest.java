package com.meibanlu.driver.webservice.requeset;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;


@Root(name = "urn:version_Android_sj",strict = false)
public class GetVersionRequest {
    @Attribute(name = "soapenv:encodingStyle",required = false)
    public String style="http://schemas.xmlsoap.org/soap/encoding/";
}
