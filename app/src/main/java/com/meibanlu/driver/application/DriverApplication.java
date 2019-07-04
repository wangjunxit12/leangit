package com.meibanlu.driver.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.meibanlu.driver.BaseManagerInterface;
import com.meibanlu.driver.OnPollingListener;
import com.meibanlu.driver.OnTimerListener;
import com.meibanlu.driver.tool.CommonData;
import com.meibanlu.driver.tool.NetManager;
import com.meibanlu.driver.tool.ScheduleTaskDaemon;
import com.meibanlu.driver.tool.UtilTool;
import com.meibanlu.driver.tool.XmPlayer;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

import static com.baidu.tts.loopj.AsyncHttpClient.LOG_TAG;

/**
 * DriverApplication
 * Created by lhq on 2017/9/13.
 */

public class DriverApplication extends Application {
    private static DriverApplication application;
    private Map<Class<? extends BaseManagerInterface>, Collection<? extends BaseManagerInterface>> managerInterfaces;

    private boolean closing;

    /**
     * Whether {@link #onServiceDestroy()} has been called.
     */
    private boolean closed;


    public DriverApplication() {
        managerInterfaces = new HashMap<>();
        registeredManagers = new ArrayList<>();

        handler = new Handler();
        closing = false;
        closed = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        initData();
        setWindowSize();
        addManagers();
        startTimer();
    }

    private void initData() {
        CrashReport.initCrashReport(getApplicationContext(), "949f883561", false);
//        CustomActivityOnCrash.install(this);//bug提示
        UtilTool.setMessageNumber();//刷新未接收的消息数
        CommonData.netAccess = NetManager.isOpenNetwork();//是否允许访问网络
        XmPlayer.getInstance();//初始化语音播放
    }


    /**
     * 初始化部分数据
     */
    private void init() {
        application = this;
        JPushInterface.init(this);            // 初始化 JPush
//        Intent serviceIntent = new Intent(this,BusService.class);
//        startService(serviceIntent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * 获取全局的 DriverApplication
     *
     * @return DriverApplication
     */
    public static DriverApplication getApplication() {
        return application;
    }

    /**
     * 官方推荐获取屏幕宽高的方法
     */
    private void setWindowSize() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        CommonData.windowWidth = dm.widthPixels;
        CommonData.WindowHeight = dm.heightPixels;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    /**
     * Submits request to be executed in UI thread.
     */
    public void runOnUiThread(final Runnable runnable) {
        handler.post(runnable);
    }

    /**
     * Submits request to be executed in UI thread.
     */
    public void runOnUiThreadDelay(final Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }
    public void addManagers() {
        addManager(UtilTool.getInstance());
        addManager(ScheduleTaskDaemon.getInstance());

    }


    /**
     * Register new manager.
     */
    private void addManager(Object manager) {
        registeredManagers.add(manager);
    }


    private final Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            for (OnTimerListener listener : getManagers(OnTimerListener.class)) {
                listener.onTimer();
            }

            if (!closing) {
                startTimer();
            }
        }

    };

    private final Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {
            for (OnPollingListener listener : getManagers(OnPollingListener.class)) {
                listener.onTimer();
            }

            if (!closing) {
                startPolling();
            }
        }

    };
    private final ArrayList<Object> registeredManagers;

    private final Handler handler;
    /**
     * @param  cls Requested class of managers.
     * @return List of registered manager.
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseManagerInterface> Collection<T> getManagers(Class<T> cls) {
        if (closed) {
            return Collections.emptyList();
        }
        Collection<T> collection = (Collection<T>) managerInterfaces.get(cls);
        if (collection == null) {
            collection = new ArrayList<>();
            for (Object manager : registeredManagers) {
                if (cls.isInstance(manager)) {
                    collection.add((T) manager);
                }
            }
            collection = Collections.unmodifiableCollection(collection);
            managerInterfaces.put(cls, collection);
        }
        return collection;
    }

    /**
     * Service have been destroyed.
     */
    public void onServiceDestroy() {
        Log.i(LOG_TAG, "onServiceDestroy");
        if (closed) {
            Log.i(LOG_TAG, "onServiceDestroy closed");
            return;
        }
        onClose();

//        // use new thread instead of run in background to exit immediately
//        // without waiting for possible other threads in executor
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                onUnload();
//            }
//        });
//        thread.setPriority(Thread.MIN_PRIORITY);
//        thread.setDaemon(true);
//        thread.start();
    }

    private void onClose() {
        Log.i(LOG_TAG, "onClose1");
//        for (Object manager : registeredManagers) {
//            if (manager instanceof OnCloseListener) {
//                ((OnCloseListener) manager).onClose();
//            }
//        }
        closed = true;
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
        Log.i(LOG_TAG, "onClose2");
    }

    /**
     * Requests to close application in some time in future.
     */
    public void requestToClose() {
        closing = true;
        Log.i(LOG_TAG, "requestToClose2");
    }

    private void startTimer() {
        runOnUiThreadDelay(timerRunnable, OnTimerListener.DELAY);
    }

    public void startPolling() {
        runOnUiThreadDelay(pollingRunnable, OnPollingListener.DELAY);
    }
}
