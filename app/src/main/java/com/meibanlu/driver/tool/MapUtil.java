package com.meibanlu.driver.tool;

import android.graphics.Color;
import android.view.animation.LinearInterpolator;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.meibanlu.driver.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 处理地图的工具类
 * Created by leigang on 16/9/14.
 */
public class MapUtil {
    /**
     * mark转成LatLng对象
     *
     * @param markers
     * @return
     */
    public static List<LatLng> markerToList(List<Marker> markers, Map<String, String> spotLat) {
        List<LatLng> result = new ArrayList<>();
        for (Marker marker : markers) {
            result.add(marker.getPosition());
        }
        if (spotLat != null && spotLat.size() > 0) {
            for (Object o : spotLat.keySet()) {
                LatLng latLng = MapUtil.String2lat(spotLat.get(o).split(";")[0]);
                result.add(latLng);
            }
        }
        return result;
    }

    /**
     * 设置定位回调参数
     *
     * @param locationClient locationClient
     * @param listener       listener
     */

    public static void setMapClient(AMapLocationClient locationClient, AMapLocationListener listener) {
        locationClient.setLocationListener(listener);
        // 初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(false);
        // 设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //获取最近3s内精度最高的一次定位结果：
        mLocationOption.setOnceLocationLatest(true);
        // 设置是否强制刷新WIFI，默认为强制刷新

        mLocationOption.setWifiScan(true);
        mLocationOption.setWifiActiveScan(true);
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);

        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(true);
        // 设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(TimeUnit.SECONDS.toMillis(Constants.LOCATION_TIME));
        // 给定位客户端对象设置定位参数
        //是否返回方位角
        mLocationOption.setSensorEnable(true);
        locationClient.setLocationOption(mLocationOption);
    }

    public static void setAMapStyle(AMap aMap, LocationSource source) {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.test_positioning_balls));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.WHITE);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(50, 190, 226, 238));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setLocationSource(source);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setRotateGesturesEnabled(false);// 设置地图是否能够旋转
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.getUiSettings().setZoomControlsEnabled(false);
        //设置定位不跟随
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

    }

    /**
     * 地上生长的Marker
     * fromX - 动画开始时横坐标位置toX - 动画结束时横坐标位置fromY - 动画开始时纵坐标位置toY - 动画结束时纵坐标位置
     */
    public static void startGrowAnimation(Marker growMarker) {
        if (growMarker != null) {
            Animation animation = new ScaleAnimation(0, 1, 0, 1);
            animation.setInterpolator(new LinearInterpolator());
            //整个移动所需要的时间
            animation.setDuration(500);
            //设置动画
            growMarker.setAnimation(animation);
            //开始动画
            growMarker.startAnimation();
        }
    }

    /**
     * String 103.975092,30.734245
     *
     * @return LatLng
     */
    public static LatLng String2lat(String str) {
        String[] point = str.split(",");
        double lat = Double.parseDouble(point[1]);
        double lng = Double.parseDouble(point[0]);
        return new LatLng(lat, lng);
    }

    /**
     * 将四个点加入集合中
     *
     * @param LatLngs LatLngs
     * @return rebuildLatlng
     */
    public static List<LatLng> addFourPoint(List<LatLng> LatLngs) {
        List<LatLng> rebuildLatLng = new ArrayList<>();
        rebuildLatLng.addAll(LatLngs);
        double eastPoint = 0;//东
        double westPoint = 0;//东
        double southPoint = 0; //南
        double northPoint = 0; //北
        for (LatLng latLng : LatLngs) {
            if (eastPoint == 0 || eastPoint < latLng.longitude) {
                eastPoint = latLng.longitude;
            }
            if (westPoint == 0 || westPoint > latLng.longitude) {
                westPoint = latLng.longitude;
            }
            if (southPoint == 0 || southPoint > latLng.latitude) {
                southPoint = latLng.latitude;
            }
            if (northPoint == 0 || northPoint < latLng.latitude) {
                northPoint = latLng.latitude;
            }
        }
        LatLng latEastNorth = new LatLng((3 * northPoint - southPoint) / 2, (3 * eastPoint - westPoint) / 2); //东北点
        LatLng latWestSouth = new LatLng((3 * southPoint - northPoint) / 2, (3 * westPoint - eastPoint) / 2); //西南点
        rebuildLatLng.add(latEastNorth);
        rebuildLatLng.add(latWestSouth);
        return rebuildLatLng;
    }

    /**
     * @param position  取点的位置
     * @param imagePath 图片路径
     */
    public static void addMarker(AMap aMap, LatLng position, int imagePath) {
        aMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromResource(imagePath)));
    }

    /**
     * 缩放地图
     */
    public static void setZoom(AMap aMap, List<LatLng> resultLatLng) {
        List<LatLng> latLngs = MapUtil.addFourPoint(resultLatLng);
        LatLngBounds.Builder builder = LatLngBounds.builder();
        if (latLngs == null || latLngs.size() == 0) {
            return;
        }
        if (latLngs.size() == 1) { //如果只有一个位置
            changeCamera(latLngs.get(0), aMap, 17.5f);
            return;
        } else {
            for (LatLng latLng : latLngs) {
                builder.include(latLng);
            }
        }
        LatLngBounds bounds = builder.build();
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }


    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    public static void changeCamera(CameraUpdate update, AMap aMap) {
        if (aMap != null && update != null) {
            aMap.animateCamera(update, 700, null);
        }
    }

    public static void changeCamera(Double latitude, Double longitude, AMap aMap, float zoomSize) {
        LatLng latLng = new LatLng(latitude, longitude);
        if (aMap == null || zoomSize == 0.0) {
            return;
        }
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, zoomSize, 0, 0));
        aMap.animateCamera(update, 800, null);
    }

    /**
     * 动画设置地图中心点的位置
     * new CameraPosition( LatLng, 18, 0, 0))  第二个参数是缩放等级，第三第四个是旋转角度
     *
     * @param latLng
     * @param aMap
     * @param zoomSize 地图缩放等级
     */
    public static void changeCamera(LatLng latLng, AMap aMap, float zoomSize) {
        if (latLng == null || aMap == null || zoomSize == 0.0) {
            return;
        }
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, zoomSize, 0, 0));
        aMap.animateCamera(update, 800, null);
    }

    /**
     * @param startLatitude  当前的点的纬度 104.26348
     * @param startLongitude 当前的点的经度 30.345
     * @param strEndLatLng   终点的经纬度 104.6475,30.43567
     * @return
     */
    public static double getDistance(Double startLatitude, Double startLongitude, String strEndLatLng) {
//        if (TextUtils.isEmpty(strEndLatLng) || startLongitude == 0 || startLongitude < 73 || startLongitude > 136) {
//            return -1;
//        }
        String[] arrayEndLatLng = strEndLatLng.split(";");
        if (arrayEndLatLng.length == 2) {
            LatLng startLatLng = new LatLng(startLatitude, startLongitude);
            LatLng endLatLng = new LatLng(Double.parseDouble(arrayEndLatLng[1]), Double.parseDouble(arrayEndLatLng[0]));
            return (double) AMapUtils.calculateLineDistance(startLatLng, endLatLng);
        }
        return -1;
    }

    /**
     * @param p1 点1         104.6475,30.43567
     * @param p2 点2         102.6475,30.43567
     * @return 距离
     */
    public static double getDistance(String p1, String p2) {
        String sep = ";";
        if (p1 == null || p2 == null || !p1.contains(sep) || !p2.contains(sep)) {
            return 0;
        }
        LatLng p1LatLng = new LatLng(Double.valueOf(p1.split(";")[1]), Double.valueOf(p1.split(";")[0]));
        LatLng p2LatLng = new LatLng(Double.valueOf(p2.split(";")[1]), Double.valueOf(p2.split(";")[0]));
        return (double) AMapUtils.calculateLineDistance(p1LatLng, p2LatLng);
    }

    /**
     * 获取两个经纬度与正北方向夹角
     *
     * @param A LatlngA
     * @param B LatLngB
     * @return double夹角
     */
    public static double getAngle(MyLatLng A, MyLatLng B) {
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
    public static LatLng getGuessPoint(double averageAngle, LatLng latLngB, double divLatitude) {
        double angle = -Math.PI * (averageAngle - 90) / 180;
        double moveY = divLatitude * Math.sin(angle);
        double moveX = divLatitude * Math.cos(angle);
        double moveLatitude = latLngB.latitude + moveY;
        double moveLongitude = latLngB.longitude + moveX;
        return new LatLng(moveLatitude, moveLongitude);
    }

}
