package com.meibanlu.driver.tool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.meibanlu.driver.application.DriverApplication;

import static com.meibanlu.driver.tool.T.context;

/**
 * 网络管理类，判断网络连接情况
 */
public class NetManager {

    // 判断网络是否可用的方法
    public static boolean isOpenNetwork() {
        ConnectivityManager connectivity = (ConnectivityManager) DriverApplication.getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    // 判断WIFI网络是否可用的方法
    public boolean isOpenWifi() {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static boolean connect() {

        // 得到连接管理器对象
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager)  DriverApplication.getApplication()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                    return true;
                } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                    return true;
                }
            } else {
                return false;
            }
        }else{
            ConnectivityManager connMgr = (ConnectivityManager) DriverApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //通过循环将网络信息逐个取出来
            for (Network network : networks) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                if (networkInfo != null && networkInfo.isConnected()) {
                    if (networkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                        return true;
                    } else if (networkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                        return true;
                    }
                } else {
                    return false;
                }
            }


        }
        return false;
    }
}
