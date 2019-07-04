package com.meibanlu.driver.tool.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.meibanlu.driver.activity.HomePageActivity;
import com.meibanlu.driver.activity.PersonalActivity;
import com.meibanlu.driver.activity.PersonalNoticeActivity;
import com.meibanlu.driver.activity.PushMessageActivity;
import com.meibanlu.driver.sql.DbHelper;
import com.meibanlu.driver.sql.MessageBean;
import com.meibanlu.driver.tool.TimeTool;
import com.meibanlu.driver.tool.UtilTool;
import com.meibanlu.driver.tool.XmPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
//			Logger.d(TAG, "[PushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//				Logger.d(TAG, "[PushReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//				Logger.d(TAG, "[PushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                insertMessage(bundle);//加入数据库
                processCustomMessage();
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//				Logger.d(TAG, "[PushReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//				Logger.d(TAG, "[PushReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//				Logger.d(TAG, "[PushReceiver] 用户点击打开了通知");
                //打开自定义的Activity
                Intent i = new Intent(context, PushMessageActivity.class);
                i.putExtra("msgId", bundle.getString(JPushInterface.EXTRA_MSG_ID));
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
//				Logger.d(TAG, "[PushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//				Logger.w(TAG, "[PushReceiver]" + intent.getAction() +" connected state change to "+connected);
            } else {
//				Logger.d(TAG, "[PushReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    //Logger.i(TAG, "This message has no Extra data");
                    continue;
                }


                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
//					Logger.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }


    //send msg to 2个activity
    private void processCustomMessage() {
        if (HomePageActivity.isForeground) {
            HomePageActivity.getInstance().showMessage();
        }
        if (PersonalActivity.isForeground) {
            PersonalActivity.refreshAdapter();
        }
        if (PersonalNoticeActivity.isForeground) {
            PersonalNoticeActivity.refreshMessage();
        }

    }

    /**
     * 向数据库插入消息
     *
     * @param bundle bundle
     */
    private void insertMessage(Bundle bundle) {
        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
        if (!ExampleUtil.isEmpty(extra)) {
            MessageBean msgBean = new Gson().fromJson(extra, MessageBean.class);
            String receiveTime = TimeTool.getCurrentTime("yyyy/MM/dd HH:mm");
            String msgId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            msgBean.setReceiveTime(receiveTime);
            msgBean.setMsgId(msgId);
            UtilTool.getDbManager().insert(msgBean);
            UtilTool.setMessageNumber();//刷新未接收消息个数
            XmPlayer.getInstance().playTTS(msgBean.getInformation());//tts播放信息
        }
    }

}
