package com.meibanlu.driver.tool;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * WriteText 写入数据
 * Created by lhq on 2017/9/20.
 */

class WriteText {
    void startWrite(String strContent, String filePath) {
        //生成文件夹之后，再生成文件，不然会出错
        File file = new File(filePath);
        if (file.exists()) {
            FileTool.deleteTxt(filePath);
            try {
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                raf.seek(file.length());
                raf.write(strContent.getBytes());
                raf.close();
            } catch (Exception e) {
                T.log("Error on write File:" + e);
            }
        }
    }

}
