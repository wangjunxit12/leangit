package com.meibanlu.driver.tool;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.meibanlu.driver.R;


/**
 * lhq
 * Created by lhq on 2016/12/21.
 */

public class UntilPopWindow {
    public static PopupWindow getPopWindow(View view) {
        PopupWindow popWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, CommonData.windowWidth / 2);
        //设置popWindow可以点击
        popWindow.setFocusable(true);
        //设置背景半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        popWindow.setBackgroundDrawable(dw);
        //设置显示和消失的动画；
        popWindow.setAnimationStyle(R.style.popStyle);
        //点击外面消失
        popWindow.setOutsideTouchable(true);
        //popWindow消失监听方法
        return popWindow;
    }

    public static PopupWindow getBackPopWindow(View view) {
        final Activity mActivity = ActivityControl.getCurrentActivity();
        UtilTool.backgroundAlpha(mActivity, 0.5f);
        PopupWindow popWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, CommonData.windowWidth / 2);
        //设置popWindow可以点击
        popWindow.setFocusable(true);
        //设置背景半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        popWindow.setBackgroundDrawable(dw);
        //设置显示和消失的动画；
        popWindow.setAnimationStyle(R.style.popStyle);
        //点击外面消失
        popWindow.setOutsideTouchable(true);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //透明
                UtilTool.backgroundAlpha(mActivity, 1f);
            }
        });
        //popWindow消失监听方法
        return popWindow;
    }

    public static PopupWindow getPop(View view) {
        final Activity mActivity = ActivityControl.getCurrentActivity();
        UtilTool.backgroundAlpha(mActivity, 0.5f);
        PopupWindow popWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //设置popWindow可以点击
        popWindow.setFocusable(true);
        //设置背景半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        popWindow.setBackgroundDrawable(dw);
        //设置显示和消失的动画；
//        popWindow.setAnimationStyle(R.style.popStyle);
        //点击外面消失
        popWindow.setOutsideTouchable(true);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //透明
                UtilTool.backgroundAlpha(mActivity, 1f);
            }
        });
        //popWindow消失监听方法
        return popWindow;
    }

    public static PopupWindow getPopWindow(View viewBottom, final Activity mActivity) {
        //黑色半透明背景
        UtilTool.backgroundAlpha(mActivity, 0.5f);
        PopupWindow popWindow = new PopupWindow(viewBottom, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //设置popWindow可以点击
        popWindow.setFocusable(true);
        //设置背景半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        popWindow.setBackgroundDrawable(dw);
        //设置显示和消失的动画；
        popWindow.setAnimationStyle(R.style.popStyle);
        //点击外面消失
        popWindow.setOutsideTouchable(true);
        //popWindow消失监听方法
        //popWindow消失监听方法
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //透明
                UtilTool.backgroundAlpha(mActivity, 1f);
            }
        });
        return popWindow;
    }


}
