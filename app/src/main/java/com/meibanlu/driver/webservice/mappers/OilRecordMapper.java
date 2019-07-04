package com.meibanlu.driver.webservice.mappers;

import android.util.Log;

import com.meibanlu.driver.bean.LineStation;
import com.meibanlu.driver.bean.Mileage;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;



public class OilRecordMapper implements Mapper<List<Mileage>,ResponseEnvelope> {
    @Override
    public List<Mileage> transform(ResponseEnvelope responseEnvelope) {
        List<Mileage> list=null;
        if(responseEnvelope.body!=null) {
            String data=responseEnvelope.body.oilRecordResponse.model.value;
            Log.e("StationMapper ","data： "+data);
            Document document = null;
            list=new ArrayList<>();
            try {
                document = DocumentHelper.parseText(data);
                Element root = document.getRootElement();
                List<Element> elements = root.elements();
                if (elements != null && elements.size() > 0) {
                    for (int i = 0; i < elements.size(); i++) {
                        String text = elements.get(i).getText();
                        String[] texts = text.split(",");
                        if(texts.length==0){
                            texts=text.split(",");
                        }
                        Mileage schedule = new Mileage();
                        schedule.setId(texts[1]);
                        schedule.setCarNumber(texts[0]);
                        schedule.setLogTime(texts[6]);
                        if(texts.length>=10){
                            schedule.setMileage(texts[9]);
                        }
                        list.add(schedule);
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
