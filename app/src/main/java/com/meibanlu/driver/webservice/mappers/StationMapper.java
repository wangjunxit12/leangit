package com.meibanlu.driver.webservice.mappers;

import android.util.Log;

import com.meibanlu.driver.bean.LineStation;
import com.meibanlu.driver.bean.TaskDetail;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;


public class StationMapper implements Mapper<List<LineStation>,ResponseEnvelope> {
    @Override
    public  List<LineStation> transform(ResponseEnvelope responseEnvelope) {
        List<LineStation> list=null;
        if(responseEnvelope.body!=null) {
             String data=responseEnvelope.body.siteResponse.model.value;
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
                        LineStation schedule = new LineStation();
                        schedule.setId(Integer.valueOf(texts[2]));
                        schedule.setName(texts[6]);
                        schedule.setShortName(texts[7]);
                        if(texts.length>9){
                            schedule.setElapsedTime(texts[3]);
                            schedule.setDistance(texts[4]);
                            schedule.setAreaRadius(Float.valueOf(texts[10]));
                            if(texts[9].contains(";")){
                                String [] textss=texts[9].split(";");
                                schedule.setLongitude(Float.valueOf(textss[0]));
                                schedule.setLatitude(Float.valueOf(textss[1]));
                            }else {
                                schedule.setLongitude(Float.valueOf("00"));
                                schedule.setLatitude(Float.valueOf("00"));
                            }
                            schedule.setLngLat(texts[9]);
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
