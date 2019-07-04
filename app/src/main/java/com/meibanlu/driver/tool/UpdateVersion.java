package com.meibanlu.driver.tool;

/**
 * 版本更新
 * Created by lhq on 2016/10/9.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.meibanlu.driver.application.DriverApplication;
import com.meibanlu.driver.bean.VersionUpdateBean;
import com.meibanlu.driver.service.UpdateService;
import com.meibanlu.driver.tool.web.CallBack;
import com.meibanlu.driver.tool.web.WebInterface;
import com.meibanlu.driver.tool.web.WebService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.meibanlu.driver.tool.XMDialog.CLICK_SURE;


public class UpdateVersion {
    private int versionCodeLocal;
    private final int VERSION_UPDATE = 0xff;
    Context context;
    private String downloadUrl /*= Urls.BASE_URL + "apk/fsrsm4.apk"*/;

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //版本更新
                case VERSION_UPDATE:
                    String result = msg.getData().getString("versionUpdate");
                    VersionUpdateBean versionUpdateData = new Gson().fromJson(result, VersionUpdateBean.class);
                    if (versionUpdateData.getData() == null) {
                        return;
                    }
                    downloadUrl = versionUpdateData.getData().getFilePath();
                    if (!TextUtils.isEmpty(downloadUrl)) {

                        if (versionCodeLocal<Integer.valueOf(versionUpdateData.getData().getVersionCode())) {
                            //判断本地是否有文件，是否可以直接安装，两个条件
                            //如果没有的话提示是否更新
//                            if (!dealApkDownComplete()) {
                            showUpdateDialog();
//                            }
                        }
                    }
                    break;
            }


        }
    };

    /**
     * 查看本地是否有安装文件，如果有已经下载好的，就不需要从服务器下载了
     *
     * @return
     */
    private boolean dealApkDownComplete() {
        //文件名确定
        final String filePath = FilePath.getAppRoute() + File.separator + "driver.apk";
        if (new File(filePath).exists() &&
                SharePreData.getInstance().getStrData("isComplete") != null) {
            XMDialog.showDialog("更新提示", "您好，新的安卓包已经下载完成，是否立即安装？", XMDialog.NO_TITLE, new XMDialog.DialogResult() {
                @Override
                public void clickResult(int resultCode) {
                    if (resultCode == CLICK_SURE) {
                        // 安装
                        UtilTool.installApk(filePath);
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    public void update() {
        versionCodeLocal = getLocalVersion();
//        getVersionData();
    }


    public UpdateVersion(Context context) {
        this.context = context;
    }

    //版本更新信息
//    private void getVersionData() {
//        Map<String, Object> param = new HashMap<>();
//        WebService.doRequest(WebService.GET, WebInterface.VERSION_UPDATE, param, new CallBack() {
//            @Override
//            public void success(int code, String message, String data) {
//
//            }
//
//            @Override
//            public void success(String result) {
//                Message msg = new Message();
//                msg.what = VERSION_UPDATE;
//                Bundle bundle = new Bundle();
//                bundle.putString("versionUpdate", result);
//                msg.setData(bundle);
//                handle.sendMessage(msg);
//            }
//
//            @Override
//            public void error(String responseMessage) {
//
//            }
//        });
//
//
//    }

    /**
     * 显示软件更新对话框
     */
    private void showUpdateDialog() {
        XMDialog.showDialog("更新提示", "您好检测到新的版本，是否更新到最新版本", XMDialog.NO_TITLE, new XMDialog.DialogResult() {
            @Override
            public void clickResult(int resultCode) {
                if (resultCode == CLICK_SURE) {
                    // 显示下载对话框
                    Intent i = new Intent(context, UpdateService.class);
                    i.putExtra("url", downloadUrl);
                    context.startService(i);
                }
            }
        });


    }

    public static int getLocalVersion() {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = DriverApplication.getApplication()
                    .getPackageManager()
                    .getPackageInfo(DriverApplication.getApplication().getPackageName(), 0);
            localVersion = packageInfo.versionCode;
            Log.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }
}
