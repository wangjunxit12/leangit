package com.meibanlu.driver.tool;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.meibanlu.driver.bean.FileBean;
import com.meibanlu.driver.tool.web.SimpleCallBack;
import com.meibanlu.driver.tool.web.WebInterface;
import com.meibanlu.driver.tool.web.WebService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RouteUpload 路线上传
 * Created by lhq on 2017/9/26.
 */

public class RouteUpload {
    private static final int UPLOAD_SUCCESS = 1;
    public static final int UPLOAD_TXT = 2;//上传,路线运行完成的文件
    public static final int UPLOAD_FAIL_TXT = 3;//上传失败文件
    private static final int MAX_TXT_NUMBER = 7; //最大文件数
    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            T.showShort("上传成功");
            ActivityControl.finishAll();
            System.exit(0);
        }
    };

    public static void uploadFileType(int uploadType) {
        FileTool fileTool = new FileTool();
        List<FileBean> fileList = fileTool.getFileList(FilePath.getRoutePath()); //所有的文件
        List<String> notUpload = new ArrayList<>();//未上传成功的文件  5.txt
        List<String> uploadSuccess = new ArrayList<>();//上传成功的文件  5.txt.1
        List<String> upLoadFailure = new ArrayList<>();//上传错误的文件  1709260921_5.txt
        for (FileBean item : fileList) {
            if (item.getFilePath().contains("_")) {
                upLoadFailure.add(item.getFilePath());
            } else if (item.getFilePath().endsWith(".txt")) {
                notUpload.add(item.getFilePath());
            } else if (item.getFilePath().contains(".txt.")) {
                uploadSuccess.add(item.getFilePath());
            }
        }
        switch (uploadType) {
            case UPLOAD_TXT: //上传完成轨迹文件
                for (String upTxtPath : notUpload) {
//                    routeUpload(uploadSuccess, upTxtPath, UPLOAD_TXT);
                }
                break;
            case UPLOAD_FAIL_TXT: //上传失败的文件
                for (String upTxtPath : upLoadFailure) {
//                    routeUpload(uploadSuccess, upTxtPath, UPLOAD_FAIL_TXT);
                }
                break;

        }
    }


//    private static void routeUpload(final List<String> uploadSuccess, final String upTxtPath, int uploadType) {
//        if (TextUtils.isEmpty(upTxtPath)) {
//            return;
//        }
//        final List<String> files = new ArrayList<>();//文件集合
//        files.add(upTxtPath);
//        Map<String, String> param = new HashMap<>();
//        final String tripId;
//        if (uploadType == UPLOAD_TXT) { //上传完成轨迹文件
//            String[] path = upTxtPath.split("/");
//            String name = path[path.length - 1];
//            tripId = name.replace(".txt", "");
//        } else { //上传失败的文件
//            String[] pathFail = upTxtPath.split("/");
//            String nameFail = pathFail[pathFail.length - 1];
//            String strTimeTripId = nameFail.replace(".txt", "");
//            String[] arrTimeTripId = strTimeTripId.split("_");
//            tripId = arrTimeTripId[1];
//        }
//
//        //加个距离,加上一个直径
//        double distance = 800;
//        String textContent = ReadText.read(upTxtPath);
//        if (TextUtils.isEmpty(textContent)) {
//            return;
//        }
//        String[] arrLatlng = textContent.split(";");
//        for (int i = 1; i < arrLatlng.length; i++) {
//            String[] latlng = arrLatlng[i].split(",");
//            String[] oldLatlng = arrLatlng[i - 1].split(",");
//            if (latlng.length == 2 && oldLatlng.length == 2) {
//                double pointDistance = AMapDistanceUtil.calculateLineDistance(Double.parseDouble(oldLatlng[0]), Double.parseDouble(oldLatlng[1]), Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
//                if (pointDistance < 500) {//防止两个点的经纬度偏离银河系
//                    distance += pointDistance;
//                }
//            }
//        }
//        int intDistance = (int) distance;
//        param.put("distance", intDistance + "");
//        param.put("tripId", tripId);
//        WebService.uploadFiles(WebInterface.UPLOAD_TXT, "file", files, param, new
//                SimpleCallBack() {
//                    @Override
//                    public void success(int code, String message, String data) {
//                        if (code == 0) {//上传成功
//                            if (uploadSuccess.size() >= MAX_TXT_NUMBER) {
//                                for (String filePath : uploadSuccess) { //删除第一个
//                                    if (filePath.endsWith(".txt.1")) {
//                                        FileTool.delete(filePath);
//                                        uploadSuccess.remove(filePath);
//                                        break;
//                                    }
//                                }
//                                for (String filePath : uploadSuccess) {//txt文件名减一
//                                    String pathNumber = filePath.substring(filePath.length() - 1); //最后一个数字  123/45/1709260921_5.txt.1
//                                    int nowNumber = Integer.parseInt(pathNumber) - 1;
//                                    String newFilePath = filePath.substring(0, filePath.length() - 1) + nowNumber;
//                                    FileTool.reNameFile(filePath, newFilePath);
//                                }
//                            }
//                            String newFilePath = upTxtPath.replace("error_", "");
//                            FileTool.reNameFile(upTxtPath, newFilePath + "." + (uploadSuccess.size() + 1)); //新加入的命名
////                            handler.sendEmptyMessage(UPLOAD_SUCCESS);
//                        } else {//上传失败
//                            String newFileName = FilePath.getRoutePath() + "/" + "error" + "_" + tripId + ".txt";
//                            FileTool.reNameFile(upTxtPath, newFileName);
//                        }
//                    }
//                });
//    }
}
