package com.meibanlu.driver.tool;

import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceStatusListener;
import com.meibanlu.driver.activity.RunMapActivity;
import com.meibanlu.driver.application.DriverApplication;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RouteTool 轨迹纠偏工具类
 * Created by lhq on 2017/9/25.
 */

public class RouteTool implements TraceStatusListener {
    private static LBSTraceClient lbsTraceClient;
    private static RouteTool routeTool;
    public static String fileName;
    //单线程池，位置上传
    private ExecutorService locationServicePool = Executors.newSingleThreadExecutor();
    int i = 0;

    private RouteTool() {
        lbsTraceClient = LBSTraceClient.getInstance(DriverApplication.getApplication());
    }

    public static RouteTool getInstance() {
        if (routeTool == null) {
            routeTool = new RouteTool();
        }
        return routeTool;
    }

    /**
     * 开始纠偏
     */
    public void startTrace() {
        lbsTraceClient.startTrace(this);
    }

    /**
     * 停止纠偏
     */
    public void stopTrace() {
        if (routeTool != null) {
            routeTool = null;
            fileName = null;
            lbsTraceClient.stopTrace();
            lbsTraceClient.destroy();
            lbsTraceClient = null;
        }
    }

    @Override
    public void onTraceStatus(List<TraceLocation> list, final List<LatLng> list1, String result) {
        if (result.equals("纠偏成功")) {
            if (RunMapActivity.getInstance() != null) {
                RunMapActivity.getInstance().setTraceStatus(list1);
            }
            if (!TextUtils.isEmpty(CommonData.holderTrip.getTrip().getId())) {
//            if (i == 6) {
//                i = 0;
                //写入文件
                locationServicePool.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (list1 != null && list1.size() > 0) {
                            StringBuilder addStrGps = new StringBuilder();
                            //加入出发打卡点的经纬度
                            if (CommonData.holderTrip != null && CommonData.holderTrip.getStartSignPosition() != null) {
                                addStrGps.append(CommonData.holderTrip.getStartSignPosition());
                            }
                            try {
                                for (LatLng latLng : list1) {
                                    String context = latLng.longitude + "," + latLng.latitude + ";";
                                    if (!addStrGps.toString().contains(context)) {
                                        addStrGps.append(latLng.longitude).append(",").append(latLng.latitude).append(";");
                                    }
                                }
                            } catch (ConcurrentModificationException e) {
                                return;
                            }
                            String strGps = addStrGps.toString();
                            String endGps = strGps.substring(0, strGps.length() - 1);
                            if (TextUtils.isEmpty(fileName)) {
                                fileName = FilePath.getRoutePath() + "/" + CommonData.holderTrip.getTrip().getId() + ".txt";
//                            fileName = FilePath.getRoutePath() + "/" + "test.txt";
                                FileTool.createFile(fileName);
                            } else {
                                new WriteText().startWrite(endGps, fileName);
                            }
                        }
                    }
                });
            }
//            i++;
//        }
        }
    }
}

