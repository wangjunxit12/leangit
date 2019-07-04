package com.meibanlu.driver.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.meibanlu.driver.tool.CommonData;
import com.meibanlu.driver.tool.NetManager;

/**
 * NetBroadcastReceiver 网络广播接收
 * Created by lhq on 2018-01-19.
 */

public class NetBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            CommonData.netAccess = NetManager.isOpenNetwork();
            Log.i("NetBroadcastReceiver"," CommonData.netAccess"+ CommonData.netAccess);
        }
    }
}
