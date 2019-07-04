package com.meibanlu.driver.tool.web;

import com.meibanlu.driver.webservice.response.ResponseEnvelope;



public  interface DataCallBack {
    void success(ResponseEnvelope result);

    void error(String responseMessage);
}
