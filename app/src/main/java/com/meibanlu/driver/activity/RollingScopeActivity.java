package com.meibanlu.driver.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.bean.LineStation;
import com.meibanlu.driver.bean.TaskDetail;
import com.meibanlu.driver.tool.CommonData;
import com.meibanlu.driver.tool.DriverLocation;
import com.meibanlu.driver.tool.GpsTool;
import com.meibanlu.driver.tool.HolderTrip;
import com.meibanlu.driver.tool.MapUtil;
import com.meibanlu.driver.tool.RectifyLocationUtils;
import com.meibanlu.driver.tool.StateConstants;
import com.meibanlu.driver.tool.T;

import java.util.ArrayList;
import java.util.List;

import static com.meibanlu.driver.tool.CommonData.aMapLocation;


/**
 * RunActivity运行中
 *
 * @author lhq
 * @date 2017/9/14
 */

@SuppressLint("Registered")
public class RollingScopeActivity extends BaseActivity implements LocationSource, AMap.OnMapLoadedListener {
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private static RollingScopeActivity rollingScopeActivity;
    private boolean isRestartLocation;
    private List<Circle> circleOptionses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rolling_scope);
        mapView = (MapView) findViewById(R.id.map);
        // 此方法必须重写
        mapView.onCreate(savedInstanceState);
        //设置顶部黑色标题栏
        setWindowColor(Color.BLACK);
        init();
        initView();
    }

    private void initView() {
        ImageView ivReturn = (ImageView) findViewById(R.id.iv_return);
        //重新定位
        ImageView ivRestart = (ImageView) findViewById(R.id.iv_restart);
        ImageView ivZoomIn = (ImageView) findViewById(R.id.iv_zoom_in);
        ImageView ivZoomOut = (ImageView) findViewById(R.id.iv_zoom_out);
        registerBtn(ivReturn, ivRestart, ivZoomIn, ivZoomOut);
    }

    private void initData() {
        String state = getIntent().getStringExtra("state");
        switch (state) {
            case StateConstants.NO_TRIP_ROLLING:
                //无当前班次，仅有滚动班次
            case StateConstants.NO_TRIP_ROLLING_FIXED:
                //无当前班次，有滚动班次和固定班次
                noTripNotIn();
                break;
            case StateConstants.HAVE_TRIP_NOT_FIXED:
                //有当前班次,不在范围内
            case StateConstants.RUN_TRIP:
                //运行中,未到达终点范围
                haveTripNotIn(state);
                break;
            default:
        }
    }

    /**
     * 没有当前班次,并且包含有滚动班次
     */
    private void noTripNotIn() {
        List<TaskDetail> taskList = HomePageActivity.getInstance().notFinishTask;
        List<LatLng> latLngs = new ArrayList<>();
        if (taskList != null) {
            for (TaskDetail item : taskList) {
                if (item.isRoll()) {
                    LineStation line = item.getDepartStation();
                    if (line != null && line.getAreaRadius() != null) {
                        drawRange(line.getLngLat(), line.getAreaRadius());
                        String[] arrLatlng = line.getLngLat().split(",");
                        LatLng latLng = new LatLng(Double.parseDouble(arrLatlng[1]), Double.parseDouble(arrLatlng[0]));
                        latLngs.add(latLng);
                        addStartEndMarker(latLng, true);
                    }
                }
            }
            setZoom(latLngs);
        }
    }

    /**
     * 有当前班次没有在范围内
     */
    private void haveTripNotIn(String state) {
        HolderTrip holderTrip = CommonData.holderTrip;
        if (holderTrip != null) {
            TaskDetail item = holderTrip.getTrip();
            LineStation line;
            boolean isDepart = state.equals(StateConstants.HAVE_TRIP_NOT_FIXED);
            if (isDepart) {
                line = item.getDepartStation();
            } else {
                line = item.getArriveStation();
            }
            if (line != null && line.getAreaRadius() != null) {
                drawRange(line.getLngLat(), line.getAreaRadius());
                List<LatLng> latLngs = new ArrayList<>();
                String[] arrLatlng = line.getLngLat().split(";");
                LatLng latLng = new LatLng(Double.parseDouble(arrLatlng[1]), Double.parseDouble(arrLatlng[0]));
                addStartEndMarker(latLng, isDepart);
                latLngs.add(latLng);
                setZoom(latLngs);
            }
        }
    }


    /**
     * 画起点终点打卡范围
     */
    private void drawRange(String strLatlng, float range) {
        if (!TextUtils.isEmpty(strLatlng)) {
            String[] arrLatlng = strLatlng.split(";");
            LatLng latLng = new LatLng(Double.parseDouble(arrLatlng[1]), Double.parseDouble(arrLatlng[0]));
            CircleOptions circleOptions = new CircleOptions().center(latLng).radius(range).strokeColor(Color.argb(50, 23, 167, 255)).strokeWidth(0);
            circleOptions.fillColor(Color.argb(50, 23, 167, 255));
            Circle circle = aMap.addCircle(circleOptions);
            circleOptionses.add(circle);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.iv_restart:
                isRestartLocation = true;
                DriverLocation.reStartLocation();
                RectifyLocationUtils.clearPosition();
                break;
            case R.id.iv_zoom_in:
                MapUtil.changeCamera(CameraUpdateFactory.zoomIn(), aMap);
                //放大
                break;
            case R.id.iv_zoom_out:
                MapUtil.changeCamera(CameraUpdateFactory.zoomOut(), aMap);
                //缩小
                break;
            default:
        }
    }

    /**
     * 设置地理位置，经纬度
     *
     * @param item aMapLocation
     */
    public void setLocation(AMapLocation item) {
        mListener.onLocationChanged(item);
        drawColor(item);
        if (isRestartLocation) {
            isRestartLocation = false;
            T.showShort("定位成功");
        }
    }

    /**
     * 获取对象
     *
     * @return RunMapActivity
     */
    public static RollingScopeActivity getInstance() {
        return rollingScopeActivity;
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            MapUtil.setAMapStyle(aMap, this);
            //地图加载完成
            aMap.setOnMapLoadedListener(this);
            rollingScopeActivity = this;
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
        rollingScopeActivity = null;
        deactivate();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    /**
     * 缩放比例
     */
    private void setZoom(List<LatLng> latLongs) {
        if (latLongs != null && latLongs.size() == 1) {
            MapUtil.changeCamera(latLongs.get(0), aMap, 15.2f);
        } else {
            MapUtil.setZoom(aMap, latLongs);
        }
    }

    @Override
    public void onMapLoaded() {
        //首次定位
        setLocation((AMapLocation) aMapLocation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }, 300);
    }

    /**
     * 在范围内变成绿色
     *
     * @param mapLocation mapLocation
     */
    private void drawColor(AMapLocation mapLocation) {
        for (Circle circle : circleOptionses) {
            LatLng latLng = circle.getCenter();
            boolean in = GpsTool.checkPointInPolygon(mapLocation.getLongitude() + ";" +
                    mapLocation.getLatitude(), latLng.longitude + ";" +
                    latLng.latitude, circle.getRadius());
            int color = Color.argb(50, 23, 167, 255);
            if (in) {
                color = Color.argb(90, 21, 160, 245);
            }
            circle.setFillColor(color);
        }
    }

    /**
     * 添加起点终点marker
     */
    private void addStartEndMarker(LatLng startLatLng, boolean isStart) {
        int image = R.mipmap.ic_start;
        if (!isStart) {
            image = R.mipmap.ic_stop;
        }
        aMap.addMarker(new MarkerOptions()
                .position(startLatLng)
                .icon(BitmapDescriptorFactory.fromResource(image)));
    }
}

