package com.meibanlu.driver.webservice.mappers;

import android.util.Log;


import com.meibanlu.driver.bean.Passenger;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;


public class PhoneMapper implements Mapper<List<Passenger>,ResponseEnvelope> {
    @Override
    public List<Passenger> transform(ResponseEnvelope responseEnvelope) {
        List<Passenger> list=null;
        if(responseEnvelope.body!=null){
            String data=responseEnvelope.body.passengersResponse.model.value;
            Log.e("StationMapper ","data： "+data);
            Document document = null;
            list=new ArrayList<>();
            try {
                document = DocumentHelper.parseText(data);
                Element root = document.getRootElement();
                List<Element> elements = root.elements();
                if (elements != null && elements.size() > 0) {
                    for (int i = 0; i < elements.size(); i++) {
                        String texts[]=elements.get(i).getText().split(",");
                        if(texts.length>=2){
                            Passenger passenger=new Passenger();
                            passenger.setPhone(texts[0]);
                            passenger.setState(texts[1]);
                            list.add(passenger);
                        }
                    }
                }
            } catch (DocumentException e) {
                e.printStackTrace();
                Log.e("DocumentException", e.getMessage());
            }
        }
        return list;
    }
}
