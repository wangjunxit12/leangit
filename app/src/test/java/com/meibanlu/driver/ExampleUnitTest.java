package com.meibanlu.driver;

import android.location.Location;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.meibanlu.driver.tool.*;
import com.meibanlu.driver.tool.web.SimpleCallBack;
import com.meibanlu.driver.tool.web.WebInterface;
import com.meibanlu.driver.tool.web.WebService;

import org.junit.Test;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.meibanlu.driver.tool.ReadText.read;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getAge() {
        String idCard = "519023199203222222";
        String year = idCard.substring(6, 10);
        System.out.println(year);
    }

    @Test
    public void Test() {
        TimeTool.getCurrentTime("yyyy");
    }

    @Test
    public void Test1() {
        String strGps = "123/45/1709260921_5.txt";
        String[] pathFail = strGps.split("/");
        String nameFail = pathFail[pathFail.length - 1];
        String strTimeTripId = nameFail.replace(".txt", "");
        String[] arrTimeTripId = strTimeTripId.split("_");
        System.out.println(arrTimeTripId[0] + "yyyy" + arrTimeTripId[1]);
    }

    @Test
    public void Test2() {
        String filePath = "123/45/1709260921_5.txt.5";
        String pathNumber = filePath.substring(filePath.length() - 1); //最后一个数字  123/45/1709260921_5.txt.1
        int nowNumber = Integer.parseInt(pathNumber) - 1;
        String newFilePath = filePath.substring(0, filePath.length() - 1) + nowNumber;
        System.out.println(newFilePath);
    }

    @Test
    public void Test3() {
        List<String> listNumber = new ArrayList<>();
        listNumber.add("1");
        listNumber.add("2");
        listNumber.add("3");
        listNumber.add("4");
        listNumber.remove("2");
        System.out.println(listNumber);
    }

    @Test
    public void Test4() {
        String upTxtPath = "fajlsifjlajf/jfsk/error_7.txt";
        String ss = upTxtPath.replace("error_", "");
        System.out.println(ss);
    }

    @Test
    public void Test5() throws ParseException {
        getDistanceTimes1("2017-09-26 08:58:00", "2017-09-26 08:55", 20);
//        TestXiXi("9:20", "9:40");
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {day, hour, min, sec};
        System.out.println(times[2]);
        return times;
    }

    /**
     * 获取对应的站点
     *
     * @param str1 时间参数 当前时间 当前时间：2017-09-28 12:00:00
     * @param str2 时间参数   发车时间：2017-09-28 12:00
     */
    public static boolean getDistanceTimes1(String str1, String str2, int timeRange) {
        String startTime = str2 + ":00";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long min = 0;
        try {
            Date one = df.parse(str1);
            Date two = df.parse(startTime);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            long day = diff / (24 * 60 * 60 * 1000);
            long hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(min);
        if (min < timeRange) {
            return true;
        } else {
            return false;
        }
    }


    public void TestXiXi(String startTime, String endTime) {
        String strStartHour = startTime.split(":")[0];
        String strStartMinute = startTime.split(":")[1];
        String strEndHour = endTime.split(":")[0];
        String strEndMinute = endTime.split(":")[1];
        int startHour = Integer.parseInt(strStartHour);
        int startMinute = Integer.parseInt(strStartMinute);
        int endHour = Integer.parseInt(strEndHour);
        int endMinute = Integer.parseInt(strEndMinute);
        int hour;
        int minute;
        int allMinute;
        hour = endHour < startHour ? endHour + 24 - startHour : endHour - startHour;
        if (hour == 0) {
            minute = endMinute - startMinute;
        } else {
            hour--;
            minute = 60 - startMinute + endMinute;
        }
        allMinute = hour * 60 + minute;
        System.out.println(allMinute);
    }

    @Test
    public void Test6() {
        String lineTime = "10:24~13:25";
        Pattern p = Pattern.compile("(\\d{1,2}):(\\d{1,2})~(\\d{1,2}):(\\d{1,2})");
        Matcher matcher = p.matcher(lineTime);
        int[] times = new int[4];
        if (matcher.matches()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                times[i] = Integer.parseInt(matcher.group(i + 1));
            }
        }
        System.out.println("时间们：" + Arrays.toString(times));
        System.out.println(matcher);
        String startTime = lineTime.split("~")[0];
        String endTime = lineTime.split("~")[1];
        String strStartHour = startTime.split(":")[0];
        String strStartMinute = startTime.split(":")[1];
        String strEndHour = endTime.split(":")[0];
        String strEndMinute = endTime.split(":")[1];
        int startHour = Integer.parseInt(strStartHour);
        int startMinute = Integer.parseInt(strStartMinute);
        int endHour = Integer.parseInt(strEndHour);
        int endMinute = Integer.parseInt(strEndMinute);
        int hour;
        int minute;
        int allMinute, effectiveTime;
        hour = endHour < startHour ? endHour + 24 - startHour : endHour - startHour;
        if (hour == 0) {
            minute = endMinute - startMinute;
        } else {
            hour--;
            minute = 60 - startMinute + endMinute;
        }
        allMinute = hour * 60 + minute;
        if (allMinute < 60) {
            effectiveTime = 6;
        } else {
            effectiveTime = allMinute / 10;
        }
        System.out.println(allMinute);
        System.out.println(effectiveTime);
    }

    @Test
    public void test7() {
        String longTime = "200";
        int intTime = Integer.parseInt(longTime);
        String time;
        if (intTime > 60) {
            int hour = intTime / 60;
            int min = intTime - hour * 60;
            time = hour + "小时" + min + "分钟";
        } else {
            time = longTime + "分钟";
        }
        System.out.println(time);
    }

    @Test
    public void test8() {
        String latlngs = "104.068848,30.546368;104.068871,30.545343;104.068909,30.544142;104.068924,30.543444;104.068977,30.542423;104.068977,30.542309;104.069023,30.541067;104.069038,30.54064;104.069038,30.54064;104.069107,30.538624;104.069107,30.538624;104.069138,30.53721;104.06916,30.53639;104.069191,30.535603;104.069191,30.535603;104.068123,30.535585;104.067871,30.535574;104.067871,30.535574;104.067505,30.535572;104.067017,30.535641;104.066803,30.535702;104.066597,30.535782;104.066376,30.535896;104.06514,30.536682;104.06514,30.536682;104.064621,30.537022;104.064621,30.537022;104.064316,30.537216;104.064316,30.537216;104.064186,30.537304;104.064186,30.537304;104.064095,30.537365;104.064056,30.53739;104.063438,30.537769;104.063248,30.537851;104.063004,30.537922;104.063004,30.537922;104.062744,30.53797;104.062691,30.537975";
        List<LatLng> list1 = new ArrayList<>();
        String[] arrLatlng = latlngs.split(";");
        for (String strLatLng : arrLatlng) {
            String[] a = strLatLng.split(",");
            LatLng latLng = new LatLng(Double.parseDouble(a[1]), Double.parseDouble(a[0]));
            list1.add(latLng);
        }

        StringBuilder addStrGps = new StringBuilder();
        for (LatLng latLng : list1) {
            String context = latLng.longitude + "," + latLng.latitude + ";";
            if (!addStrGps.toString().contains(context)) {
                addStrGps.append(latLng.longitude).append(",").append(latLng.latitude).append(";");
            }
        }
        String strGps = addStrGps.toString();
        String endGps = strGps.substring(0, strGps.length() - 1);
        System.out.println(endGps);
    }

    @Test
    public void test9() {
        MyLatLng A = new MyLatLng(new LatLng(30.670928, 104.05619));
        MyLatLng B = new MyLatLng(new LatLng(30.688201, 104.070266));
        System.out.println(getAngle(A, B));
    }

    public static double getAngle(MyLatLng A, MyLatLng B) {
        double dx = (B.m_RadLo - A.m_RadLo) * A.Ed;
        double dy = (B.m_RadLa - A.m_RadLa) * A.Ec;
        double angle;
        angle = Math.atan(Math.abs(dx / dy)) * 180 / Math.PI;
        double dLo = B.m_Longitude - A.m_Longitude;
        double dLa = B.m_Latitude - A.m_Latitude;
        if (dLo > 0 && dLa <= 0) {
            angle = (90 - angle) + 90;
        } else if (dLo <= 0 && dLa < 0) {
            angle = angle + 180;
        } else if (dLo < 0 && dLa >= 0) {
            angle = (90 - angle) + 270;
        }
        return angle;
    }

    @Test
    public void test10() {
        System.out.println(Math.cos(Math.PI * 60.000001 / 180));
        System.out.println((double) 1 / 6);
        System.out.println(Math.PI * (30.0001 / 180));
        System.out.println(Math.sin(Math.PI / 6));
    }

    @Test
    public void test11() {
        double distance = AMapUtils.calculateLineDistance(new LatLng(30.546368, 104.068948), new LatLng(30.546368, 104.068848));
        System.out.println(distance);
    }

    @Test
    public void test12() {
        String name = "袁江峰(YJF)";
        if (name.contains("(")) {
            name = name.split("\\(")[0];
        }
        System.out.println(name);
    }

    @Test
    public void test13() throws Exception {
        String textContent = read("E://test.txt");
        Map<String, String> param = new HashMap<>();
        //加个距离
        double distance = 0;
        String[] arrLatlng = textContent.split(";");
        for (int i = 1; i < arrLatlng.length; i++) {
            String[] latlng = arrLatlng[i].split(",");
            String[] oldLatlng = arrLatlng[i - 1].split(",");
            if (latlng.length > 1) {
                distance += AMapDistanceUtil.calculateLineDistance(Double.parseDouble(oldLatlng[0]), Double.parseDouble(oldLatlng[1]), Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
            }
        }
        int intDistance = (int) distance;
        param.put("distance", intDistance + "");
        param.put("tripId", "11298356");
        final List<String> files = new ArrayList<>();//文件集合
        files.add("E://test.txt");
        WebService.uploadFiles(WebInterface.UPLOAD_TXT, "file", files, param, new SimpleCallBack() {
            @Override
            public void success(int code, String message, String data) {
                System.out.println(message + code);
            }
        });
        Thread.sleep(10000);
    }

    @Test
    public void test14() {
        String position = new DecimalFormat("#0.000000").format(104.09701822916666) + "," + new DecimalFormat("#0.000000").format(30.680117458767363);
        System.out.println(position);
    }

    @Test
    public void test15() {
//        104.145297,30.731839@180118115244;104.145313,30.731863@180118115320;
        String allPosition = ReadText.read("F:\\tmp\\trippos\\1483885_gps.txt");
        List<MyAMap> modifyLocation = new ArrayList<>();
        StringBuilder old = new StringBuilder();
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss", Locale.CHINA);
        if (allPosition != null) {
            String[] position = allPosition.split(";");
//            String startTime = "180122151021";
//            String endTime = "180122235921";
            String startTime = "0";
            String endTime = "z";
            for (String pos : position) {
                String longitude = pos.split(",")[0];
                String latitude = pos.split(",")[1].split("@")[0];
                String time = pos.split("@")[1];
                if (time.compareTo(startTime) > 0 && time.compareTo(endTime) < 0) {
                    if (builder.length() > 0) {
                        builder.append(";");
                    }
                    if (old.length() > 0) {
                        old.append(";");
                    }
                    old.append(pos);
                    long enterTime = TimeTool.dateToTime(time, "yyyyMMddHHmmss");
                    MyAMap aMap = new MyAMap("hello");
                    aMap.setLatitude(Double.parseDouble(latitude));
                    aMap.setLongitude(Double.parseDouble(longitude));
                    AMapLocation rectify = RectifyLocationUtils.rectify(aMap, enterTime);
                    builder.append(rectify.getLongitude()).append(",").append(rectify.getLatitude()).append("@")
                            .append(time);
                }
            }
        }
        System.out.println(old.toString());
        System.out.println(builder.toString());
    }

    /**
     * 位置信息封装类
     */
    private static class PositionVo {

        private long enterTime;

        private double longitude;

        private double latitude;

        private int age;

        public long getEnterTime() {
            return enterTime;
        }

        public void setEnterTime(long enterTime) {
            this.enterTime = enterTime;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

}

class MyAMap extends AMapLocation {

    public MyAMap(String s) {
        super(s);
    }

    public MyAMap(Location location) {
        super(location);
    }
}