package com.meibanlu.driver.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.bean.LineStation;
import com.meibanlu.driver.bean.TaskDetail;
import com.meibanlu.driver.tool.CommonData;
import com.meibanlu.driver.tool.Constants;
import com.meibanlu.driver.tool.DriverLocation;
import com.meibanlu.driver.tool.GpsTool;
import com.meibanlu.driver.tool.MapUtil;
import com.meibanlu.driver.tool.ScheduleTaskDaemon;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.TimeTool;
import com.meibanlu.driver.tool.TraceTool;
import com.meibanlu.driver.tool.UtilTool;
import com.meibanlu.driver.tool.XMDialog;
import com.meibanlu.driver.tool.web.SimpleCallBack;
import com.meibanlu.driver.tool.web.WebInterface;
import com.meibanlu.driver.tool.web.WebService;
import com.meibanlu.driver.webservice.RetrofitGenerator;
import com.meibanlu.driver.webservice.mappers.StationMapper;
import com.meibanlu.driver.webservice.requeset.CircleLineRequest;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.meibanlu.driver.tool.CommonData.aMapLocation;
import static com.meibanlu.driver.tool.CommonData.listTask;


/**
 * RunActivity运行中
 *
 * @author lhq
 * @date 2017/9/14
 */

public class RunMapActivity extends BaseActivity implements LocationSource, AMap.OnMapClickListener, AMap.OnMapLoadedListener ,AMap.OnMyLocationChangeListener{
    private MapView mapView;
    private AMap aMap;
    private String routeGps;
    private int errorCode;
    private ImageView ivEndSign;
    private String mapTripId;
    private ListView listView;
    private OnLocationChangedListener mListener;
    private static RunMapActivity runMapActivity;
    /**
     * 是否画线
     */
    private boolean isDrawLine;
    private final int DRAW_LINE = 0x1;
    private List<Circle> circleOptionses = new ArrayList<>();
    private List<LineStation> lineStations=new ArrayList<>();
    @SuppressLint("HandlerLeak")
    Handler mapHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DRAW_LINE:
                    T.hideLoading();
                    drawRoute();
                    break;
                default:
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        mapView = (MapView) findViewById(R.id.map);
        // 此方法必须重写
        mapView.onCreate(savedInstanceState);
        //设置顶部黑色标题栏
        setWindowColor(Color.BLACK);
        init();
        initView();
        initData();
    }

    private void initView() {
        listView= findViewById(R.id.listView);
        ImageView ivZoomIn = (ImageView) findViewById(R.id.iv_zoom_in);
        ImageView ivZoomOut = (ImageView) findViewById(R.id.iv_zoom_out);
        ImageView ivReturn = (ImageView) findViewById(R.id.iv_return);
        //定位
        ImageView ivLocation = (ImageView) findViewById(R.id.iv_location);
        //终点打卡
        ivEndSign = (ImageView) findViewById(R.id.iv_end_sign);
        Button btn1 = (Button) findViewById(R.id.btn1);
        TextView textView= (TextView) findViewById(R.id.tv_reStart);
        registerBtn(ivZoomIn, ivZoomOut, ivReturn, ivLocation, ivEndSign, btn1,textView);
    }

    private void initData() {
        if (listTask == null || listTask.size() == 0){
            return;
        }
        boolean canSign = false;
        String clickId = getIntent().getStringExtra("clickId");
        TaskDetail item = getTaskDetail(clickId);

        ivEndSign.setEnabled(canSign);
        if(item.isCircle()){
            if(item.getBackupStations()==null||item.getBackupStations().size()==0){
                 getBackupStations(item);
            }
        }
        if (!TextUtils.isEmpty(clickId) && item != null) {
            int departId = item.getDepartId();
            int arriveId = item.getArriveId();
            if (item.getArriveStation() != null) {
                TextView tvDistance = (TextView) findViewById(R.id.tv_distance);
                TextView tvRunTime = (TextView) findViewById(R.id.tv_run_time);
                int distance = item.getDistance()/1000;
                String elapsedTime = item.getArriveStation().getElapsedTime();
                tvDistance.setText(distance + "km");
            }
            mapTripId = item.getId();
            TextView tvStartStation = (TextView) findViewById(R.id.tv_start_station);
            TextView tvStartTime = (TextView) findViewById(R.id.tv_start_time);
            TextView tvEndStation = (TextView) findViewById(R.id.tv_end_station);
            tvStartStation.setText(item.getDepartStation().getName());
            tvEndStation.setText(item.getArriveStation().getName());
            tvStartTime.setText(item.getSchedule());
            if(item.getDepartStation()!=null){
                String startLatlng = item.getDepartStation().getLngLat();
                float startRadius = item.getDepartStation().getAreaRadius();
                drawRange(startLatlng, startRadius);

            }
            if(item.getArriveStation()!=null){
                String endLatlng = item.getArriveStation().getLngLat();
                float endRadius = item.getArriveStation().getAreaRadius();
                drawRange(endLatlng, endRadius);
            }

            if (CommonData.holderTrip != null && mapTripId.equals(CommonData.holderTrip.getTrip().getId())) {
                addStartSignMarker(); //画范围路线,出发旗帜
            }
            if (item.getStatus() == 1) {//设置终点是否可以打卡
                canSign = true;
            }
//            T.showLoading();
        } else {
            T.showShort(getString(R.string.no_data));
        }
    }

    /**
     * 获取当前的班次
     *
     * @param clickId clickId
     * @return task
     */
    private TaskDetail getTaskDetail(String clickId) {
        if(clickId==null) return null;
        List<TaskDetail> list = CommonData.listTask;
        if (list != null) {
            for (TaskDetail task : list) {
                if (clickId.equals(task.getId())) {
                    return task;
                }
            }
        }
        return null;
    }

    /**
     * 画起点终点打卡范围
     */
    private void drawRange(String strLatlng, float range) {
        Log.i("RunMap",strLatlng+"range:  "+range);
        if (!TextUtils.isEmpty(strLatlng)) {
            String[] arrLatlng = strLatlng.split(";");
            LatLng latLng = new LatLng(Double.parseDouble(arrLatlng[1]), Double.parseDouble(arrLatlng[0]));
            CircleOptions circleOptions = new CircleOptions().center(latLng).radius(range)
                    .fillColor(Color.argb(50,255,0,0))
                    .strokeColor(Color.argb(50, 1, 1, 1)).strokeWidth(15);
            Circle circle = aMap.addCircle(circleOptions);
            circleOptionses.add(circle);
        }
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
    private void addStartEndMarker(LatLng startLatLng, LatLng endLatLng) {
        aMap.addMarker(new MarkerOptions()
                .position(startLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_start)));
        aMap.addMarker(new MarkerOptions()
                .position(endLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_stop)));
    }

    private void addMarker(List<LineStation> stations){
        int length=stations.size();
        for(int i=0;i<length;i++){
            LatLng lng= new LatLng(Double.valueOf(stations.get(i).getLngLat().split(";")[1]),
                    Double.valueOf(stations.get(i).getLngLat().split(";")[0]));
            aMap.addMarker(new MarkerOptions()
                    .position(lng)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue)));
            TextOptions textOptions = new TextOptions();
            textOptions.position(lng);
            textOptions.text("途经点"+"\n"+stations.get(i).getName());
            textOptions.fontSize(18);
            textOptions.rotate(10);
            textOptions.fontColor(Color.RED);
            textOptions.backgroundColor(Color.TRANSPARENT);
            textOptions.align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL);
            textOptions.zIndex(1.f).typeface(Typeface.DEFAULT_BOLD);
            aMap.addText(textOptions);
            float radius = stations.get(i).getAreaRadius();
            drawRange(stations.get(i).getLngLat(),radius);
        }
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.btn1:
                List<TraceLocation> listTrace = UtilTool.getDbManager().queryAllPosition(mapTripId);
                routeGps = "";
                if (listTrace != null && listTrace.size() > 0) {
                    StringBuilder addStrGps = new StringBuilder();
                    try {
                        for (TraceLocation trace : listTrace) {
                            String context = trace.getLatitude() + "," + trace.getLongitude() + "," + trace.getBearing() + "," + trace.getSpeed() + trace.getTime();
                            if (!addStrGps.toString().contains(context)) {
                                addStrGps.append(context).append(";");
                                routeGps += trace.getLongitude() + "," + trace.getLatitude() + ";";
                            }
                        }
                    } catch (ConcurrentModificationException e) {
                        return;
                    }
                    new TraceTool().startTrace();
                    drawRoute();
                }
                break;
            case R.id.iv_zoom_in:
                MapUtil.changeCamera(CameraUpdateFactory.zoomIn(), aMap);
                //放大
                break;
            case R.id.iv_zoom_out:
                MapUtil.changeCamera(CameraUpdateFactory.zoomOut(), aMap);
                //缩小
                break;
            case R.id.iv_location:
                if (aMapLocation == null || aMapLocation.getLatitude() == 0) {
                    //未定位成功
                    T.showShort(getString(R.string.location_false));
                } else {
                    MapUtil.changeCamera(aMapLocation.getLatitude(), aMapLocation.getLongitude(), aMap, aMap.getCameraPosition().zoom);
                }
                //缩小
                break;
            //终点手动打卡
            case R.id.iv_end_sign:
                UtilTool.endManSign(RunMapActivity.this);
                break;
            //终点手动打卡
            case R.id.tv_reStart:
                T.showShort("重启定位");
                DriverLocation.reStartLocation();
                break;
            default:
        }
    }


    /**
     * 设置地理位置，经纬度
     *
     * @param
     */
    public void setLocation( ) {
        if(!isFirst){
            isFirst=true;
        }
    }

    /**
     * 获取对象
     *
     * @return RunMapActivity
     */
    public static RunMapActivity getInstance() {
        return runMapActivity;
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            runMapActivity = this;
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
            myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.test_positioning_balls));
            aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
            aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
            aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定
            aMap.setOnMyLocationChangeListener(this);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
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
        runMapActivity = null;
        deactivate();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
    }

    /**
     * 画路线
     */
    private void drawRoute() {
        int lineColor = Color.rgb(251, 27, 27);
        PolylineOptions polyline = UtilTool.strToPolyline(routeGps);
        aMap.addPolyline(polyline.color(lineColor));
        List<LatLng> latLongs = polyline.getPoints();
//        MapUtil.setZoom(aMap, latLongs);
        addStartEndMarker(latLongs.get(0), latLongs.get(latLongs.size() - 1));
    }

    /**
     * 轨迹纠偏
     *
     * @param list 经纬度信息
     */
    public void setTraceStatus(List<LatLng> list) {
        //将得到的轨迹点显示在地图上
//        if (CommonData.holderTrip.getTripId().equals(mapTripId)) {
        new TraceOverlay(aMap, list);
//        }
    }

    /**
     * @param latLng 点击地图
     */
    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override

    public void onMapLoaded() {
        setLocation();//首次定位
    }


    /**
     * 添加出发打卡成功
     */
    public void addStartSignMarker() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String startPos;
                if (CommonData.holderTrip != null && !TextUtils.isEmpty(startPos = CommonData.holderTrip.getStartSignPosition())) {
                    ivEndSign.setEnabled(true);
                    LatLng pos = new LatLng(Double.valueOf(startPos.split(";")[1]),
                            Double.valueOf(startPos.split(";")[0]));
                    //出发打卡旗帜
                    aMap.addMarker(new MarkerOptions()
                            .position(pos)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_flag)));
                }
            }
        });
    }


    /**
     * 添加终点打卡成功
     */
    public void addEndSignMarker() {
//        drawRange();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String endPos;
                if (CommonData.holderTrip == null || TextUtils.isEmpty(endPos = CommonData.holderTrip.getEndSignPosition())) {
                    endPos = UtilTool.getCurrentLatLng();
                }
                assert endPos != null;
                LatLng pos = new LatLng(Double.valueOf(endPos.split(";")[1]),
                        Double.valueOf(endPos.split(";")[0]));

                ivEndSign.setEnabled(false);
                //终点打卡旗帜
                aMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_flag)));
            }
        });
    }

    private boolean isFirst=true;

    @Override
    public void onMyLocationChange(Location location) {
        if(isFirst){
            CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(
                    location.getLatitude(),location.getLongitude()),11,0,0));
            aMap.animateCamera(mCameraUpdate);
            isFirst=false;
//            CommonData.aMapLocation= (AMapLocation) location;
        }
    }

    /**获取环线线路的途径站点
     * @param detail 当前任务
     */
    public void getBackupStations(final TaskDetail detail){
        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        CircleLineRequest request=new CircleLineRequest();
        Header header=new Header();
        request.setLineId(String.valueOf(detail.getBusId()));
        body.circleLineRequest=request;
        envelope.header=header;
        envelope.body=body;
        RetrofitGenerator.getInstance().getApiStore().getData(envelope,"urn:TYWJAPPIntf-ITYWJAPP#huanxian")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseEnvelope>() {
                    @Override
                    public void accept(ResponseEnvelope responseEnvelope) throws Exception {
                        List<LineStation> stations = new StationMapper().transform(responseEnvelope);
                        if (stations.size() > 0) {
                            LatLng start=new LatLng(Double.valueOf(stations.get(0).getLngLat().split(";")[1])-0.001000,
                                    Double.valueOf(stations.get(0).getLngLat().split(";")[0])-0.001000);
                            LatLng end=new LatLng(Double.valueOf(stations.get(stations.size()-1).getLngLat().split(";")[1]),
                                    Double.valueOf(stations.get(stations.size()-1).getLngLat().split(";")[0]));
                            addStartEndMarker(start, end);
//                            if(stations.get(0).getId()==stations.get(stations.size()-1).getId()){
//
//                            }
                            stations.remove(stations.size()-1);
                            stations.remove(0);
                            lineStations=stations;
                            addMarker(stations);
                            detail.setBackupStations(stations);
                            StationAdapter adapter=new StationAdapter();
                            listView.setAdapter(adapter);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(throwable.getMessage()!=null){
                            Log.e("ERROR",throwable.getMessage());
                        }
                    }
                });

    }

    private class StationAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return lineStations.size();
        }

        @Override
        public LineStation getItem(int position) {
            return lineStations.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(RunMapActivity.this, R.layout.item_station, null);
                holder.name=convertView.findViewById(R.id.station_name);
                holder.sign=convertView.findViewById(R.id.sign);
                convertView.setTag(holder);
            }else {
                holder= (ViewHolder) convertView.getTag();
            }
            final LineStation station=lineStations.get(position);
            holder.name.setText(station.getName());
            holder.sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) v.getTag();
                    LineStation lineStation= getItem(pos);
                    if(CommonData.aMapLocation==null){
                        T.showShort("定位失败");
                        return;
                    }
                    boolean in= GpsTool.checkPointInPolygon(
                            CommonData.aMapLocation.getLongitude() + ";" + CommonData.aMapLocation.getLatitude(),
                            lineStation.getLngLat(),
                            lineStation.getAreaRadius());
//                    if(in){
//                        ScheduleTaskDaemon.sign(taskDetail.toHolderTrip(), Constants.STATUS_ARRIVE, null, null, Constants.MODE_MAN_SUCCESS);
//                    }else {
//                        XMDialog.showDialog(activity, "不在打卡范围内,确认打异常卡", new XMDialog.DialogResult() {
//                            @Override
//                            public void clickResult(int resultCode) {
//                                if (resultCode == XMDialog.CLICK_SURE) {
//                                    ScheduleTaskDaemon.sign(taskDetail.toHolderTrip(), Constants.STATUS_ARRIVE, null, null, Constants.MODE_MAN_ABNORMAL);
//                                }
//                            }
//                        });
//                    }
                }
            });
            holder.sign.setTag(position);
            return convertView;
        }
    }
    class ViewHolder {
        TextView name, sign;
    }
}

