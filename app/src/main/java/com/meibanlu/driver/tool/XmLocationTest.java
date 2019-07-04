//package com.meibanlu.driver.tool;
//
//import android.annotation.TargetApi;
//import android.app.Service;
//import android.content.Intent;
//import android.os.Build;
//import android.os.IBinder;
//import android.text.TextUtils;
//
//import com.amap.api.location.AMapLocation;
//import com.amap.api.maps.AMapUtils;
//import com.amap.api.maps.model.LatLng;
//import com.meibanlu.driver.R;
//import com.meibanlu.driver.activity.RunMapActivity;
//import com.meibanlu.driver.bean.TaskDetail;
//import com.meibanlu.driver.sql.PositionBean;
//import com.meibanlu.driver.tool.web.SimpleCallBack;
//import com.meibanlu.driver.tool.web.WebInterface;
//import com.meibanlu.driver.tool.web.WebService;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.PrintWriter;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * LocationService
// * Created by lhq on 2017/10/25.
// */
//public class XmLocationTest extends Service {
//    private static LatLng oldLatLng;//旧的经纬度,用于距离大于10米时候上传经纬度
//    private static int moveDistance = 10;//有效的移动距离，大于10米时上传当前的位置信息
//    private static int maxMoveDistance = 300;//有效的移动距离，大于10米时上传当前的位置信息
//    private static int times;
//    private static long lastTime;
//    static Timer timer;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        startLocation();
//        setTimer(10000);
//        flags = START_FLAG_REDELIVERY;
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    public static void setXmLocation(final AMapLocation aMapLocation) {
//        if (aMapLocation != null && aMapLocation.getLatitude() != 0) {
//            if (!CommonData.isRun || times % 2 == 0) { //7秒运算一次
//                //上传位置
//                CommonData.aMapLocation = aMapLocation;//赋值给地图那边用的
//                //打卡
//                if (CommonData.holderTrip == null || (CommonData.holderTrip != null && !CommonData.holderTrip.isStartSign())) {
//                    initTripId();//获取tripId打卡信息,根据时间更新班次信息
//                    if (CommonData.holderTrip != null && !CommonData.isRun) {
//                        CommonData.changeLocation = true;
//                        CommonData.isRun = true;
//                        locationStyle();
//                    }
//                }
//                if (CommonData.holderTrip != null) {
//                    final String position = aMapLocation.getLongitude() + "," + aMapLocation.getLatitude();
//                    //boolean isIn = GpsTool.checkPointInPolygon(position, "104.057281,30.554748;104.074533,30.551791;104.075477,30.540666;104.061272,30.541997");
//                    if (CommonData.holderTrip.isStartSign()) {
//                        if (!CommonData.holderTrip.isEndSign()) {
//                            driverEndSign(aMapLocation); //到站打卡判断
//                        }
//                        if (!CommonData.holderTrip.isEndSign()) {//到站后不上传经纬度
//                            LatLng currentLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
//                            if (oldLatLng == null) {
//                                positionUpload(position);//出发成功，实时位置上传
//                                oldLatLng = currentLatLng;
//                            } else {
//                                double distance = AMapUtils.calculateLineDistance(oldLatLng, currentLatLng);
//                                if (distance > moveDistance && distance < maxMoveDistance) {
////                                        GpsUtil.remindDeviation(position); //轨迹是否偏离
//                                    positionUpload(position);
//                                    oldLatLng = currentLatLng;
//                                }
//                            }
//                        } else if (CommonData.holderTrip.isServiceEndSign()) {
//                            driverStartOrEndSignToService("2");
//                        }
//                        if (!CommonData.holderTrip.isServiceStartSign()) {
//                            driverStartOrEndSignToService("1");
//                        }
//                    } else {
//                        driverStartSign(aMapLocation);//司机打卡
//                    }
//                }
//            }
//            if (RunMapActivity.getInstance() != null) {
//                //地图打开了
//                RunMapActivity.getInstance().setLocation(aMapLocation);
//            }
//            insertPositionSqlite(aMapLocation);
//        }
//        stopLocation();//停止运行
//        locationStyle();//定位方式
//        times++;
//    }
//
//    private void startLocation() {
//        T.log("start");
////        locationClient.startLocation();
//    }
//
//    private static void stopLocation() {
//        if (!CommonData.openApp) {
//            T.log("stop");
//        }
////
////            locationClient.stopLocation();
////            onDestroy();
//////            RouteTool.getInstance().stopTrace();//停止轨迹纠偏
////        }
//    }
//
//    /**
//     * 判断哪个时间点打卡
//     */
//    private static void initTripId() {
//        if (CommonData.listTask != null && CommonData.listTask.size() > 0) {
//            String time = TimeTool.getCurrentTime("yyyy-MM-dd HH:mm:ss");
//            if (!TextUtils.isEmpty(time)) {
//                for (int i = 0; i < CommonData.listTask.size(); i++) {
//                    TaskDetail list = CommonData.listTask.get(i);
//                    if (list.getLine() != null) {
//                        boolean isTrueTime = getDistanceTimes(time, list.getLine().getSignStartTime(), list.getLine().getSignTimeRange());
//                        if (isTrueTime && (CommonData.holderTrip == null || (CommonData.holderTrip != null && CommonData.holderTrip.getSignPosition() != i))) {
//                            initCommon(i);
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * 实时上传位置
//     *
//     * @param position 104.45,30.26487
//     */
//    private static void positionUpload(String position) {
//        Map<String, Object> param = new HashMap<>();
//        param.put("position", position);
//        param.put("tripId", CommonData.holderTrip.getTripId());
//        param.put("driverName", CommonData.holderTrip.getTrip().getDriverName());
//        param.put("carNumber", CommonData.holderTrip.getTrip().getCarNumber());
//        param.put("phone", CommonData.holderTrip.getTrip().getDriverTel());
//        WebService.doRequest(WebService.POST, WebInterface.UPLOAD_GPS, param, new SimpleCallBack() {
//            @Override
//            public void success(int code, String message, String data) {
//            }
//        });
//    }
//
//    /**
//     * 司机出发打卡
//     */
//    private static void driverStartSign(AMapLocation aMapLocation) {
//        boolean isIn = GpsTool.checkPointInPolygon(aMapLocation.getLatitude(), aMapLocation.getLongitude(), CommonData.holderTrip.getStartLatlng(), CommonData.holderTrip.getStartRadius());
//        if (!CommonData.holderTrip.isStartIn()) {
//            if (isIn) {
//                CommonData.holderTrip.setStartIn(true); //出发进入范围
//            }
//        } else {
//            if (!isIn) {
//                CommonData.holderTrip.setStartOut(true); //出发走出范围
//            }
//        }
//        if (CommonData.holderTrip.isStartIn() && CommonData.holderTrip.isStartOut()) {
//            CommonData.holderTrip.setStartSign(true); //出发打卡成功
//            CommonData.holderTrip.setStartSignLatLng(new LatLng(CommonData.aMapLocation.getLatitude(), CommonData.aMapLocation.getLongitude()));
//            driverStartOrEndSignToService("1");
////            RouteTool.getInstance().startTrace();//开始纠偏
//        }
//    }
//
//    /**
//     * 司机终点签到打卡
//     */
//    private static void driverEndSign(AMapLocation aMapLocation) {
//        boolean isIn = GpsTool.checkPointInPolygon(aMapLocation.getLatitude(), aMapLocation.getLongitude(), CommonData.holderTrip.getEndLatlng(), CommonData.holderTrip.getEndRadius());
//        if (isIn) {
//            CommonData.holderTrip.setEndSignTimes(CommonData.holderTrip.getEndSignTimes() + 1);
//        }
//        if (CommonData.holderTrip.getEndSignTimes() >= 2) {
//            CommonData.holderTrip.setEndSign(true);
////            CommonData.holderTrip.setEndSignLatLng(UtilTool.aMapLocationToLatlng(CommonData.aMapLocation));//设置终点打卡经纬度
////            FileTool.addStrToTxt(RouteTool.fileName, ";" + UtilTool.aMapLocationToString(CommonData.aMapLocation));//加入最后一个点的经纬度
////            RouteTool.getInstance().stopTrace();//停止轨迹纠偏
//            CommonData.changeLocation = true;
//            CommonData.isRun = false;
////            GpsUtil.clearRouteRange(); //路径范围置空
////            TraceTool traceTool = new TraceTool();//轨迹纠偏
////            traceTool.startTrace();
////            RouteUpload.uploadFileType(RouteUpload.UPLOAD_TXT); //上传完成轨迹的txt文档
//            driverStartOrEndSignToService("2");
//        }
//    }
//
//    /**
//     * 发车到站打卡
//     *
//     * @param signType signType   1：出发   2：到站
//     */
//
//    private static void driverStartOrEndSignToService(final String signType) {
//        Map<String, Object> param = new HashMap<>();
//        param.put("tripId", CommonData.holderTrip.getTripId());
//        param.put("status", signType);
//        WebService.doRequest(WebService.POST, WebInterface.DRIVER_SIGN, param, new SimpleCallBack() {
//            @Override
//            public void success(int code, String message, String data) {
//                if (code == 0) {
//                    if (signType.equals("1")) {
//                        XmPlayer.getInstance().playTTS(CommonData.holderTrip.getTrip().getDepartStation().getName() + T.getStringById(R.string.start_sign_success));
//                        UtilTool.changeListTaskState(CommonData.holderTrip.getSignPosition());
//                        refreshSignMarker(1);
//                        CommonData.holderTrip.setServiceStartSign(true); //出发打卡
////                        GpsUtil.setRouteRange(data); //路径范围
//                    } else {
//                        XmPlayer.getInstance().playTTS(CommonData.holderTrip.getTrip().getArriveStation().getName() + T.getStringById(R.string.end_sign_success));
//                        CommonData.holderTrip.setServiceEndSign(true); //到站服务器打卡
//                        setTimer(10000);
//                        dealEndSignTimeOut();
//                        refreshSignMarker(2);
//                        CommonData.holderTrip = null;//将打卡班次设置为空
//                        UtilTool.refreshTodayTask();//刷新首页列表
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
//    private static void initCommon(int i) {
//        CommonData.holderTrip = new HolderTrip();
//        CommonData.holderTrip.setSignPosition(i);
//        CommonData.holderTrip.setTrip(CommonData.listTask.get(i));
//    }
//
//    /**
//     * 获取对应的站点
//     * 判断在不在时间打卡范围
//     *
//     * @param str1 时间参数 当前时间 当前时间：2017-09-28 12:00:00
//     * @param str2 时间参数   发车时间：2017-09-28 12:00
//     */
//    @TargetApi(Build.VERSION_CODES.N)
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
//        return min < timeRange;
//    }
//
//    /**
//     * 处理终点打卡时间超出下一班发车时间
//     */
//    private static void dealEndSignTimeOut() {
//        if (CommonData.holderTrip.getSignPosition() < CommonData.listTask.size() - 1) {
//            int nextPosition = CommonData.holderTrip.getSignPosition() + 1;
//            if (CommonData.listTask.get(nextPosition).getLine() != null) {
//                boolean timeOut = TimeTool.compareTime(CommonData.listTask.get(nextPosition).getLine().getSignStartTime());
//                if (timeOut) {
//                    initCommon(nextPosition);
//                }
//            }
//        }
//    }
//
//    /**
//     * 刷新打卡标记
//     */
//    private static void refreshSignMarker(int signCode) {
//        if (signCode == 1) {
//            if (RunMapActivity.getInstance() != null && CommonData.holderTrip != null) {
//                RunMapActivity.getInstance().addStartSignMarker();
//            }
//        } else if (signCode == 2) {
//            if (RunMapActivity.getInstance() != null) {
//                RunMapActivity.getInstance().addEndSignMarker();
//            }
//        }
//    }
//
//    /**
//     * 改变定位模式
//     */
//    private static void locationStyle() {
//        if (CommonData.changeLocation) {
//            if (CommonData.isRun) {
//                T.log("精确定位");
//                setTimer(3000);
////                MapUtil.setMapClient(locationClient, this, 3500, true); //设定Map的参数
//            } else {
//                T.log("省电定位");
//                setTimer(10000);
////                MapUtil.setMapClient(locationClient, this, 10000, false); //设定Map的参数
//            }
//            CommonData.changeLocation = false;
//        }
//    }
//
//    /**
//     * 3秒插入位置信息到数据库
//     *
//     * @param aMapLocation aMapLocation
//     */
//    private static void insertPositionSqlite(AMapLocation aMapLocation) {
//        if (CommonData.holderTrip != null && CommonData.holderTrip.isStartSign() && !CommonData.holderTrip.isEndSign() && !TextUtils.isEmpty(CommonData.holderTrip.getTripId())) {
//            PositionBean positionBean = new PositionBean();
//            positionBean.setTripId(Integer.parseInt(CommonData.holderTrip.getTripId()));
//            positionBean.setLatitude(aMapLocation.getLatitude());
//            positionBean.setLongitude(aMapLocation.getLongitude());
//            positionBean.setSpeed(aMapLocation.getSpeed());
//            positionBean.setBearing(aMapLocation.getBearing());
//            positionBean.setTime(aMapLocation.getTime());
//            UtilTool.getDbManager().insertPosition(positionBean);
//        }
//    }
//
//    /**
//     * 3秒插入位置信息到数据库
//     *
//     * @param aMapLocation aMapLocation
//     */
//    private void insertTestPositionSqlite(AMapLocation aMapLocation) {
//        PositionBean positionBean = new PositionBean();
//        positionBean.setTripId(1008611250);
//        positionBean.setLatitude(aMapLocation.getLatitude());
//        positionBean.setLongitude(aMapLocation.getLongitude());
//        positionBean.setSpeed(aMapLocation.getSpeed());
//        positionBean.setBearing(aMapLocation.getBearing());
//        positionBean.setTime(aMapLocation.getTime());
//        UtilTool.getDbManager().insertPosition(positionBean);
//        T.showShort(aMapLocation.getLatitude() + ":" + aMapLocation.getLongitude() + ":" + aMapLocation.getSpeed() + ":" + aMapLocation.getBearing());
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    public static void getGps() {
//        InputStream is = null;
//        PrintWriter out = null;
//        ByteArrayOutputStream baos = null;
//        try {
//            HttpURLConnection connection = (HttpURLConnection) new URL("https://www.meibanlu.com/meibanlu_cms/cms/getGps").openConnection();
//            connection.setReadTimeout(18 * 1000);
//            connection.setConnectTimeout(18 * 1000);
//            connection.setDoInput(true);
//            connection.setRequestMethod("GET");
//            // 读取服务器的响应
//            T.log(connection.getResponseCode() + "返回值");
//            if (connection.getResponseCode() == 200) {
//                // 获取输入流
//                is = connection.getInputStream();
//                // 读取数据
//                baos = new ByteArrayOutputStream();
//                int len;
//                byte[] buf = new byte[2048];
//                while ((len = is.read(buf)) != -1) {
//                    baos.write(buf, 0, len);
//                }
//                String resultStr = baos.toString();
//                //生成回调对象，执行回调
//                JSONObject result = new JSONObject(resultStr);
//                //适应重载方法
//                String data;
//                //有时data数据不存在
//                try {
//                    data = result.getString("data");
//                } catch (JSONException json) {
//                    data = "";
//                }
//                int code = result.getInt("code");
//                if (code == 200) {
//                    if (CommonData.aMapLocation != null) {
//                        String[] latlng = data.split(",");
//                        CommonData.aMapLocation.setLatitude(Double.parseDouble(latlng[1]));
//                        CommonData.aMapLocation.setLongitude(Double.parseDouble(latlng[0]));
//                        setXmLocation(CommonData.aMapLocation);
//                    }
//                }
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (baos != null)
//                    baos.close();
//                if (is != null)
//                    is.close();
//                if (out != null)
//                    out.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void setTimer(int time) {
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
////                if (RunMapActivity.getInstance() != null) {
////                    RunMapActivity.getInstance().showTime(System.currentTimeMillis() - lastTime);
////                }
//                lastTime = System.currentTimeMillis();
//                T.log("3333333333");
//                getGps();
//            }
//        }, 0, time);
//    }
//}
