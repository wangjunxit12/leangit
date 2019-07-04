package com.meibanlu.driver.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.meibanlu.driver.R;
import com.meibanlu.driver.tool.*;
import com.meibanlu.driver.tool.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class UpdateService extends Service {
    private NotificationManager notificationManager;
    private Notification notification;
    private UploadHandler uploadHandler;
    private int download_percent = 0;
    private RemoteViews views;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder xmBuilder = new Notification.Builder(this);
        //设置任务栏中下载进程显示的views
        views = new RemoteViews(getPackageName(), R.layout.notification_update);
        xmBuilder.setSmallIcon(R.mipmap.ic_app_logo)
                .setTicker(getString(R.string.app_name) + " "+ getString(R.string.software_updates))
                .setContentTitle(getString(R.string.notice))
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContent(views);
        notification = xmBuilder.build();
        //将下载任务添加到任务栏中
//        nm.notify(notificationId, notification);
        uploadHandler = new UploadHandler(Looper.myLooper(), this);
//        //启动线程开始执行下载任务
        if (intent == null) {
            notificationManager.cancel(Constants.UPDATE_NOTIFICATION_ID);
            stopSelf();
        } else {
            downFile(intent.getStringExtra("url"));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //下载更新文件
    private void downFile(final String downloadUrl) {
        new Thread() {
            public void run(){
                try {
                    // 判断SD卡是否存在，并且是否具有读写权限
                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        //获取路径
                        String mSavePath = FilePath.getAppRoute();
                        File file = new File(mSavePath);
                        // 判断文件目录是否存在
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        if (TextUtils.isEmpty(mSavePath)) {
                            return;
                        }
                        URL url = new URL(downloadUrl);
                        // 创建连接
                        HttpURLConnection conn = (HttpURLConnection) url
                                .openConnection();
                        conn.connect();
                        // 获取文件大小
                        int length = conn.getContentLength();
                        // 创建输入流
                        InputStream is = conn.getInputStream();
                        String fileName = "driver.apk";
                        File apkFile = new File(mSavePath, fileName);
                        FileOutputStream fos = new FileOutputStream(apkFile);
                        // 显示文件大小格式：2个小数点显示
                        DecimalFormat df = new DecimalFormat("0.00");
                        // 进度条下面显示的总文件大小
                        String apkFileSize = df.format((float) length / 1024 / 1024) + "MB";
                        int count = 0;
                        // 缓存
                        byte buf[] = new byte[1024];
                        // 写入到文件中
                        uploadHandler.sendEmptyMessage(3);
                        do {
                            int numRead = is.read(buf);
                            count += numRead;
                            // 进度条下面显示的当前下载文件大小
                            String tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
                            // 计算进度条位置
                            int percent = (int) (((double) count / length) * 100);
                            // 更新进度
                            if (percent - download_percent >= 3) {
                                download_percent = percent;
                                uploadHandler.sendEmptyMessage(3);
                            }

                            if (numRead <= 0) {
                                // 下载完成
                                uploadHandler.sendEmptyMessage(4);
                                break;
                            }
                            // 写入文件
                            fos.write(buf, 0, numRead);
                        } while (true);// 点击取消就停止下载.
                        fos.close();
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /*事件处理类*/
    class UploadHandler extends Handler {
        private Context context;

        public UploadHandler(Looper looper, Context c) {
            super(looper);
            this.context = c;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case 0:
                        T.showShort(msg.obj.toString());
                        break;
                    case 1:
                        break;
                    case 2:
                        //下载完成后清除所有下载信息，执行安装提示
                        download_percent = 0;
                        notificationManager.cancel(Constants.UPDATE_NOTIFICATION_ID);
                        //安装
                        //停止掉当前的服务
                        stopSelf();
                        break;
                    case 3:
                        //更新状态栏上的下载进度信息
                        views.setTextViewText(R.id.tv_upload, T.getStringById(R.string.have_down) + download_percent + "%");
                        views.setProgressBar(R.id.pb_upload, 100, download_percent, false);
                        notification.contentView = views;
                        notificationManager.notify(Constants.UPDATE_NOTIFICATION_ID, notification);
                        break;
                    case 4:
                        //设置下载完成标志
                        SharePreData.getInstance().addStrData("isComplete", "yes");
                        //下载完成
                        views.setTextViewText(R.id.tv_upload, getString(R.string.install_apk));
                        views.setViewVisibility(R.id.pb_upload, View.GONE);
                        notification.contentView = views;
                        Intent intentClick = new Intent("com.meibanlu.driver.DialogReceiver");
                        intentClick.putExtra("broadcastMark", Constants.INSTALL_APK);
                        notification.contentIntent = PendingIntent.getBroadcast(context, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);
                        notificationManager.notify(Constants.UPDATE_NOTIFICATION_ID, notification);
                        //对话框崩溃处理，直接安装
//                        XMDialog.showDialog(getString(R.string.install_prompt), getString(R.string.down_finish_install),XMDialog.HAVE_TITLE, new XMDialog.DialogResult() {
//                            @Override
//                            public void clickResult(int resultCode) {
//                                if (resultCode == XMDialog.CLICK_SURE) {
//                                    notificationManager.cancel(Constants.UPDATE_NOTIFICATION_ID);
//                                    //安装
//                                    //停止掉当前的服务
//                                    stopSelf();
//                                    UtilTool.installApk(FilePath.getAppRoute() + File.separator + "driver.apk");
//                                }
//                            }
//                        });
                        notificationManager.cancel(Constants.UPDATE_NOTIFICATION_ID);
                        //安装
                        //停止掉当前的服务
                        stopSelf();
                        UtilTool.installApk(FilePath.getAppRoute() + File.separator + "driver.apk");
                        break;
                }
            }
        }
    }

}