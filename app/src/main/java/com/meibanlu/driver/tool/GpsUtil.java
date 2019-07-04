package com.meibanlu.driver.tool;


import android.text.TextUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 设置路线的范围
 * Created by lhq on 2017/10/16.
 */

public class GpsUtil {
    private static List<LatLng> routeRange;//路线范围
    private static int deviationTimes; //偏离次数

    public static String getStrRoutRangePolyline() {
        if (routeRange != null && routeRange.size() > 0) {
            StringBuffer strRouteRange = new StringBuffer();
            for (LatLng latLng : routeRange) {
                strRouteRange.append(latLng.getLongitude() + "," + latLng.getLatitude()+";");
            }
            String str = UtilTool.dealEndStr(strRouteRange);
            return str;
        } else return null;
    }


    /**
     * 设置预定路线轨迹的范围
     *
     * @param regularPath 预定路线 103.4342,30.219389;104.9427,30.872,103.4342,30.219389;104.9427,30.872
     */
    static void setRouteRange(String regularPath) {
        if (TextUtils.isEmpty(regularPath)) {
            return;
        }
        routeRange = new ArrayList<>();
        double directionLatLng = 0.00000000001; //判断左右方向的经度偏移
        double divLatLng = 0.0005; //实际路线的偏移，大约50米
        List<LatLng> leftPoint = new ArrayList<>();
        List<LatLng> rightPoint = new ArrayList<>();
        List<LatLng> latLngs = new ArrayList<>();
        String[] arrAllLatlng = regularPath.split(";");
        for (String strLatlng : arrAllLatlng) {
            String[] arrLatlng = strLatlng.split(",");
            if (arrLatlng.length == 2) {
                LatLng latLng = new LatLng(Double.parseDouble(arrLatlng[1]), Double.parseDouble(arrLatlng[0]));
                if (!latLngs.contains(latLng)) {
                    latLngs.add(latLng);
                }
            }
        }
        for (int i = 0; i < latLngs.size(); i++) {
            if (i > 1) {
                LatLng latLngA = latLngs.get(i - 2); //3点折线第一点
                LatLng latLngB = latLngs.get(i - 1); //3点折线第2点
                LatLng latLngC = latLngs.get(i); ////3点折线第3点
                double angleA = getAngle(new AMapLatLng(latLngB), new AMapLatLng(latLngA));
                double angleB = getAngle(new AMapLatLng(latLngB), new AMapLatLng(latLngC));
                double averageAngle = (angleA + angleB) / 2;
                LatLng guessPoint1 = getGuessPoint(averageAngle, latLngB, directionLatLng);//猜测点1
                double angleAB = getAngle(new AMapLatLng(latLngA), new AMapLatLng(latLngB)); //AB与正北方向夹角
                double angleAtoGuessPoint1 = getAngle(new AMapLatLng(latLngA), new AMapLatLng(guessPoint1)); //A点与猜测点1与正北方向的夹角
                double leftAngle, rightAngle;//左边角度，右边角度
                leftAngle = averageAngle;
                if (angleAB == 0) {
                    if (averageAngle < 180) {
                        leftAngle = averageAngle + 180;
                    }
                } else if (angleAtoGuessPoint1 > angleAB) {
                    if (averageAngle > 180) {
                        leftAngle = averageAngle - 180;
                    } else {
                        leftAngle = averageAngle + 180;
                    }
                }
                if (leftAngle > 180) {
                    rightAngle = leftAngle - 180;
                } else {
                    rightAngle = leftAngle + 180;
                }
                LatLng leftLatLng = getGuessPoint(leftAngle, latLngB, divLatLng);
                LatLng rightLatLng = getGuessPoint(rightAngle, latLngB, divLatLng);
                leftPoint.add(leftLatLng);
                rightPoint.add(rightLatLng);
            }

        }
        Collections.reverse(rightPoint);
        routeRange.add(latLngs.get(0));//加入起点
        routeRange.addAll(leftPoint);
        routeRange.add(latLngs.get(latLngs.size() - 1));//加入终点
        routeRange.addAll(rightPoint);
    }

    /**
     * 获取两个经纬度与正北方向夹角
     *
     * @param A LatlngA
     * @param B LatLngB
     * @return double夹角
     */
    private static double getAngle(AMapLatLng A, AMapLatLng B) {
        double dx = (B.m_RadLo - A.m_RadLo) * A.Ed;
        double dy = (B.m_RadLa - A.m_RadLa) * A.Ec;
        double angle;
        angle = Math.atan(Math.abs(dx / dy)) * 180 / Math.PI;
        double dLo = B.m_Longitude - A.m_Longitude;
        double dLa = B.m_Latitude - A.m_Latitude;
        if (dLo > 0 && dLa <= 0) {
            angle = (90 - angle) + 90;
        } else if (dLo <= 0 && dLa < 0) {
            angle = angle + 180;
        } else if (dLo < 0 && dLa >= 0) {
            angle = (90 - angle) + 270;
        }
        return angle;
    }

    /**
     * @param averageAngle A与正北B与正北角度的平均值
     * @param latLngB      中间点的latlng
     * @param divLatitude  偏移的一点纬度
     * @return 猜测点
     */
    private static LatLng getGuessPoint(double averageAngle, LatLng latLngB, double divLatitude) {
        double angle = -Math.PI * (averageAngle - 90) / 180;
        double moveY = divLatitude * Math.sin(angle);
        double moveX = divLatitude * Math.cos(angle);
        double moveLatitude = latLngB.latitude + moveY;
        double moveLongitude = latLngB.longitude + moveX;
        return new LatLng(moveLatitude, moveLongitude);
    }

    private static class AMapLatLng {
        double Rc = 6378137;
        double Rj = 6356725;
        double m_LoDeg, m_LoMin, m_LoSec;
        double m_LaDeg, m_LaMin, m_LaSec;
        double m_Longitude, m_Latitude;
        double m_RadLo, m_RadLa;
        double Ec;
        double Ed;

        private AMapLatLng(LatLng latLng) {
            double longitude = latLng.longitude;
            double latitude = latLng.latitude;
            m_LoDeg = (int) longitude;
            m_LoMin = (int) ((longitude - m_LoDeg) * 60);
            m_LoSec = (longitude - m_LoDeg - m_LoMin / 60) * 3600;

            m_LaDeg = (int) latitude;
            m_LaMin = (int) ((latitude - m_LaDeg) * 60);
            m_LaSec = (latitude - m_LaDeg - m_LaMin / 60) * 3600;

            m_Longitude = longitude;
            m_Latitude = latitude;
            m_RadLo = longitude * Math.PI / 180;
            m_RadLa = latitude * Math.PI / 180;
            Ec = Rj + (Rc - Rj) * (90 - m_Latitude) / 90;
            Ed = Ec * Math.cos(m_RadLa);
        }
    }

    private static class LatLng {
        double longitude;
        double latitude;

        /**
         * 通过经纬度对象构建Point
         *
         * @param polygon 类似 1
         */
        private LatLng(String polygon) {
            if (polygon != null) {
                this.setLongitude(Double.valueOf(polygon.split(",")[0]));
                this.setLatitude(Double.valueOf(polygon.split(",")[1]));
            }
        }

        private LatLng(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        private double getLongitude() {
            return longitude;
        }

        private void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        private double getLatitude() {
            return latitude;
        }

        private void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LatLng latLng = (LatLng) o;

            if (Double.compare(latLng.longitude, longitude) != 0) return false;
            return Double.compare(latLng.latitude, latitude) == 0;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(longitude);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(latitude);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

    /**
     * 清空routeRange
     */
    public static void clearRouteRange() {
        routeRange = null;
    }

    /**
     * 判断是否在区域内部
     *
     * @param testStr testStr 判断的点 104.234,30.374
     */
    public static void checkPointInPolygon(String testStr) {
        if (routeRange == null || routeRange.size() < 1 || TextUtils.isEmpty(testStr)) {
            return;
        }
        boolean isIn = false;
        LatLng testPoint = new LatLng(testStr);
        int polyLength = routeRange.size();//获得点的个数
        //循环polygon
        for (int i = 0; i < polyLength; i++) {
            LatLng p1 = routeRange.get(i);  //第一个点
            LatLng p2 = routeRange.get((i + 1) % polyLength);//第二个点
            //求解 y = testPoint.y 与 p1, p2 的交点
            if (p1.getLatitude() == p2.getLatitude() ||  //如果p1 p2与y=testPoint.y平行
                    testPoint.getLatitude() < Math.min(p1.getLatitude(), p2.getLatitude()) || //交点在p1 p2延长线上
                    testPoint.getLatitude() >= Math.max(p1.getLatitude(), p2.getLatitude())) { //交点在p1 p1延长线上)
                continue;
            }
            // 求交点的 X 坐标 --------------------------------------------------------------
            double x = (testPoint.getLatitude() - p1.getLatitude()) * (p2.getLongitude() - p1.getLongitude()) / (p2.getLatitude() - p1.getLatitude()) + p1.getLongitude();
            if (x > testPoint.getLongitude()) {
                isIn = !isIn; //交替
            }
        }
        if (!isIn) {
            deviationTimes++;
        }
    }

    /**
     * 提醒偏离
     */
    public static void remindDeviation(String testStr) {
        checkPointInPolygon(testStr);
        if (deviationTimes > 6) {
            deviationTimes = 0;
            XmPlayer.getInstance().playTTS("您已经偏离预定路线");
        }
    }

}
