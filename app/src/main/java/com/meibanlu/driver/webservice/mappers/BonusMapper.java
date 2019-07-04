package com.meibanlu.driver.webservice.mappers;

import android.util.Log;

import com.meibanlu.driver.bean.Bonus;
import com.meibanlu.driver.bean.LineStation;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;



public class BonusMapper implements Mapper<List<Bonus>,ResponseEnvelope> {
    @Override
    public List<Bonus> transform(ResponseEnvelope responseEnvelope) {
        List<Bonus> list=null;
        if(responseEnvelope.body!=null) {
            String data=responseEnvelope.body.moneyResponse.model.value;
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
                        Bonus schedule = new Bonus();
                        schedule.setCreateDate(texts[1]);
                        schedule.setDepart(texts[5]);
                        schedule.setArrive(texts[6]);
                        schedule.setAmount(Double.valueOf(texts[7]));
                        schedule.setType(texts[8]);
                        if(texts.length>=9){
                            schedule.setTodayAmount(texts[9]);
                            schedule.setMonthAmount(texts[10]);
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
