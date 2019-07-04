package com.meibanlu.driver.tool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * ReadText 读取text
 * Created by lhq on 2017/9/20.
 */

public class ReadText {
    /**
     * @author chenzheng_java
     * 读取刚才用户保存的内容
     */
    public static String read(String fileName) {
        try {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = "";
            while((line = br.readLine())!=null){
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
