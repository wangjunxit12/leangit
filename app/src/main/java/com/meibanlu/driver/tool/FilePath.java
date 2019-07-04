package com.meibanlu.driver.tool;

import android.os.Environment;

import java.io.File;

/**
 * FilePath文件路径
 * Created by lhq on 2017/9/25.
 */

public class FilePath {
    //文件夹的根路径 需要通过计算，所以使用私有变量
    private static String fileRootPath;
    private static final String ROUTE_RECODE = "/route";
    private static final String APP_ROUTE = "/down";

    private static String getFileRootPath() {
        if (fileRootPath == null) {
            fileRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com.driver"; //使用域名作文件夹名
//            File baseFilePath = new File(fileRootPath);
//            if (!baseFilePath.exists()) {
//                try {
//                    baseFilePath.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

        }
        return fileRootPath;
    }

    /**
     * GPS的Txt文件路径
     *
     * @return routePath
     */
    public static String getRoutePath() {
        return getFileRootPath() + ROUTE_RECODE;
    }

    //下载app的路径
    public static String getAppRoute() {
        return getFileRootPath() + APP_ROUTE;
    }
}
