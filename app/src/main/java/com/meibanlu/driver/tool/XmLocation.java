//package com.meibanlu.driver.tool;
//
//import android.text.TextUtils;
//
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationListener;
//import com.amap.api.maps.AMapUtils;
//import com.amap.api.maps.model.LatLng;
//import com.meibanlu.driver.RunActivity;
//import com.meibanlu.driver.activity.RunMapActivity;
//import com.meibanlu.driver.application.DriverApplication;
//import com.meibanlu.driver.bean.TodayTaskBean;
//import com.meibanlu.driver.tool.web.SimpleCallBack;
//import com.meibanlu.driver.tool.web.WebInterface;
//import com.meibanlu.driver.tool.web.WebService;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * 创建一个地理位置相关的类，动态监测位置的变化，然后通知首页进行相应的更新或者播放文字语音
// * Created by leigang on 2016/10/13.
// */
//public class XmLocation implements AMapLocationListener {
//    private static XmLocation xmLocation;
//    private static boolean showStart;
//    private static boolean showEnd;
//    private LatLng oldLatLng;//旧的经纬度,用于距离大于10米时候上传经纬度
//    private int moveDistance = 10;//有效的移动距离，大于10米时上传当前的位置信息
//    private AMapLocationClient locationClient;
//    //单线程池，位置上传
//    private ExecutorService locationServicePool = Executors.newSingleThreadExecutor();
//
//    private XmLocation() {
//        this.locationClient = new AMapLocationClient(DriverApplication.getApplication());
//        MapUtil.setMapClient(locationClient, this, 5000); //设定Map的参数
//    }
//
//    /**
//     * 开启定位服务，使用静态方法，用于便于测试
//     */
//    public static void startXmLocation() {
//        if (xmLocation == null) {
//            xmLocation = new XmLocation();
//        }
//        xmLocation.startLocation();
////        new XmLocationByServer();
//    }
//
//    public static void stopXmLocation() {
//        if (xmLocation != null) {
//            xmLocation.stopLocation();
//        }
//    }
//
//    @Override
//    public void onLocationChanged(final AMapLocation aMapLocation) {
//        if (aMapLocation != null && aMapLocation.getLatitude() != 0) {
//            //上传位置
//            locationServicePool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    //打卡
//                    if (!TextUtils.isEmpty(CommonData.tripId)) {
//                        final String position = aMapLocation.getLongitude() + "," + aMapLocation.getLatitude();
//                        //boolean isIn = GpsTool.checkPointInPolygon(position, "104.057281,30.554748;104.074533,30.551791;104.075477,30.540666;104.061272,30.541997");
//                        if (CommonData.isStartSign) {
//                            boolean isArrive = driverEndSign(aMapLocation); //到站打卡判断
//                            if (!isArrive) {//到站后不上传经纬度
//                                LatLng currentLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
//                                if (oldLatLng == null) {
//                                    positionUpload(position);//出发成功，实时位置上传
//                                } else {
//                                    double distance = AMapUtils.calculateLineDistance(oldLatLng, currentLatLng);
//                                    if (distance > moveDistance) {
//                                        GpsUtil.remindDeviation(position); //轨迹是否偏离
//                                        positionUpload(position);
//                                    }
//                                }
//                                oldLatLng = currentLatLng;
//                            }
//                        } else {
//                            driverStartSign(aMapLocation);//司机打卡
//                        }
//                    } else {
//
//                        initTripId();//获取tripId打卡信息
//                    }
//                }
//            });
//            if (RunMapActivity.getInstance() != null) {
//                //地图打开了
//                RunMapActivity.getInstance().setLocation(aMapLocation);
//            }
//            CommonData.aMapLocation = aMapLocation;//赋值给地图那边用的
//            showSignStatus();
//        }
//
//    }
//
//    private void startLocation() {
//        locationClient.startLocation();
//    }
//
//    private void stopLocation() {
//        locationClient.stopLocation();
//    }
//
//    /**
//     * 判断哪个时间点打卡
//     */
//    private void initTripId() {
//        if (CommonData.listTask != null && CommonData.listTask.size() > 0) {
//            String time = TimeTool.getCurrentTime("yyyy-MM-dd HH:mm:ss");
//            if (!TextUtils.isEmpty(time)) {
//                T.log(time);
//                for (int i = 0; i < CommonData.listTask.size(); i++) {
//                    TodayTaskBean.TaskDetail list = CommonData.listTask.get(i);
//                    boolean isTrueTime = getDistanceTimes(time, list.getLine().getSignStartTime(), list.getLine().getSignTimeRange());
//                    if (isTrueTime) {
//                        initCommon(i);
//                        return;
//                    }
//                }
//            }
//        }
//    }
//
//
//    /**
//     * Toast打卡状态
//     */
//    private void showSignStatus() {
//        if (CommonData.isEndSign) {//提示打卡成功,终点
//            if (CommonData.isServiceEndSign) {//app打卡成功,雷刚未显示打卡成功
//                if (!showEnd) {
//                    showEnd = true;
//                    T.showShort(T.getStringById(RunActivity.string.end_sign_success));
//                }
//            } else {
//                driverStartOrEndSignToService("2");
//            }
//        }
//        if (CommonData.isStartSign) { //提示打卡成功，起点
//            if (CommonData.isServiceStartSign) {//app打卡成功,雷刚未显示打卡成功
//                if (!showStart) {
//                    showStart = true;
//                    T.showShort(T.getStringById(RunActivity.string.start_sign_success));
//                }
//            } else {
//                driverStartOrEndSignToService("1");
//            }
//        }
//    }
//
//
//    /**
//     * 实时上传位置
//     *
//     * @param position 104.45,30.26487
//     */
//    private void positionUpload(String position) {
//        Map<String, Object> param = new HashMap<>();
//        param.put("position", position);
//        param.put("tripId", CommonData.tripId);
//        param.put("driverName", CommonData.trip.getDriverName());
//        param.put("carNumber", CommonData.trip.getCarNumber());
//        param.put("phone", CommonData.trip.getDriverTel());
//        WebService.doRequest(WebService.POST, WebInterface.UPLOAD_GPS, param, new SimpleCallBack() {
//            @Override
//            public void success(int code, String message, String data) {
//                T.log("dada" + code);
//            }
//        });
//    }
//
//    /**
//     * 司机出发打卡
//     */
//    private void driverStartSign(AMapLocation aMapLocation) {
//        boolean isIn = GpsTool.checkPointInPolygon(aMapLocation.getLatitude(), aMapLocation.getLongitude(), CommonData.startLatlng, CommonData.startRadius);
//        if (!CommonData.isStartIn) {
//            if (isIn) {
//                CommonData.isStartIn = true; //出发进入范围
//            }
//        } else {
//            if (!isIn) {
//                CommonData.isStartOut = true; //出发走出范围
//            }
//        }
//        if (CommonData.isStartIn && CommonData.isStartOut) {
//            CommonData.isStartSign = true; //出发打卡成功
//            driverStartOrEndSignToService("1");
//            RouteTool.getInstance().startTrace();//开始纠偏
//        }
//    }
//
//    /**
//     * 司机终点签到打卡
//     */
//    private boolean driverEndSign(AMapLocation aMapLocation) {
//        boolean isIn = GpsTool.checkPointInPolygon(aMapLocation.getLatitude(), aMapLocation.getLongitude(), CommonData.endLatlng, CommonData.endRadius);
//        if (isIn) {
//            CommonData.isEndSign = true;
//            RouteTool.getInstance().stopTrace();//停止轨迹纠偏
//            driverStartOrEndSignToService("2");
//            RouteUpload.uploadFileType(RouteUpload.UPLOAD_TXT); //上传完成轨迹的txt文档
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 发车到站打卡
//     *
//     * @param signType signType   1：出发   2：到站
//     */
//    private void driverStartOrEndSignToService(final String signType) {
//        Map<String, Object> param = new HashMap<>();
//        param.put("tripId", CommonData.tripId);
//        param.put("status", signType);
//        WebService.doRequest(WebService.POST, WebInterface.DRIVER_SIGN, param, new SimpleCallBack() {
//            @Override
//            public void success(int code, String message, String data) {
//                if (code == 0) {
//                    if (signType.equals("1")) {
//                        CommonData.isServiceStartSign = true; //出发打卡
//                        GpsUtil.setRouteRange(data); //路径范围
//                    } else {
//                        CommonData.isServiceEndSign = true; //到站服务器打卡
//                        CommonData.tripId = null;//将tripId设置成空
//                        GpsUtil.clearRouteRange(); //路径范围置空
//                    }
//                }
//            }
//        });
//
//
//    }
//
//    /**
//     * 选择路线,设置参数
//     */
//    private void initCommon(int routNumber) {
//        List<TodayTaskBean.TaskDetail> listTask = CommonData.listTask;
//        CommonData.trip = listTask.get(routNumber);
//        CommonData.startLatlng = CommonData.trip.getDepartStation().getLngLat();
//        CommonData.endLatlng = CommonData.trip.getArriveStation().getLngLat();
//        CommonData.startRadius = CommonData.trip.getDepartStation().getAreaRadius();
//        CommonData.endRadius = CommonData.trip.getArriveStation().getAreaRadius();
//        CommonData.tripId = CommonData.trip.getId();
//        showStart = false;
//        showEnd = false;
//    }
//
//    /**
//     * 获取对应的站点
//     * 判断在不在时间打卡范围
//     *
//     * @param str1 时间参数 当前时间 当前时间：2017-09-28 12:00:00
//     * @param str2 时间参数   发车时间：2017-09-28 12:00
//     */
//    public static boolean getDistanceTimes(String str1, String str2, int timeRange) {
//        String startTime = str2 + ":00";
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        long min = 0;
//        try {
//            Date one = df.parse(str1);
//            Date two = df.parse(startTime);
//            long time1 = one.getTime();
//            long time2 = two.getTime();
//            long diff;
//            if (time1 < time2) {
//                diff = time2 - time1;
//            } else {
//                diff = time1 - time2;
//            }
//            min = ((diff / (60 * 1000)));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        System.out.println(min);
//        if (min <= timeRange) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//}
