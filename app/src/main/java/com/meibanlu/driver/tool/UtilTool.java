package com.meibanlu.driver.tool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.amap.api.location.AMapLocationQualityReport;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.meibanlu.driver.BuildConfig;
import com.meibanlu.driver.OnTimerListener;
import com.meibanlu.driver.R;
import com.meibanlu.driver.activity.HomePageActivity;
import com.meibanlu.driver.activity.UserLoginActivity;
import com.meibanlu.driver.application.DriverApplication;
import com.meibanlu.driver.bean.Line;
import com.meibanlu.driver.bean.LineStation;
import com.meibanlu.driver.bean.TaskDetail;
import com.meibanlu.driver.bean.TodayTaskBean;
import com.meibanlu.driver.bean.UpdateEvent;
import com.meibanlu.driver.sql.MessageBean;
import com.meibanlu.driver.tool.web.SimpleCallBack;
import com.meibanlu.driver.tool.web.WebInterface;
import com.meibanlu.driver.tool.web.WebService;
import com.meibanlu.driver.tts.MySyntherizer;
import com.meibanlu.driver.webservice.RetrofitGenerator;
import com.meibanlu.driver.webservice.mappers.ScheduleMapper;
import com.meibanlu.driver.webservice.mappers.StationMapper;
import com.meibanlu.driver.webservice.requeset.CheckUserRequest;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.LinesRequest;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.requeset.StationRequest;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 常用的工具类
 * Created by lhq on 2017/9/15.
 */

public class UtilTool implements OnTimerListener{



    public static boolean showLogin;
    private Handler handler = new Handler(Looper.getMainLooper());
    @NonNull
    private CompositeDisposable mCompositeDisposable=new CompositeDisposable();

    private volatile static UtilTool instance;

    private UtilTool(){

    }

    public static UtilTool getInstance(){
        if(instance==null){
            instance=new UtilTool();
        }
        return instance;
    }

//    /**
//     * 字符串转换成数组
//     *
//     * @param str   str
//     * @param split 分隔符
//     * @return String[]
//     */
//    public static String[] strToArray(String str, String split) {
//        if (!TextUtils.isEmpty(str)) {
//            return str.split(split);
//        }
//        return null;
//    }

    public static List<LatLng> strToLat(String strGps) {
        List<LatLng> latlngs = new ArrayList<>();
        if (!TextUtils.isEmpty(strGps)) {
            String[] str = strGps.split(";");
            for (String gps : str) {
                String[] strLatLng = gps.split(",");
                if (strLatLng.length == 2) {
                    LatLng latLng = new LatLng(Double.parseDouble(strLatLng[1]), Double.parseDouble(strLatLng[0]));
                    latlngs.add(latLng);
                }
            }
        }
        return latlngs;
    }


    public static PolylineOptions strToPolyline(String strGps) {
        PolylineOptions polyline = new PolylineOptions();
        if (!TextUtils.isEmpty(strGps)) {
            String[] str = strGps.split(";");
            for (String gps : str) {
                String[] strLatLng = gps.split(",");
                if (strLatLng.length == 2) {
                    LatLng latLng = new LatLng(Double.parseDouble(strLatLng[1]), Double.parseDouble(strLatLng[0]));
                    polyline.add(latLng);
                }
            }
        }
        return polyline;
    }


    public static PolylineOptions listToPolyline(List<LatLng> list) {
        PolylineOptions polyline = new PolylineOptions();
        if (list != null && list.size() > 0) {
            for (LatLng latLng : list) {
                polyline.add(latLng);
            }
        }
        return polyline;
    }

    public static void cleanAll() {
        SharePreData sharePreData = SharePreData.getInstance();
        CommonData.isLogin=false;
        CommonData.holderTrip = null;//当前打卡班次为空
        sharePreData.removeStrData("id");
        sharePreData.removeStrData("name");
        sharePreData.removeStrData("sex");
        sharePreData.removeStrData("token");
        sharePreData.removeStrData("idCard");
        sharePreData.removeStrData("age");
        sharePreData.removeStrData("password");

    }

    //账号被占用
    public static void accountNotUser() {
        cleanAll();
        T.startActivity(UserLoginActivity.class);
        UtilTool.showLogin = true;

//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                XMDialog.showDialog("", "您的账号已经在其他地方登陆，是否重新登陆", "否", "是", XMDialog.NO_TITLE, new XMDialog.DialogResult() {
//                    @Override
//                    public void clickResult(int resultCode) {
//                        if (resultCode == XMDialog.CLICK_SURE) {
//                            cleanAll();
//                            T.startActivity(UserLoginActivity.class);
//                        } else {
//                            UtilTool.finishAll();
//                        }
//                        showLogin = false;
//                        UtilTool.clearExit();//清除班次信息
//                    }
//                });
//            }
//        });
    }

    //账号登录过期
    public static void accountLogin() {
        cleanAll();
        T.startLoginActivity(UserLoginActivity.class);
        UtilTool.showLogin = true;
    }

    /**
     * 处理最后一个字符串
     *
     * @param str str
     * @return str
     */
    static String dealEndStr(StringBuffer str) {
        int strLength = str.length();
        if (strLength > 0) {
            return str.delete(strLength - 1, strLength).toString();
        }
        return "";
    }

    /**
     * 刷新消息未读数
     */
    public static void setMessageNumber() {
        int number = 0;
        List<MessageBean> list = UtilTool.getDbManager().query();
        if (list != null) {
            for (MessageBean msgBean : list) {
                if (msgBean.getIsRead() == 0) {
                    number++;
                }
            }
            CommonData.notReadNumber = number;
        }
    }


    public static void clearExit() {
        CommonData.listTask = null;//所有的打卡信息
        CommonData.holderTrip = null;//当前打卡班次为空
        CommonData.openApp = false;//关闭app
        CommonData.notReadNumber = 0;//消息未读
        CommonData.aMapLocation = null;//定位的那个
    }

    /**
     * 刷新今天的任务
     *
     * @param taskBean taskBean
     */
    private static void initTodayTaskAdapter(TodayTaskBean taskBean) {
        final List<TaskDetail> itemTask = taskBean.getTrips();
        if (itemTask.size() > 0) {
            String carNumber = itemTask.get(0).getCarNumber();
            if (!TextUtils.isEmpty(carNumber)) {
                SharePreData.getInstance().addStrData("carNumber", carNumber);
            }
            for (TaskDetail item : itemTask) {
                Line line = item.getLine();
                if (line != null){
                    line.setSignStartTime(item.getDate() + " " + item.getSchedule());
//                    if (TextUtils.isEmpty(item.getLine().getElapsedTime())) {
//                        item.getLine().setSignTimeRange(Constants.TRIP_TOLERATE_TIME);
//                    } else {
//                        int signTimeRange = Integer.parseInt(item.getLine().getElapsedTime()) / 10 + 1;
//                        if (signTimeRange < Constants.TRIP_TOLERATE_TIME) {
//                            signTimeRange = Constants.TRIP_TOLERATE_TIME;
//                        }
//                        item.getLine().setSignTimeRange(signTimeRange);
//                    }
                }
                //处理当前班次
                if (item.getStatus() == Constants.STATUS_DEPART) {
                    CommonData.holderTrip = item.toHolderTrip(true);
                }

                if(!isGoing(itemTask)){
                    if(CommonData.holderTrip!=null){
                        CommonData.holderTrip=null;
                    }
                }
            }
            Collections.sort(itemTask);
            CommonData.listTask = removeDuplicateWithOrder(itemTask);
        }
        changeHomeUi();
    }

    /**
     * 刷新首页界面
     */
    public static void changeHomeUi() {
//        RxBus.getInstance().post(new UpdateEvent());
        if (HomePageActivity.getInstance() != null) {
            //列表打开
            HomePageActivity.getInstance().refreshTask();
        }
    }



//    /**
//     * 刷新今天任务
//     */
//    public static void refreshTodayTask() {
//
//        WebService.doRequest(WebService.GET, WebInterface.TODAY_TASK, null, new SimpleCallBack() {
//            @Override
//            public void success(int code, String message, String data) {
//                if (code == 0) {
//                    TodayTaskBean taskBean = new Gson().fromJson(data, TodayTaskBean.class);
//                    initTodayTaskAdapter(taskBean);
//                }
//            }
//        });
//    }

    /**
     * 改变状态
     */
    static void changeListTaskState(int position) {
        if (CommonData.listTask != null) {
            CommonData.listTask.get(position).setStatus(1);
            changeHomeUi();
        }
    }


    /**
     * 画足迹的线
     *
     * @param latLngs 点集合
     */
    public static PolylineOptions getPolyline(List<LatLng> latLngs) {
        List<BitmapDescriptor> texTrueList = new ArrayList<>();
        texTrueList.add(BitmapDescriptorFactory.fromResource(R.mipmap.map_alr));
        //指定某一段用某个纹理，对应texTrueList的index即可, 四个点对应三段颜色
        List<Integer> texIndexList = new ArrayList<>();
        texIndexList.add(0);//对应上面的第0个纹理
        PolylineOptions options = new PolylineOptions();
        options.width(20);//设置宽度
        options.geodesic(true);
        //加入2个点
        for (int i = 0; i < latLngs.size(); i++) {
            options.add(latLngs.get(i));
        }
        //加入对应的颜色,使用setCustomTextureList 即表示使用多纹理；
        options.setCustomTextureList(texTrueList);
        //设置纹理对应的Index
        options.setCustomTextureIndex(texIndexList);
        return options;
    }

//    public static String aMapLocationToString(AMapLocation aMapLocation) {
//        if (aMapLocation == null) {
//            return "";
//        } else {
//            return aMapLocation.getLongitude() + "," + aMapLocation.getLatitude();
//        }
//    }
//
//    public static LatLng aMapLocationToLatlng(AMapLocation aMapLocation) {
//        if (aMapLocation == null) {
//            return null;
//        } else {
//            return new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
//        }
//    }

    /**
     * 获取当前经纬度度
     *
     * @return LatLng
     */
    public static String getCurrentLatLng() {
        if (CommonData.aMapLocation == null) {
            return null;
        } else {
            return CommonData.aMapLocation.getLongitude() + ";" + CommonData.aMapLocation.getLatitude();
        }
    }

    /**
     * 操作数据库
     *
     * @return manager
     * 记得close
     */
    public static DBManager getDbManager() {
        DBManager manager = DBManager.getInstance();
        manager.openDb();
        return manager;
    }

    /**
     * 安装apk
     *
     * @param filePath f文件路径
     */
    public static void installApk(String filePath) {

        File apkFile = new File(filePath);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(ActivityControl.getCurrentActivity(), BuildConfig.APPLICATION_ID + ".fileProvider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }


        // 通过Intent安装APK文件
//        i.setDataAndType(Uri.parse("file://" + apkFile.toString()),
//                "application/vnd.android.package-archive");
        ActivityControl.getCurrentActivity().startActivity(intent);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 终点到站打卡
     *
     * @param context context
     */
    public static void endManSign(Context context) {
        String prompt = T.getStringById(R.string.abnormal_arrive_sign);
        XmPlayer.getInstance().playTTS(prompt);
        final HolderTrip holderTrip = CommonData.holderTrip;
        if (holderTrip == null) {
            T.showShort("打卡异常");
            return;
        }
        if (!ScheduleTaskDaemon.driveEndSign(holderTrip, CommonData.judgePosition, Constants.MODE_MAN_SUCCESS)) {
            XMDialog.showDialog(context, prompt, new XMDialog.DialogResult() {
                @Override
                public void clickResult(int resultCode) {
                    if (resultCode == XMDialog.CLICK_SURE) {
                        ScheduleTaskDaemon.sign(holderTrip, Constants.STATUS_ARRIVE, null, null, Constants.MODE_MAN_ABNORMAL);
                    }
                }
            });
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public static void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    /**
     * activity的跳转
     *
     * @param intentClass 跳转后的activity
     */
    public static void startActivity(Class intentClass, String... data) {
        Activity activity = ActivityControl.getCurrentActivity();
        Intent intent = new Intent();
        intent.setClass(activity, intentClass);
        for (int i = 0; i < data.length; i += 2) {
            intent.putExtra(data[i], data[i + 1]);
        }
        activity.startActivity(intent);
    }

    public static String getPseudo(){
        String  m_szDevIDShort = "35"+//we make this look like a valid IMEI
                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10+
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10+
                Build.TYPE.length()%10+
                Build.USER.length()%10; //13 digits
        T.log("Build.DISPLAY"+Build.DISPLAY);
        T.log("Build.DISPLAY.length()"+ Build.DISPLAY.length());
        return m_szDevIDShort;
    }

    /**
     * 获取手机IMEI
     *
     * @param
     * @return
     */
    public static  String getIMEI() {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) DriverApplication.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            @SuppressLint({"MissingPermission", "HardwareIds"})
            String imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getDeviceID() {
        String deviceId = "";
        String imei1 = "";
        String imei2 = "";
        TelephonyManager mTelephonyMgr = null;
        try {
            mTelephonyMgr = (TelephonyManager) DriverApplication.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
            imei1 = getDoubleImei(mTelephonyMgr, "getDeviceIdGemini", 0);
            imei2 = getDoubleImei(mTelephonyMgr, "getDeviceIdGemini", 1);
        } catch (Exception e) {
            try {
                imei1 = getDoubleImei(mTelephonyMgr, "getDeviceId", 0);
                imei2 = getDoubleImei(mTelephonyMgr, "getDeviceId", 1);
            } catch (Exception ex) {
                Log.e("UtilTool", "get device id fail: " + e.toString());
            }
        }

        if (!TextUtils.isEmpty(imei1) && !TextUtils.isEmpty(imei2)) {
            deviceId = imei1 + "," + imei2;
        } else if (!TextUtils.isEmpty(imei1)) {
            deviceId = imei1;
        } else if (!TextUtils.isEmpty(imei2)) {
            deviceId = imei2;
        } else if (mTelephonyMgr != null) {
            try {
                deviceId = mTelephonyMgr.getDeviceId();
            } catch (Exception e) {
                Log.i("UtilTool", "mTelephonyMgr.getDeviceId() fail" + e.toString());
            }
        }
        return deviceId;
    }

    /**
     * 获取双卡手机的imei
     */
    private static String getDoubleImei(TelephonyManager telephony, String predictedMethodName, int slotID) throws Exception {
        String inumeric = null;

        Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
        Class<?>[] parameter = new Class[1];
        parameter[0] = int.class;
        Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
        Object[] obParameter = new Object[1];
        obParameter[0] = slotID;
        Object ob_phone = getSimID.invoke(telephony, obParameter);
        if (ob_phone != null) {
            inumeric = ob_phone.toString();
        }
        return inumeric;
    }

    /**
     * 获取app的名称
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        String appName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            appName =  context.getResources().getString(labelRes);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return appName;
    }

    /**判断程序是否在后台
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static int REQUEST_SETTING_NOTIFICATION=1;
    public static boolean isNotificationEnabled(){
        NotificationManagerCompat notification = NotificationManagerCompat.from(DriverApplication.getApplication());
        return notification.areNotificationsEnabled();
    }
    public static void goToNotificationSetting(Activity activity) {
        if(activity==null) return;
        ApplicationInfo appInfo = DriverApplication.getApplication().getApplicationInfo();
        String pkg = DriverApplication.getApplication().getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent();
                //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, pkg);
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, uid);
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                }else {
                    //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.putExtra("app_package", pkg);
                    intent.putExtra("app_uid", uid);
                }
                activity.startActivityForResult(intent, REQUEST_SETTING_NOTIFICATION);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_SETTING_NOTIFICATION);
            } else {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                activity.startActivityForResult(intent, REQUEST_SETTING_NOTIFICATION);
            }
        } catch (Exception e) {
            // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
            Intent intent = new Intent();

            //下面这种方案是直接跳转到当前应用的设置界面。
            //https://blog.csdn.net/ysy950803/article/details/71910806
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivityForResult(intent, REQUEST_SETTING_NOTIFICATION);
        }
    }
    //可以全局控制是否打印log日志
    private static boolean isPrintLog = true;
    private static int LOG_MAXLENGTH = 2000;

    public static void i(String tagName, String msg) {
        if (isPrintLog) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.i(tagName + i, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.i(tagName + i, msg.substring(start, strLength));
                    break;
                }
            }
        }

    }

    private Map<Integer, TaskDetail> map = new HashMap<>();
    private int index = 0;
    public void getSchedules() {
        if(!NetManager.connect()){
            DriverApplication.getApplication().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    T.showShort(T.getStringById(R.string.network_error));
                    XmPlayer.getInstance().playTTS(T.getStringById(R.string.network_error));
                }
            });
            return;
        }
        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        LinesRequest request=new LinesRequest();
        Header header=new Header();
        request.cityNumber=SharePreData.getInstance().getStrData("id");
        request.date=TimeTool.getCurrentTime("yyyy-MM-dd");
        body.linesRequest=request;
        envelope.header=header;
        envelope.body=body;
        mCompositeDisposable.add(RetrofitGenerator.getInstance().getApiStore().getSchedules(envelope)

//                .flatMap(new Function<List<TaskDetail>, ObservableSource<TaskDetail>>() {
//                    @Override
//                    public ObservableSource<TaskDetail> apply(List<TaskDetail> taskDetail) throws Exception {
//                        index=0;
//                        return Observable.fromIterable(taskDetail);
//                    }
//                })
//                .flatMap(new Function<TaskDetail, ObservableSource<ResponseEnvelope>>() {
//                    @Override
//                    public ObservableSource<ResponseEnvelope> apply(TaskDetail taskDetail) throws Exception {
//                        map.put(index, taskDetail);
//                        RequestEnvelope envelope=new RequestEnvelope();
//                        RequestBody body=new RequestBody();
//                        StationRequest request=new StationRequest();
//                        Header header=new Header();
//                        request.setNumber(String.valueOf(taskDetail.getBusId()));
//                        body.siteRequest=request;
//                        envelope.header=header;
//                        envelope.body=body;
//                        return RetrofitGenerator.getInstance().getApiStore().getLineStation(envelope);
//                    }
//                })
//                .map(new Function<ResponseEnvelope, List<LineStation>>() {
//                    @Override
//                    public List<LineStation> apply(ResponseEnvelope responseEnvelope) throws Exception {
//                        return new StationMapper().transform(responseEnvelope);
//                    }
//                })
//                .map(new Function<List<LineStation>, TaskDetail>() {
//                    @Override
//                    public TaskDetail apply(List<LineStation> lineStations) throws Exception {
//                        TaskDetail taskDetail=map.get(index);
//                        for(LineStation station:lineStations){
//                            if(taskDetail.getDepartId()==station.getId()){
//                                taskDetail.setDepartStation(station);
//                            }
//                            if(taskDetail.getArriveId()==station.getId()){
//                                taskDetail.setArriveStation(station);
//                            }
//                        }
//
//                        index++;
//                        return taskDetail;
//                    }
//                })
//                .collect(new Callable<List<TaskDetail>>() {
//                    @Override
//                    public List<TaskDetail> call() throws Exception {
//                        return new ArrayList<>();
//                    }
//                }, new BiConsumer<List<TaskDetail>, TaskDetail>() {
//                    @Override
//                    public void accept(List<TaskDetail> taskDetails, TaskDetail taskDetail) throws Exception {
//                        taskDetails.add(taskDetail);
//                    }
//                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<ResponseEnvelope, List<TaskDetail>>() {
                            @Override
                            public List<TaskDetail> apply(ResponseEnvelope responseEnvelopeResult) throws Exception {
                                return new ScheduleMapper().transform(responseEnvelopeResult);
                            }
                        })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        RxBus.getInstance().post(new UpdateEvent(false));
                    }
                })
                .subscribe(new Consumer<List<TaskDetail>>() {
                    @Override
                    public void accept(List<TaskDetail> schedules) throws Exception {
                        if(schedules!=null&&schedules.size()>0){
                            TodayTaskBean taskBean=new TodayTaskBean();
                            taskBean.setTrips(schedules);
                            initTodayTaskAdapter(taskBean);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        DriverApplication.getApplication().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                T.showShort("获取班次失败，请刷新重新获取");
                            }
                        });
                    }
                }
                )

        );
    }

//    private void getStation(List<TaskDetail> schedules){
//        Disposables
//        mCompositeDisposable.add()
//    }


    @Override
    public void onTimer() {
        if (CommonData.isLogin){
            checkUid();
            checkSchedule();
        }
         if(CommonData.listTask!=null&&CommonData.listTask.size()>0){
             Collections.sort(CommonData.listTask);
         }
    }

    private void checkUid(){
            RequestEnvelope envelope=new RequestEnvelope();
            RequestBody body=new RequestBody();
            CheckUserRequest request=new CheckUserRequest();
            Header header=new Header();
            request.tel= SharePreData.getInstance().getStrData("phone");
            body.userRequest=request;
            envelope.header=header;
            envelope.body=body;
            final Observable<ResponseEnvelope> observable=RetrofitGenerator.getInstance().getApiStore().checkUser(envelope);
            mCompositeDisposable.add(observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseEnvelope>() {
                        @Override
                        public void accept(ResponseEnvelope responseEnvelope) throws Exception {
                                String data=responseEnvelope.body.userResponse.model.value;
                                Log.e("Login","data:   "+data);
                                if(data!=null&&!data.equalsIgnoreCase(UtilTool.getDeviceID().length()==0?UtilTool.getPseudo():UtilTool.getDeviceID())
                                        &&!data.equalsIgnoreCase("error")){
                                    T.show("设备ID错误，更换手机登录请联系车务",4000);
                                    UtilTool.cleanAll();
                                    XmPlayer.getInstance().playTTS("设备ID错误，更换手机登录请联系车务");
                                    T.startLoginActivity(UserLoginActivity.class);
                                }
                        }

                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    })

            );
    }

    private void checkSchedule(){
        if(CommonData.holderTrip!=null){
            if(CommonData.holderTrip.getTrip().getStatus()==Constants.STATUS_PRE_DEPART&&
                    !CommonData.holderTrip.isStartOut()){
                XmPlayer.getInstance().playTTS("您当前存在未打出发卡的班次，请及时打卡");
            }

        }
    }

    public static int getLocalVersion() {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = DriverApplication.getApplication()
                    .getPackageManager()
                    .getPackageInfo(DriverApplication.getApplication().getPackageName(), 0);
            localVersion = packageInfo.versionCode;
            Log.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }


    private static boolean isGoing(List<TaskDetail> itemTask){
        for(TaskDetail taskDetail:itemTask){
            if(taskDetail.getStatus()==Constants.STATUS_DEPART){
                return true;
            }
        }
        return false;
    }
    // 删除ArrayList中重复元素，保持顺序
    public static List<TaskDetail> removeDuplicateWithOrder(List<TaskDetail> list) {
        Set<TaskDetail> set = new HashSet<>();
        List<TaskDetail> newList =new ArrayList<>();
        for (TaskDetail element : list) {
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        return  newList;
    }

}