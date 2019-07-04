package com.meibanlu.driver.tool;

import android.app.Activity;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import static java.lang.Double.parseDouble;

/**
 * DriveRouteTool 修改行程驾车导航工具类
 * Created by lhq on 2017/6/29.
 */

public class DriveRouteTool implements RouteSearch.OnRouteSearchListener {
    private GetRouteData getRouteData;

    /**
     * @param activity     activity
     * @param startLatlng  startLatlng 开始的经纬度 104.6594,30.87954
     * @param endLatLng    endLatlng结束的经纬度
     * @param markId       markerId多线程标记
     * @param getRouteData 导航数据回调接口
     */
    public DriveRouteTool(Activity activity, String startLatlng, String endLatLng, String markId, GetRouteData getRouteData) {
        this.getRouteData = getRouteData;
        String[] latLngStart = startLatlng.split(",");
        String[] latLngEnd = endLatLng.split(",");
        LatLonPoint mStartPoint = new LatLonPoint(parseDouble(latLngStart[1]), parseDouble(latLngStart[0]));//起点，39.942295,116.335891
        LatLonPoint mEndPoint = new LatLonPoint(Double.parseDouble(latLngEnd[1]), Double.parseDouble(latLngEnd[0]));//终点，39.995576,116.481288
        RouteSearch routeSearch = new RouteSearch(activity);
        routeSearch.setRouteSearchListener(this);
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        fromAndTo.setStartPoiID(markId);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }


    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
        if (rCode == 1000) {
            DrivePath drivePath = result.getPaths().get(0);
            float dis = drivePath.getDistance() / 1000; //距离
            float dur = (float) drivePath.getDuration() / 3600; //时间
//            String distance = AMapDistanceUtil.getFriendlyLength(dis);
//            String time = AMapDistanceUtil.getFriendlyTime(dur);
            String strMarker = result.getDriveQuery().getFromAndTo().getStartPoiID();
            getRouteData.routeResult(dis, dur, strMarker,result);
        }
    }


    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    public interface GetRouteData {
        void routeResult(float dis, float dur, String marker, DriveRouteResult result);
    }
}
