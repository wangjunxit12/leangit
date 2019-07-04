package com.meibanlu.driver.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.meibanlu.driver.R;
import com.meibanlu.driver.application.DriverApplication;
import com.meibanlu.driver.tool.UtilTool;

public class BusService extends Service{

    private static final String TAG=BusService.class.getSimpleName();

    private static BusService instance;


    public static BusService getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        startForegroundService();
        return super.onStartCommand(intent,flags,startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopForeground(true);
        DriverApplication.getApplication().onServiceDestroy();
    }

    public static Intent  createIntent(Context context) {
        return new Intent(context, BusService.class);
    }

   public void startForegroundService(){
       startForeground(110, buildNotification());// 开始前台服务
   }

    private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;
    @SuppressLint("NewApi")
    public Notification buildNotification() {
        Notification.Builder builder = null;
        Notification notification = null;
        if(Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = getPackageName();
            if(!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        builder.setSmallIcon(R.mipmap.ic_app_logo)
                .setContentTitle(UtilTool.getAppName(this))
                .setContentText("正在后台运行")
                .setWhen(System.currentTimeMillis());
        notification = builder.build();
        return notification;
    }


}
