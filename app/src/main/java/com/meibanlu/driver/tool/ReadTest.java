package com.meibanlu.driver.tool;

import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lhq on 2017/11/3.
 */

public class ReadTest {
    static String fileName;
    static String routeGPS = "104.054848,30.661585;104.054848,30.661493;104.054825,30.660875;104.054604,30.660892;104.054283,30.660885;104.053246,30.660837;104.05294,30.66083;104.05265,30.660843;104.052597,30.660843;104.052269,30.660875;104.051926,30.660933;104.051659,30.661007;104.051003,30.661215;104.050331,30.661444;104.049843,30.661615;104.049797,30.661516;104.050934,30.661129;104.051895,30.660833;104.052589,30.660742;104.052887,30.660725;104.054504,30.660791;104.054985,30.660751;104.055412,30.660643;104.05587,30.660446;104.056259,30.660183;104.057121,30.659376;104.057526,30.659016;104.058144,30.658464;104.058334,30.658329;104.058578,30.65802;104.058807,30.657787;104.059486,30.657173;104.059647,30.657053;104.060287,30.656658;104.060486,30.65654;104.060715,30.656363;104.060715,30.655399;104.060715,30.65506;104.060699,30.654657;104.060692,30.654449;104.060623,30.654032;104.060463,30.653738;104.0588,30.651037;104.057365,30.649218;104.056732,30.648438;104.05603,30.647604;104.055397,30.647108;104.05349,30.646172;104.052734,30.645834;104.04969,30.64444;104.048126,30.644222;104.04734,30.644144";
    private static ExecutorService locationServicePool = Executors.newSingleThreadExecutor();

    public static void start() {
        locationServicePool.execute(new Runnable() {
            @Override
            public void run() {
                T.log("3333333333333");
                T.log("44444444444444444444444444444");
                final List<LatLng> list1 = new ArrayList<>();
                String[] arrGps = routeGPS.split(";");
                T.log("555555555555555555555555555555");
                for (String strLatlng : arrGps) {
                    String[] arrLatlng = strLatlng.split(",");
                    list1.add(new LatLng(Double.parseDouble(arrLatlng[1]), Double.parseDouble(arrLatlng[0])));
                }
                T.log("6666666666666666666666");
                if (list1 != null && list1.size() > 0) {
                    StringBuilder addStrGps = new StringBuilder();
                    for (LatLng latLng : list1) {
                        String context = latLng.longitude + "," + latLng.latitude + ";";
                        if (!addStrGps.toString().contains(context)) {
                            addStrGps.append(latLng.longitude).append(",").append(latLng.latitude).append(";");
                        }
                    }
                    String strGps = addStrGps.toString();
                    String endGps = strGps.substring(0, strGps.length() - 1);
                    if (TextUtils.isEmpty(fileName)) {
                        fileName = FilePath.getRoutePath() + "/" + "1008611" + ".txt";
//                            fileName = FilePath.getRoutePath() + "/" + "test.txt";
                        FileTool.createFile(fileName);
                    }
                    new WriteText().startWrite(endGps, fileName);
                    T.log("记录成功");
                }
            }
        });
    }


}
