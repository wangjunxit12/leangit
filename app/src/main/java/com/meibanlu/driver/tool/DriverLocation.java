package com.meibanlu.driver.tool;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.meibanlu.driver.R;
import com.meibanlu.driver.activity.RollingScopeActivity;
import com.meibanlu.driver.activity.RunMapActivity;
import com.meibanlu.driver.application.DriverApplication;
import com.meibanlu.driver.bean.PositionVo;

/**
 * LocationService
 *
 * @author lhq
 * @date 2017/10/25
 */
public class DriverLocation implements AMapLocationListener {
    private static DriverLocation driverLocation;
    @SuppressLint("StaticFieldLeak")
    private static AMapLocationClient locationClient;

    private DriverLocation() {
        if (locationClient == null) {
            locationClient = new AMapLocationClient(DriverApplication.getApplication());
            //创建PowerManager对象
        }
        //设定Map的参数
        MapUtil.setMapClient(locationClient, this);
        startLocation();
    }

    public static DriverLocation getInstance() {
        if (driverLocation == null) {
            driverLocation = new DriverLocation();
        }
        return driverLocation;
    }

    @Override
    public void onLocationChanged(final AMapLocation aMapLocation) {
        Log.i("driver-roll","onLocationChanged");
        if (aMapLocation != null) {
            CommonData.locationErrorCode = aMapLocation.getErrorCode();

            StringBuffer sb = new StringBuffer();
            sb.append("定位成功" + "\n");
            sb.append("定位类型: " + aMapLocation.getLocationType() + "\n");
            sb.append("经    度    : " + aMapLocation.getLongitude() + "\n");
            sb.append("纬    度    : " + aMapLocation.getLatitude() + "\n");
            sb.append("精    度    : " + aMapLocation.getAccuracy() + "米" + "\n");
            //解析定位结果，
            String result = sb.toString();
            Log.i("driver-roll",result);

            if (aMapLocation.getErrorCode() == 0&&(aMapLocation.getLocationType()==AMapLocation.LOCATION_TYPE_GPS||
                    aMapLocation.getLocationType()==AMapLocation.LOCATION_TYPE_WIFI||
                    aMapLocation.getLocationType()==AMapLocation.LOCATION_TYPE_SAME_REQ||
                    aMapLocation.getLocationType()==AMapLocation.LOCATION_TYPE_CELL)&&aMapLocation.getAccuracy()<500) {
                RxBus.getInstance().post(new AMapLocation(aMapLocation));
                //处理当前点是否有效
                CommonData.aMapLocation = aMapLocation;
                CommonData.judgePosition = new PositionVo(aMapLocation);
//                CommonData.judgePosition = RectifyLocationUtils.rectify(aMapLocation, System.currentTimeMillis());
                if (RollingScopeActivity.getInstance() != null) {
                    //滚动范围打开
                    RollingScopeActivity.getInstance().setLocation(aMapLocation);
                }
                ScheduleTaskDaemon.startGpsTask();
            }else {
                Log.i("driver-roll","aMapLocation.getErrorCode():"+aMapLocation.getErrorCode());
            }
        }
        if (!CommonData.openApp) {
            stopLocation();//停止运行
        }
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        Log.i("driver-roll","startLocation");
        if(locationClient==null){
            locationClient= new AMapLocationClient(DriverApplication.getApplication());
            MapUtil.setMapClient(locationClient, this);
        }
        locationClient.startLocation();
    }

    /**
     * 停止定位
     */
    private static void stopLocation() {
        if(locationClient!=null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
            driverLocation = null;
            locationClient = null;
        }
    }

    /**
     * 重新定位
     */
    public static void reStartLocation() {
        stopLocation();
        getInstance().startLocation();
    }

    public void startBackGroundLocation(){
        if(locationClient!=null){
            locationClient.enableBackgroundLocation(2001, buildNotification());
        }
    }

    public void stop(){
        if(locationClient!=null){
            locationClient.disableBackgroundLocation(true);
        }
    }


    private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;
    @SuppressLint("NewApi")
    public Notification buildNotification() {
        Notification.Builder builder = null;
        Notification notification = null;
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) DriverApplication.getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = DriverApplication.getApplication().getPackageName();
            if(!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(DriverApplication.getApplication(), channelId);
        } else {
            builder = new Notification.Builder(DriverApplication.getApplication());
        }
        builder.setSmallIcon(R.mipmap.ic_app_logo)
                .setContentTitle(UtilTool.getAppName(DriverApplication.getApplication()))
                .setContentText("正在后台运行")
                .setWhen(System.currentTimeMillis());
        notification = builder.build();
        return notification;
    }
}