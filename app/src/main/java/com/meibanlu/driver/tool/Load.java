package com.meibanlu.driver.tool;

import android.app.Activity;
import android.util.Log;

import com.meibanlu.driver.R;
import com.meibanlu.driver.view.loading.ShapeLoadingDialog;

/**
 * 加载中
 * Created by lhq on 2017/9/14.
 */

public class Load {
    private static Load load;
    /**
     * loading相关
     */
    private static ShapeLoadingDialog xmLoading; //弹出框

    private Load(Activity Activity) {
        initLoading(Activity);
    }

    public static Load getInstance(Activity activity) {
        Activity oldActivity = ActivityControl.getCurrentActivity();
        if (load == null || !activity.equals(oldActivity)) {
            load = new Load(activity);
        }
        return load;
    }

    public static Load getInstance() {
        Activity activity = ActivityControl.getCurrentActivity();
        load = new Load(activity);
        return load;
    }

    private void initLoading(Activity activity) {
        xmLoading = new ShapeLoadingDialog(activity);
        xmLoading.setCanceledOnTouchOutside(false);
        xmLoading.setLoadingText(T.getStringById(R.string.loading));
    }


    /**
     * 显示正在加载对话框
     */
    public void showLoading() {
        Log.i("T","showLoading:  "+ActivityControl.getCurrentActivity().toString());
        xmLoading.show();
    }

    /**
     * 显示加载对话框，并且改变文字内容
     *
     * @param title
     */
    public void showLoading(String title) {
        xmLoading.setLoadingText(title);
        xmLoading.show();
    }

    /**
     * 隐藏加载对话框
     */
    public static void hideLoading() {
        if (xmLoading != null) {
            xmLoading.dismiss();
        }
    }
}
