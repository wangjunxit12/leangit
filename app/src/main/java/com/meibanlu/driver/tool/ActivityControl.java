package com.meibanlu.driver.tool;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * activity控制类
 * Created by lhq on 2016/9/19.
 */
public class ActivityControl {
    static List<Activity> mActivityList = new ArrayList<>();
    private static Activity mCurrentActivity;

    public static void addActivity(Activity activity) {
        if (!mActivityList.contains(activity)) {
            mActivityList.add(activity);
        }
        setCurrentActivity(activity);
    }

    public static void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    public static void setCurrentActivity(Activity activity) {
        mCurrentActivity = activity;
    }

    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public static void finishAll() {
        for (Activity activity : mActivityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 关闭除了登陆的接口
     */
    public static void finishExceptLogin() {
        for (Activity activity : mActivityList) {
            if (!activity.isFinishing() && !activity.getLocalClassName().equals("activity.UserLoginActivity")) {
                activity.finish();
            }
        }
    }

    public static void finishWithHome() {
        for (Activity activity : mActivityList) {
            if (!activity.isFinishing() && !activity.getLocalClassName().equals("activities.HomeActivity")) {
                activity.finish();
            }
        }
    }

    public static void finishChoiseActivity() {
        for (Activity activity : mActivityList) {
            if (activity.getLocalClassName().equals("activities.DestinationActivity") || activity.getLocalClassName().equals("activities.PlanSearchActivity")) {
                activity.finish();
            }
        }
    }

}
