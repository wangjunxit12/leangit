package com.meibanlu.driver.webservice.mappers;


import android.util.Log;

import com.meibanlu.driver.bean.TaskDetail;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;


public class SchedulesMapper implements Mapper<List<TaskDetail>,ResponseEnvelope> {
    @Override
    public List<TaskDetail> transform(ResponseEnvelope responseEnvelopeResult) {
        List<TaskDetail> list=null;
        if(responseEnvelopeResult.body!=null) {
                    String data = responseEnvelopeResult.body.lineResponse.model.value;
                    Log.e("ScheduleMapper ","data： "+data);
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
                                TaskDetail schedule = new TaskDetail();
                                schedule.setId(texts[0]);
                                schedule.setDate(texts[1]);
                                schedule.setCarNumber(texts[2]);
                                schedule.setSchedule(texts[5]);
                                schedule.setDepartId(Integer.valueOf(texts[19]));
                                schedule.setArriveId(Integer.valueOf(texts[20]));
                                schedule.setRoll(Boolean.valueOf(texts[8]));
                                schedule.setBusId(Integer.valueOf(texts[3]));
                                if(texts[12].equalsIgnoreCase("已完成")){
                                    schedule.setStatus(2);
                                }else if(texts[12].equalsIgnoreCase("未完成")){
                                    schedule.setStatus(0);
                                }else {
                                    schedule.setStatus(1);
                                }
                                if(texts.length>13){
                                    schedule.setInstructorName(texts[13]);
                                    schedule.setDriverTel(texts[14]);
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
