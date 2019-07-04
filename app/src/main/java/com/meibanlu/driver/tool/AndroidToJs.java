package com.meibanlu.driver.tool;

import android.app.Activity;
import android.webkit.JavascriptInterface;

// 继承自Object类
public class AndroidToJs extends Object {

    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void back() {
        Activity activity = ActivityControl.getCurrentActivity();
        activity.finish();
    }
}