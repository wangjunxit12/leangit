package com.meibanlu.driver.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.tool.CommonData;
import com.meibanlu.driver.tool.MapUtil;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.TimeTool;
import com.meibanlu.driver.tool.UtilTool;
import com.meibanlu.driver.tool.web.SimpleCallBack;
import com.meibanlu.driver.tool.web.WebInterface;
import com.meibanlu.driver.tool.web.WebService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DrivingRouteMapActivity运行中
 * Created by lhq on 2017/9/14.
 */

public class RouteDepartureMapActivity extends BaseActivity implements AMap.OnMapLoadedListener {
    private MapView mapView;
    private AMap aMap;
    private TextView tvStartTime, tvArriveTime, tvPlanDistance, tvRealityDistance, tvStartStation, tvEndStation;
    private final int GET_GPS = 1;
    private final int PLAN_GET_GPS = 2;
    private boolean depart;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_GPS:
                    T.hideLoading();
                    String routeGps = msg.obj.toString();
                    List<LatLng> latlngs = UtilTool.strToLat(routeGps);
                    PolylineOptions options = UtilTool.getPolyline(latlngs);
                    lineAddMap(options);
                    break;
                case PLAN_GET_GPS:
                    int color1 = Color.rgb(251, 27, 27);
                    String PlanRouteGps = msg.obj.toString();
                    drawRoute(PlanRouteGps, color1, true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_record);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        initView();
    }

    private void initView() {
        depart = getIntent().getBooleanExtra("routeDepart", false);
        setTitle(getString(depart ? R.string.departure_recode_detail : R.string.recode_detail));
        ImageView ivZoomIn = (ImageView) findViewById(R.id.iv_zoom_in);
        ImageView ivZoomOut = (ImageView) findViewById(R.id.iv_zoom_out);
        tvStartTime = (TextView) findViewById(R.id.tv_depart_time);
        tvEndStation = (TextView) findViewById(R.id.tv_end_station);
        tvPlanDistance = (TextView) findViewById(R.id.tv_plan_distance);
        tvRealityDistance = (TextView) findViewById(R.id.tv_reality_distance);
        tvArriveTime = (TextView) findViewById(R.id.tv_arrive_time);
        tvStartStation = (TextView) findViewById(R.id.tv_start_station);
        registerBtn(ivZoomIn, ivZoomOut);
    }

    private void initData() {
        String tripLineFile = getIntent().getStringExtra("tripLineFile");
        long longDepartTime = getIntent().getLongExtra("departTime", 0);
        long longArriveTime = getIntent().getLongExtra("arriveTime", 0);

        if (longDepartTime != 0) {
            tvStartTime.setText(TimeTool.stampToDate(longDepartTime, "HH:mm"));
        }
        if (longArriveTime != 0) {
            tvArriveTime.setText(TimeTool.stampToDate(longArriveTime, "HH:mm"));
        }
        tvStartStation.setText(getIntent().getStringExtra("startStation"));
        tvEndStation.setText(getIntent().getStringExtra("endStation"));
        tvPlanDistance.setText(getIntent().getStringExtra("planDistance"));
        tvRealityDistance.setText(getIntent().getStringExtra("realtyDistance"));
//        if (!TextUtils.isEmpty(tripLineFile)) {
//            getRouteGps(tripLineFile);
//        }
        int startId = getIntent().getIntExtra("startId", 0);
        int endId = getIntent().getIntExtra("endId", 0);
//        getLineGps(startId, endId);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_zoom_in:
                MapUtil.changeCamera(CameraUpdateFactory.zoomIn(), aMap);
                //放大
                break;
            case R.id.iv_zoom_out:
                MapUtil.changeCamera(CameraUpdateFactory.zoomOut(), aMap);
                //缩小
                break;
        }
    }

    private void lineAddMap(PolylineOptions options) {
        List<LatLng> latLongs = options.getPoints();
        options.width(CommonData.windowWidth / 20).zIndex(6);
        aMap.addPolyline(options);
        addMarker(latLongs.get(0), latLongs.get(latLongs.size() - 1));
    }

    /**
     * 添加实际路线的marker
     */
    private void addMarker(LatLng startLatLng, LatLng endLatLng) {
        aMap.addMarker(new MarkerOptions()
                .position(startLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_flag)));
        aMap.addMarker(new MarkerOptions()
                .position(endLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_flag)));
    }

//    private void getRouteGps(String tripLineFile) {
//        T.showLoading();
//        Map<String, Object> param = new HashMap<>();
//        param.put("filePath", tripLineFile);
//        WebService.doRequest(WebService.GET, WebInterface.RECORD_GPS, param, new SimpleCallBack() {
//            @Override
//            public void success(int code, String message, String data) {
//                if (code == 0) {
//                    Message msg = Message.obtain();
//                    msg.obj = data;
//                    msg.what = GET_GPS;
//                    handler.sendMessage(msg);
//                } else {
//                    hide();
//                    toast("暂无路线记录");
//                }
//            }
//        });
//    }

    /**
     * 画路线
     */
    private void drawRoute(String routeGps, int color, boolean drawStart) {
        PolylineOptions polyline = UtilTool.strToPolyline(routeGps);
        aMap.addPolyline(polyline.color(color));
        List<LatLng> latLongs = polyline.getPoints();
        MapUtil.setZoom(aMap, latLongs);
        if (drawStart) {
            addStartEndMarker(latLongs.get(0), latLongs.get(latLongs.size() - 1));
        }
    }

    /**
     * 添加起点终点marker
     */
    private void addStartEndMarker(LatLng startLatLng, LatLng endLatLng) {
        aMap.addMarker(new MarkerOptions()
                .position(startLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_start)));
        aMap.addMarker(new MarkerOptions()
                .position(endLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_stop)));
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setRotateGesturesEnabled(false);// 设置地图是否能够旋转
            aMap.setOnMapLoadedListener(this);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapLoaded() {
        initData();
    }

//    public void getLineGps(int departId, int arriveId) {
//        Map<String, Object> param = new HashMap<>();
//        param.put("departId", departId);
//        param.put("arriveId", arriveId);
//        WebService.doRequest(WebService.GET, WebInterface.GET_LINE_GPS, param, new SimpleCallBack() {
//            @Override
//            public void success(int code, String message, String data) {
//                if (code == 0) {
//                    Message msg = Message.obtain();
//                    msg.obj = data;
//                    msg.what = PLAN_GET_GPS;
//                    handler.sendMessage(msg);
//                }
//            }
//        });
//
//    }
}
