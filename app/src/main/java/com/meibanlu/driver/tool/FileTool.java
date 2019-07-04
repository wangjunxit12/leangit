package com.meibanlu.driver.tool;

import com.meibanlu.driver.bean.FileBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import static com.baidu.tts.tools.FileTools.deleteFile;

/**
 * FileTool文件管理
 * Created by lhq on 2017/9/25.
 */

public class FileTool {
    public static File createFile(String strFilePath) {
        File file = new File(strFilePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
                return file;
            } catch (Exception e) {
                T.log("createError" + e);
            }
        }
        return null;
    }

    /**
     * 清空文件内容
     *
     * @param filePath filePath文件路径
     */
    static void deleteTxt(String filePath) {
        try {
            FileOutputStream out = new FileOutputStream(filePath, false);
            out.write("".getBytes());
            out.close();
        } catch (Exception ignored) {
            T.log("ignored" + ignored);
        }
    }

    public List<FileBean> getFileList(String strPath) {
        File dir = new File(strPath);
        List<FileBean> fileList = new ArrayList<>();
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i].getAbsolutePath()); // 获取文件绝对路径
                } else {
                    String filePath = files[i].getAbsolutePath();
                    String fileName = files[i].getName();
                    FileBean fileBean = new FileBean();
                    fileBean.setFileName(fileName);
                    fileBean.setFilePath(filePath);
                    fileList.add(fileBean);
                }
            }
        }
        return fileList;
    }

    /**
     * 文件重命名
     *
     * @param oldFilePath oldFilePath 旧的路径
     * @param newFilePath newFilePath 新的路径
     */
    public static void reNameFile(String oldFilePath, String newFilePath) {
        File oldFile = new File(oldFilePath);
        File newFile = new File(newFilePath);
        oldFile.renameTo(newFile);
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static void delete(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            deleteFile(fileName);
        }
    }

    /**
     * 向txt文件中追加字符串
     *
     * @param fileName fileName
     * @param content  content
     */
    public static void addStrToTxt(String fileName, String content) {
        if (fileName == null) {
            T.log("FileTool.addStrToTxt failed, because of filename is null");
            return;
        }
        File file = new File(fileName);
        if (file.exists() && content != null) {
            try {
                // 打开一个随机访问文件流，按读写方式
                RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
                // 文件长度，字节数
                long fileLength = randomFile.length();
                // 将写文件指针移到文件尾。
                randomFile.seek(fileLength);
                randomFile.writeBytes(content);
                randomFile.close();
            } catch (Exception e) {
                T.log("addStrToTxt" + e);
            }
        }
    }

    public static void writeTxt(String strContent, String filePath) {
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
