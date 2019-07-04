package com.meibanlu.driver.tool;
/**
 * 记录文件经纬度文件名字是否需要更新
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.meibanlu.driver.application.DriverApplication;

public class SharePreData {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static SharePreData sharePreData;

    //构造方法私有化
    private SharePreData() {
    }

    public static SharePreData getInstance() {
        if (sharePreData == null) {
            //由于注释了MyApplication，需要重新使用一个
            Context context = DriverApplication.getApplication();
            sharePreData = new SharePreData();
            sharePreData.preferences = context.getSharedPreferences("nameMark", Context.MODE_PRIVATE);
            sharePreData.editor = sharePreData.preferences.edit();
            sharePreData.editor.apply();
        }
        return sharePreData;
    }

    public void addStrData(String key, String value) {
        //增加，更新数据
        editor.putString(key, value);
        editor.apply();
    }

    public String getStrData(String key) {
        // 获取文件名字
        return preferences.getString(key, "");
    }

    // 移除share的数据
    public void removeStrData(String key) {
        editor.remove(key);
        editor.apply();
    }

    public void putLong(int key,Long value){
        preferences.edit().putLong(DriverApplication.getApplication().getString(key),value).apply();
    }

    public Long getLong(int key){
        return preferences.getLong(DriverApplication.getApplication().getString(key),-1);
    }
}
