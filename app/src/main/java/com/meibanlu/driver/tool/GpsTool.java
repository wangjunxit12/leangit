package com.meibanlu.driver.tool;

import android.util.Log;

import com.amap.api.maps.model.LatLng;

import java.util.List;

/**
 * Gps判断该点是否在区域内
 * <p>
 * Created by lhq on 2017/9/19.
 */

public class GpsTool {
    /**
     * @param testStr “104.057281,30.554748”
     * @param points  “104.057281,30.554748;104.074533,30.551791;104.075477,30.540666;104.061272,30.541997”
     * @return boolean
     */
    public static boolean checkPointInPolygon(String testStr, String points) {
        String[] polygon = points.split(";");
        boolean isIn = checkPointInPolygon(testStr, polygon);
        return isIn;
    }

    /**
     * 判断是否在区域内部
     *
     * @param testStr
     * @param polygon
     * @return
     */
    public static boolean checkPointInPolygon(String testStr, String[] polygon) {
        boolean isIn = false;
        PointBean testPoint = new PointBean(testStr);
        int polyLength = polygon.length;//获得点的个数
        //循环polygon
        for (int i = 0; i < polyLength; i++) {
            PointBean p1 = new PointBean(polygon[i]);  //第一个点
            PointBean p2 = new PointBean(polygon[(i + 1) % polyLength]);//第二个点
            //求解 y = testPoint.y 与 p1, p2 的交点
            if (p1.getY() == p2.getY() ||  //如果p1 p2与y=testPoint.y平行
                    testPoint.getY() < Math.min(p1.getY(), p2.getY()) || //交点在p1 p2延长线上
                    testPoint.getY() >= Math.max(p1.getY(), p2.getY())) { //交点在p1 p1延长线上)
                continue;
            }
            // 求交点的 X 坐标 --------------------------------------------------------------
            double x = (testPoint.getY() - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY()) + p1.getX();
            if (x > testPoint.getX()) {
                isIn = !isIn; //交替
            }
        }
        return isIn;
    }

    /**
     * 判断是否在区域内部
     *
     * @param testStr
     * @return
     */
    public static boolean checkPointInPolygon(String testStr, List<LatLng> latLngs) {
        boolean isIn = false;
        PointBean testPoint = new PointBean(testStr);
        int polyLength = latLngs.size();//获得点的个数
        //循环polygon
        for (int i = 0; i < polyLength; i++) {
            PointBean p1 = new PointBean(latLngs.get(i));  //第一个点
            PointBean p2 = new PointBean(latLngs.get((i + 1) % polyLength));//第二个点
            //求解 y = testPoint.y 与 p1, p2 的交点
            if (p1.getY() == p2.getY() ||  //如果p1 p2与y=testPoint.y平行
                    testPoint.getY() < Math.min(p1.getY(), p2.getY()) || //交点在p1 p2延长线上
                    testPoint.getY() >= Math.max(p1.getY(), p2.getY())) { //交点在p1 p1延长线上)
                continue;
            }
            // 求交点的 X 坐标 --------------------------------------------------------------
            double x = (testPoint.getY() - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY()) + p1.getX();
            if (x > testPoint.getX()) {
                isIn = !isIn; //交替
            }
        }
        return isIn;
    }

    /**
     * 根据半径检查是否到达指定区域
     *
     * @return
     */
    public static boolean checkPointInPolygon(Double startLatitude, Double startLongitude, String strEndLatLng, float effectiveDistance) {
        double distance = MapUtil.getDistance(startLatitude, startLongitude, strEndLatLng);
        return distance <= effectiveDistance;
    }

    /**
     * 根据半径检查是否到达指定区域
     *
     * @param effectiveDistance 范围值
     * @return 是否距离在 范围内
     */
    public static boolean checkPointInPolygon(String p1, String p2, double effectiveDistance) {
        Log.i("GpsTool","checkPointInPolygon:  "+p1+"p2:  "+p2+"effectiveDistance:  "+effectiveDistance);
        return MapUtil.getDistance(p1, p2) <= effectiveDistance;
    }

    public static double getDistance(String p1, String p2, double effectiveDistance) {
        Log.i("GpsTool","checkPointInPolygon:  "+p1+"p2:  "+p2);
        return MapUtil.getDistance(p1, p2)- effectiveDistance;
    }
}
