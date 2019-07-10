package com.meibanlu.driver.webservice.mappers;


import android.util.Log;

import com.meibanlu.driver.bean.LineStation;
import com.meibanlu.driver.bean.TaskDetail;
import com.meibanlu.driver.tool.MapUtil;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;


import retrofit2.adapter.rxjava2.Result;


public class ScheduleMapper implements Mapper<List<TaskDetail>,ResponseEnvelope> {
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
                                LineStation departStation=new LineStation();
                                LineStation backUpStation=new LineStation();
                                schedule.setDriverName("9527");
                                schedule.setId(texts[0]);
                                schedule.setDate(texts[1]);
                                schedule.setCarNumber(texts[2]);
                                schedule.setSchedule(texts[5]);
                                schedule.setRoll(Boolean.valueOf(texts[8]));
                                schedule.setBusId(Integer.valueOf(texts[4]));

                                if(texts[12].equalsIgnoreCase("已完成")){
                                    schedule.setStatus(2);
                                }else if(texts[12].equalsIgnoreCase("未完成")){
                                    schedule.setStatus(0);
                                }else {
                                    schedule.setStatus(1);
                                }
                                if(texts.length>23){
                                    schedule.setInstructorName(texts[13]);
                                    schedule.setDriverTel(texts[14]);
                                    schedule.setDepartId(Integer.valueOf(texts[19]));
                                    schedule.setArriveId(Integer.valueOf(texts[20]));
                                    if(texts[28]!=null&&texts[27]!=null){
                                        schedule.setOccupiedSeats(Integer.valueOf(texts[28])-Integer.valueOf(texts[27]));
                                    }
                                    departStation.setAreaRadius(Float.valueOf(texts[29]));
                                    backUpStation.setAreaRadius(Float.valueOf(texts[30]));
                                    departStation.setId(Integer.valueOf(texts[19]));
                                    backUpStation.setId(Integer.valueOf(texts[20]));
                                    departStation.setName(texts[21]);
                                    backUpStation.setName(texts[22]);
                                    if(texts[31].equals("1")){
                                        schedule.setCircle(true);
                                    }else {
                                        schedule.setCircle(false);
                                    }
                                    if(texts[23].contains(";")){
                                        String [] textss=texts[23].split(";");
                                        departStation.setLongitude(Float.valueOf(textss[0]));
                                        departStation.setLatitude(Float.valueOf(textss[1]));
                                    }else {
                                        departStation.setLongitude(Float.valueOf("00"));
                                        departStation.setLatitude(Float.valueOf("00"));
                                    }
                                    if(texts[25].contains(";")){
                                        String [] textss=texts[25].split(";");
                                        backUpStation.setLongitude(Float.valueOf(textss[0]));
                                        backUpStation.setLatitude(Float.valueOf(textss[1]));
                                    }else {
                                        backUpStation.setLongitude(Float.valueOf("00"));
                                        backUpStation.setLatitude(Float.valueOf("00"));
                                    }
                                    departStation.setLngLat(texts[23]);
                                    backUpStation.setLngLat(texts[25]);
                                    Double distance= MapUtil.getDistance(texts[23],texts[25]);
                                    if(distance>100){
                                        T.log("distance:  "+distance);
                                        schedule.setDistance((int) Math.round(distance));
                                    }
                                }
                                schedule.setDepartStation(departStation);
                                schedule.setArriveStation(backUpStation);
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
