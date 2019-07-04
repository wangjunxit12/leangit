package com.meibanlu.driver.tool;


import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.meibanlu.driver.activity.RunMapActivity;
import com.meibanlu.driver.application.DriverApplication;

import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * 轨迹纠偏
 * Created by lhq on 2017/11/16.
 */

public class TraceTool implements TraceListener {
    private int mCoordinateType = LBSTraceClient.TYPE_AMAP;
    /**
     * 开始打卡点的经纬度
     */

    private String startLatLng = "";
    /**
     * 结束打卡点的经纬度
     */
    private String endLatLng = "";

    /**
     * 开始纠偏
     */
    public void startTrace() {
        HolderTrip holderTrip = CommonData.holderTrip;
        if (holderTrip != null) {
            String tripId = CommonData.holderTrip.getTrip().getId();
            List<TraceLocation> listTrace = UtilTool.getDbManager().queryAllPosition(tripId);
            LBSTraceClient mTraceClient = new LBSTraceClient(DriverApplication.getApplication());
            mTraceClient.queryProcessedTrace(Integer.parseInt(tripId), listTrace, mCoordinateType, this);
            startLatLng = holderTrip.getStartSignPosition();
            endLatLng = holderTrip.getEndSignPosition();
        }
    }

    @Override
    public void onRequestFailed(int lineID, String s) {

    }

    @Override
    public void onTraceProcessing(int lineID, int i1, List<LatLng> list) {

    }

    /**
     * @param lineID      lineID Marker
     * @param list        纠偏的经纬度
     * @param distance    距离
     * @param waitingTime 等待时间
     */
    @Override
    public void onFinished(final int lineID, final List<LatLng> list, int distance, int waitingTime) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (list != null && list.size() > 0) {
                    if (RunMapActivity.getInstance() != null) {
                        RunMapActivity.getInstance().setTraceStatus(list);
                    }
                    StringBuilder addStrGps = new StringBuilder();
                    try {
                        //起点
                        if (!TextUtils.isEmpty(startLatLng)) {
                            addStrGps.append(startLatLng).append(";");
                        }
                        for (LatLng latLng : list) {
                            String context = latLng.longitude + "," + latLng.latitude + ";";
                            if (!addStrGps.toString().contains(context)) {
                                addStrGps.append(latLng.longitude).append(",").append(latLng.latitude).append(";");
                            }
                        }
                        //终点
                        if (!TextUtils.isEmpty(endLatLng)) {
                            addStrGps.append(endLatLng).append(";");
                        }
                    } catch (ConcurrentModificationException e) {
                        return;
                    }
                    String strGps = addStrGps.toString();
//                    String endGps = strGps.substring(0, strGps.length() - 1);
                    String fileName = FilePath.getRoutePath() + "/" + lineID + ".txt";
                    FileTool.createFile(fileName);
                    FileTool.writeTxt(strGps, fileName);
                    UtilTool.getDbManager().deleteAllTripId();
                    //上传完成轨迹的txt文档
//                    RouteUpload.uploadFileType(RouteUpload.UPLOAD_TXT);
                }
            }
        }).start();
    }
}
