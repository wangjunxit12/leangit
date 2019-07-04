package com.meibanlu.driver.webservice.mappers;

import android.util.Log;

import com.meibanlu.driver.bean.UserBean;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import java.io.IOException;

import retrofit2.adapter.rxjava2.Result;


public class LoginMapper implements Mapper<UserBean,ResponseEnvelope> {
    @Override
    public UserBean transform(ResponseEnvelope responseEnvelopeResult) {
        UserBean bean = null;
        if(responseEnvelopeResult.body!=null){
            bean=new UserBean();
            String data=responseEnvelopeResult.body.driverResponse.model.value;
            String [] texts=data.split(",");
            if(texts.length>1){
                Log.i("LoginMapper",texts[0]+texts[1]);
                bean.setSex(texts[0]);
                bean.setId(texts[1]);
            }

        }
        return bean;
    }
}
