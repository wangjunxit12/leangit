package com.meibanlu.driver.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.meibanlu.driver.application.DriverApplication;
import com.meibanlu.driver.tool.web.SimpleCallBack;
import com.meibanlu.driver.tool.web.WebInterface;
import com.meibanlu.driver.tool.web.WebService;

import java.util.HashMap;
import java.util.Map;


/**
 * Toast统一管理类
 */
public class T {
    @SuppressLint("StaticFieldLeak")
    public static Context context = DriverApplication.getApplication();

    private T() {
    }

    private static Dialog loadingDialog; //进度条的dialog

    /**
     * 短时间显示Toast
     *
     * @param message 显示toast信息
     */
    public static void showShort(CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param message 显示toast信息
     */
    public static void showLong(CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     */
    public static void show(CharSequence message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    /**
     * 打印的信息
     *
     * @param str str，字数大于2000的分条打印
     */
    public static void log(String str) {
        if (str.length() > 2000) {
            for (int i = 0; i < str.length(); i += 2000) {
                if (i + 2000 < str.length())
                    Log.i("driveTest", str.substring(i, i + 2000));
                else
                    Log.i("driveTest", str.substring(i, str.length()));
            }
        } else {
            Log.i("driveTest", str);
        }
    }
    /**
     * 打印的信息
     *
     * @param str str，字数大于2000的分条打印
     */
    public static void logE(String str) {
        if (str.length() > 2000) {
            for (int i = 0; i < str.length(); i += 2000) {
                if (i + 2000 < str.length())
                    Log.e("driveTest", str.substring(i, i + 2000));
                else
                    Log.e("driveTest", str.substring(i, str.length()));
            }
        } else {
            Log.e("driveTest", str);
        }
    }
    public static void toast(final String msg) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                T.showShort(msg);
            }
        });
    }

    //根据id获取文字
    public static String getStringById(int strId) {
        return context.getResources().getString(strId);
    }

    //根据id获取颜色
    public static int getColorById(int colorId) {
        return context.getResources().getColor(colorId);
    }

    protected void initLoading() {
//        xmLoading = new com.meibanlu.driver.view.loading.ShapeLoadingDialog(this);
//        xmLoading.setCanceledOnTouchOutside(false);
//        xmLoading.setLoadingText(getString(RunActivity.string.loading));
    }

    /**
     * activity的跳转
     *
     * @param intentClass 跳转后的activity
     */
    public static void startActivity(Class intentClass) {
        Activity activity = ActivityControl.getCurrentActivity();
        Intent intent = new Intent();
        intent.setClass(activity, intentClass);
        activity.startActivity(intent);
    }

    /**
     * activity的跳转
     *
     * @param intentClass 跳转后的activity
     */
    public static void startLoginActivity(Class intentClass) {
        Activity activity = ActivityControl.getCurrentActivity();
        if(activity==null) return;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(activity, intentClass);
        activity.startActivity(intent);
    }

    /**
     * 加载中
     *
     * @param activity activity
     */
    public static void showLoading(Activity activity) {
        Load.getInstance(activity).showLoading();
    }

    /**
     * 加载中
     *
     * @param activity activity
     * @param str      显示数据
     */
    public static void showLoading(Activity activity, String str) {
        Load.getInstance(activity).showLoading(str);
    }

    /**
     * 加载中
     */
    public static void showLoading() {
        Log.i("T","Activity:  "+ActivityControl.getCurrentActivity().toString());
        Load.getInstance().showLoading();
    }

    /**
     * 加载中
     *
     * @param str 显示数据
     */
    public static void showLoading(String str) {
        Load.getInstance().showLoading(str);
    }

    /**
     * 隐藏加载中
     */
    public static void hideLoading() {
        Load.hideLoading();
    }

//    public static void error(String errorLog) {
//        Map<String, Object> param = new HashMap<>();
//        param.put("errorLog", errorLog);
//        try {
//            WebService.doRequest(WebService.POST, WebInterface.UPLOAD_ERROR, param, new SimpleCallBack() {
//                @Override
//                public void success(int code, String message, String data) {
//                 T.log(data);
//                }
//            });
//        } catch (Exception e) {
//
//        }
//    }
}
