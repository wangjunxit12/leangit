package com.meibanlu.driver.broadcast;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.meibanlu.driver.service.UpdateService;
import com.meibanlu.driver.tool.ActivityControl;
import com.meibanlu.driver.tool.Constants;
import com.meibanlu.driver.tool.FilePath;
import com.meibanlu.driver.tool.UtilTool;

import java.io.File;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 全局广播
 * Created by lhq on 2016/9/19.
 */
public class DialogReceiver extends BroadcastReceiver {

    private Activity curActivity;
    static DialogReceiver dialogReceiver;

    /**
     * 接收广播
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        dialogReceiver = this;
        curActivity = ActivityControl.getCurrentActivity();
        int broadcastMark = intent.getIntExtra("broadcastMark", 0);
        switch (broadcastMark) {
            case Constants.INSTALL_APK:
                if (curActivity != null) {
                    curActivity.stopService(new Intent(curActivity, UpdateService.class));
                    NotificationManager manager = (NotificationManager) curActivity.getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(Constants.UPDATE_NOTIFICATION_ID);
                    UtilTool.installApk(FilePath.getAppRoute() + File.separator + "driver.apk");
                }
                break;
        }
    }

}
