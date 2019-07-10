package com.meibanlu.driver.webservice.requeset;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "soapenv:Body",strict = false)
public class RequestBody {

    @Element(name = "urn:ck_user",required = false)
    public LoginRequest loginRequest;

    @Element(name = "urn:car_sch ",required = false)
    public LineRequest lineRequest;

    @Element(name = "urn:line_station ",required = false)
    public StationRequest siteRequest;
    @Element(name = "urn:weizhiinsert ",required = false)
    public UpLoadLocationRequest locationRequest;
    @Element(name = "urn:sijichufa",required = false)
    public StartOffRequest offRequest;
    @Element(name = "urn:sijishouche",required = false)
    public ArriveRequest arriveRequest;

    @Element(name = "urn:getchezhanrenshu",required = false)
    public NumberOfPeopleRequest peopleRequest;

    @Element(name = "urn:chengshi",required = false)
    public CityNumberRequest numberRequest;

    @Element(name = "urn:getuid",required = false)
    public CheckUserRequest userRequest;

    @Element(name = "urn:sj_userinsert",required = false)
    public RegisteredRequest registeredRequest;

    @Element(name = "urn:version_Android_sj",required = false)
    public GetVersionRequest versionRequest;
    @Element(name = "urn:getsijiqianbao",required = false)
    public MoneyRequest moneyRequest;

    @Element(name = "urn:ck_updatepsd",required = false)
    public ModifyPasswordRequest passwordRequest;

    @Element(name = "urn:car_sch",required = false)
    public LinesRequest linesRequest;

    @Element(name = "urn:sijitousu",required = false)
    public GetUserComplaintRequest complaintRequest;

    @Element(name = "urn:insert_oil_record",required = false)
    public UpLoadOilRequest oilRequest;

    @Element(name = "urn:oil_record",required = false)
    public GetOilRecordRequest getOilRecordRequest;
    @Element(name = "urn:getchengke",required = false)
    public PassengersRequest passengersRequest;

    @Element(name = "urn:huanxian",required = false)
    public CircleLineRequest circleLineRequest;

    @Element(name = "urn:quxiaoupdatexiaofeima ",required = false)
    public CancelCodeRequest cancelCodeRequest;
    @Element(name = "urn:updatexiaofeima ",required = false)
    public UpdateCodeRequest codeRequest;
    @Element(name = "urn:xiaofeimazhuangtai",required = false)
    public GetCodeStateRequest codeStateRequest;


}
