package com.meibanlu.driver.tool;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * TimeTool时间的工具类
 * Created by lhq on 2017/9/25.
 */

public class TimeTool {
    /**
     * 获取当前时间
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime(String timeStyle) {
        //yyyy-MM-dd HH:mm:ss 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat(timeStyle);
        return df.format(new Date());
    }

    /**
     * 获取明天时间
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTomorrowTime(String timeStyle) {
        //yyyy-MM-dd HH:mm:ss 设置日期格式
        Date date = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        SimpleDateFormat df = new SimpleDateFormat(timeStyle);
        return df.format(date);
    }

    /**
     * 将时间戳转换为时间
     * longTime 时间戳
     * timeStyle 时间格式
     */
    public static String stampToDate(String longTime, String timeStyle) {
        if (TextUtils.isEmpty(longTime)) {
            return "";
        }
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeStyle, Locale.CHINA);
        long lt = Long.valueOf(longTime);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 将时间转换为时间戳
     * longTime 时间戳     180118115244
     * timeStyle 时间格式 yyMMddHHmmss
     */
    public static long dateToTime(String time, String style) {
        if (time != null) {
            SimpleDateFormat format = new SimpleDateFormat(style);
            try {
                Date date = format.parse(time);
                return date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 将时间戳转换为时间
     * longTime 时间戳
     * timeStyle 时间格式
     */
    public static String stampToDate(Long longTime, String timeStyle) {
        if (longTime == null) {
            return "";
        }
        return new SimpleDateFormat(timeStyle, Locale.CHINA).format(new Date(longTime));
    }

    /**
     * @param longTime 60，     80  分钟
     * @return 小时加分钟
     */
    public static String minToHour(String longTime) {
        int intTime = Integer.parseInt(longTime);
        String time;
        if (intTime > 60) {
            int hour = intTime / 60;
            int min = intTime - hour * 60;
            time = hour + "小时" + min + "分钟";
        } else {
            time = longTime + "分钟";
        }
        return time;
    }

    /**
     * 比较时间差
     * <p>
     * strTime1 时间参数 汽车到站时间  到站打卡成功时间：2017-09-28 12:00:00
     *
     * @param strTime2 汽车下一班发车时间：2017-09-28 12:00
     * @return 是否超过发车时间
     */
    public static boolean compareTime(String strTime2) {
        String strTime1 = getCurrentTime("yyyy-MM-dd HH:mm:ss");//当前时间
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date one = df.parse(strTime1);
            Date two = df.parse(strTime2 + ":00");
            long time1 = one.getTime();
            long time2 = two.getTime();
            if (time1 > time2 || time1 == time2) {
                return true;
            }
        } catch (ParseException ignored) {
        }
        return false;
    }

    /**
     * 时间差
     *
     * @param timeFirst  时间参数 当前时间 当前时间：12:00
     * @param timeSecond 时间参数   发车时间：12:00
     * @return 分钟数
     */
    static int getDistanceTimes(String timeFirst, String timeSecond) {
        int r = time2Int(timeFirst) - time2Int(timeSecond);
        return r > 0 ? r : -r;
    }

    /**
     * 时间差
     *
     * @param timeFirst  时间参数 当前时间 当前时间：12:00
     * @param timeSecond 时间参数   发车时间：12:00
     * @return 分钟数
     */
    public static int getDistanceTimesSign(String timeFirst, String timeSecond) {
        return time2Int(timeFirst) - time2Int(timeSecond);
    }

    /**
     * 时间差 --- 秒数
     *
     * @param timeFirst  时间参数 当前时间 当前时间：12:00:00
     * @param timeSecond 时间参数   发车时间：12:00:11
     * @return 秒数
     */
    static int getDistanceSecondsSign(String timeFirst, String timeSecond) {
        return time2Int(timeFirst) - time2Int(timeSecond);
    }

    /**
     * 时间转换成小数
     *
     * @param time 12:30
     * @return 12.5
     */
    private static int time2Int(String time) {
        return Integer.valueOf(time.split(":")[0]) * 60 + Integer.valueOf(time.split(":")[1]);
    }
}
