package com.meibanlu.driver.webservice.response;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

/* 请求发回中的BODY字段
* */
@Root(name = "Body",strict = false)
@Namespace(reference = "urn:TYWJAPPIntf-ITYWJAPP",prefix = "NS1")
public class ResponseBody {
    @Attribute(name = "SOAP-ENV:encodingStyle",required = false)
    public String style="http://schemas.xmlsoap.org/soap/encoding/";
    @Element(name ="ck_userResponse",required = false)
    public DriverResponse driverResponse;
    @Element(name ="car_schResponse",required = false)
    public LineResponse lineResponse;
    @Element(name ="line_stationResponse",required = false)
    public StationResponse siteResponse;
    @Element(name ="weizhiinsertResponse",required = false)
    public LocationResponse locationResponse;
    @Element(name ="sijichufaResponse",required = false)
    public StartOffResponse offResponse;
    @Element(name ="sijishoucheResponse",required = false)
    public ArriveResponse arriveResponse;

    @Element(name ="getchezhanrenshuResponse",required = false)
    public NumberOfPeopleResponse peopleResponse;

    @Element(name ="chengshiResponse",required = false)
    public CityNumberResponse numberResponse;

    @Element(name ="getuidResponse",required = false)
    public CheckUserResponse userResponse;
    @Element(name ="sj_userinsertResponse",required = false)
    public RegisteredResponse registeredResponse;

    @Element(name ="version_Android_sjResponse",required = false)
    public GetVersionResponse versionResponse;
    @Element(name ="getsijiqianbaoResponse" ,required = false)
    public MoneyResponse moneyResponse;

    @Element(name ="ck_updatepsdResponse" ,required = false)
    public ModifyPasswordResponse passwordResponse;

    @Element(name ="xianlubiaoResponse",required = false)
    public LinesResponse linesResponse;

    @Element(name ="sijitousuResponse",required = false)
    public UserComplaintResponse complaintResponse;

    @Element(name ="insert_oil_recordResponse",required = false)
    public OilResponse oilResponse;

    @Element(name ="oil_recordResponse",required = false)
    public OilRecordResponse oilRecordResponse;


    @Element(name ="getchengkeResponse",required = false)
    public PassengersResponse passengersResponse;

}
