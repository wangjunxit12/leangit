package com.meibanlu.driver.webservice;

import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import io.reactivex.Observable;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiStore {

    @Headers({"Content-Type: text/xml; charset=utf-8","SOAPAction:urn:TYWJAPPIntf-ITYWJAPP#ck_user"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> login(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","SOAPAction:urn:TYWJAPPIntf-ITYWJAPP#car_sch"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> getSchedules(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","SOAPAction:urn:TYWJAPPIntf-ITYWJAPP#line_station"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> getLineStation(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","SOAPAction:urn:TYWJAPPIntf-ITYWJAPP#weizhiinsert"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> uploadLocation(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","SOAPAction:urn:TYWJAPPIntf-ITYWJAPP#sijichufa"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> startOff(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","SOAPAction:urn:TYWJAPPIntf-ITYWJAPP#sijishouche"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> arrive(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","urn:TYWJAPPIntf-ITYWJAPP#getchezhanrenshu"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> getNumberOfPeople(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","urn:TYWJAPPIntf-ITYWJAPP#chengshi"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> getNumberOfCity(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","urn:TYWJAPPIntf-ITYWJAPP#getuid"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> checkUser(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","urn:TYWJAPPIntf-ITYWJAPP#sj_userinsert"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> registered(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","urn:TYWJAPPIntf-ITYWJAPP#version_Android_sj"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> checkVersion(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","urn:TYWJAPPIntf-ITYWJAPP#getsijiqianbao"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> getIncome(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","urn:TYWJAPPIntf-ITYWJAPP#sj_updatepsd"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> modifyPassword(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","urn:TYWJAPPIntf-ITYWJAPP#sijipingjia"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> getUserEvaluation(@Body RequestEnvelope requestEnvelope);

    @Headers({"Content-Type: text/xml; charset=utf-8","urn:TYWJAPPIntf-ITYWJAPP#sijitousu"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> getUserComplaint(@Body RequestEnvelope requestEnvelope);

    //    @Headers({"Content-Type:application/json;charset=utf-8", "Accept:application/json;"})
//    @POST("SendSms")
//    Observable<CodeResponse> getCode(@Body Code code);
//
//    @Headers({"Content-Type:application/json"})
//    @POST("getLine")
//    Observable<DriverLine> getDriverLine(@Body User user);

    @Headers({"Content-Type: text/xml; charset=utf-8"})
    @POST("ITYWJAPP")
    Observable<ResponseEnvelope> getData(@Body RequestEnvelope requestEnvelope,@Header("SOAPAction") String soap);

}
