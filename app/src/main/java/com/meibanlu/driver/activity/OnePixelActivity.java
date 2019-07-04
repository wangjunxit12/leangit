package com.meibanlu.driver.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class OnePixelActivity extends FragmentActivity {
    //注册广播接受者   当屏幕开启结果成功结束一像素的activity  
    BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设定一像素的activity  
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
        //在一像素activity里注册广播接受者    接受到广播结束掉一像素  
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("TAG", "--------OnePixelActivity finish");
                finish();
            }
        };
        registerReceiver(br, new IntentFilter("finish activity"));

        checkScreenOn("onCreate");

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Log.d("TAG", "--------OnePixelActivity onDestroy");
        try {
            unregisterReceiver(br);
        } catch (IllegalArgumentException e) {
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkScreenOn("onResume");
    }

    /**
     * 检查锁屏     解屏的方法    isScreenOn为true   则表示屏幕“亮”了  否则屏幕“暗”了。    亮着就结束1像素
     */
    private void checkScreenOn(String methodName) {
        Log.d("TAG", "-------from call method: " + methodName);
        PowerManager pm = (PowerManager) OnePixelActivity.this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        Log.d("TAG", "-------isScreenOn: " + isScreenOn);
        if (isScreenOn) {
            finish();
        }
    }
}  
