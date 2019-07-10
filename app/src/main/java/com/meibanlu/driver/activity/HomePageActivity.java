package com.meibanlu.driver.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.adapter.TaskListAdapter;
import com.meibanlu.driver.application.DriverApplication;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.bean.LineStation;
import com.meibanlu.driver.bean.PositionVo;
import com.meibanlu.driver.bean.TaskDetail;
import com.meibanlu.driver.bean.UpdateEntity;
import com.meibanlu.driver.bean.UpdateEvent;
import com.meibanlu.driver.service.UpdateService;
import com.meibanlu.driver.tool.CommonData;
import com.meibanlu.driver.tool.Constants;
import com.meibanlu.driver.tool.DriverLocation;
import com.meibanlu.driver.tool.FilePath;
import com.meibanlu.driver.tool.GpsTool;
import com.meibanlu.driver.tool.HolderTrip;
import com.meibanlu.driver.tool.RefreshLayout;
import com.meibanlu.driver.tool.RxBus;
import com.meibanlu.driver.tool.ScheduleTaskDaemon;
import com.meibanlu.driver.tool.SharePreData;
import com.meibanlu.driver.tool.StateConstants;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.TimeTool;
import com.meibanlu.driver.tool.UpdateManager;
import com.meibanlu.driver.tool.UtilState;
import com.meibanlu.driver.tool.UtilTool;
import com.meibanlu.driver.tool.XMDialog;
import com.meibanlu.driver.tool.XmPlayer;
import com.meibanlu.driver.tool.web.DataCallBack;
import com.meibanlu.driver.tool.web.WebService;
import com.meibanlu.driver.view.loading.ShapeLoadingDialog;
import com.meibanlu.driver.webservice.RetrofitGenerator;
import com.meibanlu.driver.webservice.requeset.GetCodeStateRequest;
import com.meibanlu.driver.webservice.requeset.GetVersionRequest;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.meibanlu.driver.tool.XMDialog.CLICK_SURE;


/**
 * HomePageActivity 主页
 *
 * @author lhq
 * @date 2017/9/14
 */

public class HomePageActivity extends BaseActivity implements
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
        View.OnTouchListener {
    /**
     * 我的最近任务列表
     */
    private ListView lvMyTask;
    /**
     * 刷新当天的任务
     */
    private static final int REFRESH_GET_TASK = 0X1;
    /**
     * 获取权限
     */
    private static final int GET_PERMISSIONS = 0X2;
    /**
     * 是否阅读
     */

    private static final int INSTALL_PACKAGES_REQUESTCODE=0;
    private ImageView isRead;
    public static boolean isForeground;
    public TextView tvNoShift;
    private TaskListAdapter baseAdapter;
    private static HomePageActivity homePageActivity;
    private RefreshLayout refresh;
    private RelativeLayout rlSign, rlStart, rlEndSign, rlErrorEnd;
    private TextView tvStartSign;
    private int state = Constants.WAIT_FINISH;
    private TextView tvWaitFinish, tvFinish;
    private TextView tvState;
    private int position;
    private RelativeLayout rlPersonal;
    private ShapeLoadingDialog dialog;


    /**
     * 当前未完成班次
     */
    public List<TaskDetail> notFinishTask;

    /**
     * 获取对象
     *
     * @return RunMapActivity
     */
    public static HomePageActivity getInstance() {
        return homePageActivity;
    }

    @SuppressLint("HandlerLeak")
    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_GET_TASK:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    if (refresh.isRefreshing()) {
                        refresh.setRefreshing(false);
                        T.showShort("刷新完成");
                    }
                    initTaskAdapter();
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mCompositeDisposable=new CompositeDisposable();
        initView();
        initData();
        initPosition();
        getPermissions();
        openWifi();
        checkVersion();
        checkPhoneState();
        operateBus();
        DriverApplication.getApplication().startPolling();
//        MySyntherizer.getInstance();
    }

    private void initPosition() {
        CommonData.openApp = true;
        DriverLocation.getInstance();
//        DriverLocationTest.getInstance();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        lvMyTask = findViewById(R.id.lv_my_task);
        TextView tvTime = findViewById(R.id.tv_time);
        isRead = findViewById(R.id.iv_is_read);
        //个人中心
        rlPersonal = findViewById(R.id.rl_personal);
        rlSign = findViewById(R.id.rl_sign);
        //进入省电模式
        TextView tvSave = findViewById(R.id.tv_save);
        tvWaitFinish = findViewById(R.id.tv_wait_finish);
        tvFinish = findViewById(R.id.tv_finish);
        tvState = findViewById(R.id.tv_state);
        TextView tvTomorrow = findViewById(R.id.tv_tomorrow);
        TextView tvReward = findViewById(R.id.tv_reward);
        tvStartSign = findViewById(R.id.tv_start_sign);
        rlErrorEnd = findViewById(R.id.rl_error_end);
        rlStart = findViewById(R.id.rl_start);
        rlEndSign = findViewById(R.id.rl_end_sign);
        registerBtn(rlPersonal, tvWaitFinish, tvFinish, tvState, tvStartSign,
                tvTomorrow, tvReward, tvSave, rlErrorEnd, rlEndSign);
        lvMyTask.setOnItemClickListener(this);
        rlPersonal.setOnTouchListener(this);
        String timeDay = TimeTool.getCurrentTime("yyyy-MM-dd");
        tvTime.setText(timeDay);
        tvNoShift = findViewById(R.id.tv_no_message);
        refresh = findViewById(R.id.refresh_container);
        dialog=new ShapeLoadingDialog(this);
        refresh.setColorSchemeResources(R.color.blue_one, R.color.blue_two, R.color.color_49B4F8, R.color.color_0972E5);
    }

    private void initData() {
        dealIsFirst();
//        //上传完成轨迹的txt文档
//        RouteUpload.uploadFileType(RouteUpload.UPLOAD_TXT);
//        //上传失败的文件再次上传
//        RouteUpload.uploadFileType(RouteUpload.UPLOAD_FAIL_TXT);
        initGPS();
        loadTask(); //初始化当日班次
        refresh.setOnRefreshListener(this);
        homePageActivity = this;
    }

    /**
     * 打开app
     */
    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则跳转至设置开启界面，设置完毕后返回到首页
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            XMDialog.showDialog(HomePageActivity.this, "为了更好的为您服务，请您打开您的GPS!", new XMDialog.DialogResult() {
                @Override
                public void clickResult(int resultCode) {
                    if (resultCode == XMDialog.CLICK_SURE) {
                        // 转到手机设置界面，用户设置GPS
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        // 设置完成后返回到原来的界面
                        startActivityForResult(intent, 0);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_start_sign:
                final HolderTrip holderTrip = CommonData.holderTrip;
                if (CommonData.aMapLocation == null) {
                    //定位失败
                    T.showShort(getString(R.string.location_error));
                    return;
                }
                if (holderTrip == null) {
                    T.showShort("未获取到当前班次");
                    return;
                }

                LineStation departStation = holderTrip.getTrip().getDepartStation();
                if(departStation==null){
                    T.showShort("未获取到当前班次");
                    return;
                }

                boolean in = GpsTool.checkPointInPolygon(
                        CommonData.aMapLocation.getLongitude() + ";" + CommonData.aMapLocation.getLatitude(),
                        departStation.getLngLat(),
                        departStation.getAreaRadius());
                if(in){
                    //不能兼容跨夜的问题
                    String time = TimeTool.getCurrentTime("HH:mm");
                    //处理正常班次
                    String scheduleTime = holderTrip.getTrip().getSchedule();
                    int duration = TimeTool.getDistanceTimesSign(time, scheduleTime);
                    if(holderTrip.getTrip().isRoll()){
                        ScheduleTaskDaemon.sign(holderTrip, Constants.STATUS_DEPART, null, null, Constants.MODE_MAN_SUCCESS);
                    }else {
                        if (duration >= Constants.TRIP_SELECT_TIME_LEFT && duration <= Constants.TRIP_SELECT_TIME_RIGHT) {
                            ScheduleTaskDaemon.sign(holderTrip, Constants.STATUS_DEPART, null, null, Constants.MODE_MAN_SUCCESS);
                        }else {
                            ScheduleTaskDaemon.sign(holderTrip, Constants.STATUS_DEPART, null, null, Constants.MODE_MAN_ABNORMAL);
                        }
                    }
                }else {
                    T.showShort("不在站点范围内");
                    T.showShort("距离打卡范围"+GpsTool.getDistance( CommonData.aMapLocation.getLongitude() + ";" + CommonData.aMapLocation.getLatitude(),
                            departStation.getLngLat(),
                            departStation.getAreaRadius())+"米");
                    AlertDialog dialog=new AlertDialog.Builder(HomePageActivity.this)
                            .setTitle("不在打卡范围内")
                            .setPositiveButton("查看当前位置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i=new Intent();
                                    i.setClass(HomePageActivity.this,RunMapActivity.class);
                                    startActivity(i);
                                }
                            })
                            .setNegativeButton("打卡", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ScheduleTaskDaemon.sign(holderTrip, Constants.STATUS_DEPART, null, null, Constants.MODE_MAN_ABNORMAL);
                                }
                            }).create();
                    dialog.show();

                }

                break;
            case R.id.rl_end_sign:
                if (CommonData.aMapLocation == null) {
                    //定位失败
                    T.showShort(getString(R.string.location_error));
                    return;
                }
                UtilTool.endManSign(HomePageActivity.this);
                break;
            case R.id.tv_wait_finish:
                state = Constants.WAIT_FINISH;
                tvWaitFinish.setTextColor(T.getColorById(R.color.text_color_333333));
                tvFinish.setTextColor(T.getColorById(R.color.text_color_999999));
                rlStart.setVisibility(View.VISIBLE);
                initTaskAdapter();
                break;
            case R.id.tv_finish:
                state = Constants.FINISH;
                tvFinish.setTextColor(T.getColorById(R.color.text_color_333333));
                tvWaitFinish.setTextColor(T.getColorById(R.color.text_color_999999));
                rlStart.setVisibility(View.GONE);
                initTaskAdapter();
                break;
            case R.id.tv_tomorrow:
                Intent intent = new Intent();
                intent.setClass(HomePageActivity.this, RouteDepartureActivity.class);
                intent.putExtra("toMorrow", "toMorrow");
                startActivity(intent);
                break;
            case R.id.tv_reward:
                startActivity(RewardActivity.class);
                break;
            case R.id.tv_save:
                startActivity(RunActivity.class);
                break;
            case R.id.rl_error_end:
                showExitDialog();
                break;
            case R.id.tv_state:
                String state = tvState.getText().toString();
                switch (state) {
                    case StateConstants.HAVE_TRIP_NOT_FIXED:
                    case StateConstants.RUN_TRIP:
                    case StateConstants.NO_TRIP_ROLLING:
                    case StateConstants.NO_TRIP_ROLLING_FIXED:
                        startActivity(RollingScopeActivity.class, "state", state);
                        break;
                    default:
                        XMDialog.stateDialog(homePageActivity, state);
                }
                break;
            default:
        }
    }


    public void initTaskAdapter() {
        List<TaskDetail> taskList = getTaskList();
        if (state == Constants.WAIT_FINISH) {
            notFinishTask = taskList;
        }
        if (CommonData.listTask != null && CommonData.listTask.size() > 0 && taskList.size() > 0) {
            tvNoShift.setVisibility(View.GONE);
            lvMyTask.setVisibility(View.VISIBLE);
            tvState.setVisibility(View.VISIBLE);
        } else {
            tvNoShift.setVisibility(View.VISIBLE);
            tvState.setVisibility(View.GONE);
            lvMyTask.setVisibility(View.GONE);
            rlSign.setVisibility(View.GONE);
            return;
        }
        if (baseAdapter == null) {
            baseAdapter = new TaskListAdapter(taskList, this);
            lvMyTask.setAdapter(baseAdapter);
        } else {
            baseAdapter.setTaskList(taskList);
            baseAdapter.notifyDataSetChanged();
        }
        if (position != -1) {
            lvMyTask.setSelection(position);
            rlSign.setVisibility(View.VISIBLE);
            //如果运行中，设置按钮不存在
            HolderTrip holderTrip = CommonData.holderTrip;
            boolean isRun = holderTrip != null && holderTrip.getTrip().getStatus() == Constants.STATUS_DEPART;
            T.log(isRun+"");
            lvMyTask.setSelection(position);
            if (isRun) {
                tvStartSign.setVisibility(View.GONE);
                rlEndSign.setVisibility(View.VISIBLE);
            } else {
                tvStartSign.setVisibility(View.VISIBLE);
                rlEndSign.setVisibility(View.GONE);
            }
        } else {
            rlSign.setVisibility(View.GONE);
        }
        setHomeState();
    }

    /**
     * 首页刷新提示状态
     */
    private void setHomeState() {
        if (state == Constants.WAIT_FINISH) {
            String state = UtilState.getState();
            boolean a = state.equals(StateConstants.NO_TRIP_FIXED) ||
                    state.equals(StateConstants.NO_TRIP_ROLLING) ||
                    state.equals(StateConstants.NO_TRIP_ROLLING_FIXED);
            if (!a) {
                freshState(state);
            } else {
                freshState(StateConstants.GET_STATE_FAIL);
            }
        }
    }

    /**
     * 筛选完成未完成的班次
     *
     * @return taskList
     */
    private List<TaskDetail> getTaskList() {
        position = -1;
        List<TaskDetail> chooseTask = new ArrayList<>();
        int j = 0;
        if (CommonData.listTask != null) {
            for (TaskDetail task : CommonData.listTask) {
                if (state == Constants.WAIT_FINISH &&
                        (task.getStatus() == Constants.STATUS_PRE_DEPART ||
                                task.getStatus() == Constants.STATUS_DEPART)) {
                    chooseTask.add(task);
                    HolderTrip holderTrip = CommonData.holderTrip;
                    boolean active = holderTrip != null && holderTrip.getTrip().getId().equals(task.getId());
                    if (active) {
                        position = j;
                    }
                    j++;
                } else if (state == Constants.FINISH &&
                        task.getStatus() == Constants.STATUS_ARRIVE) {
                    chooseTask.add(task);
                }
            }
        }
        return chooseTask;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        TaskDetail taskDetail = baseAdapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra("clickId", taskDetail.getId());
        intent.setClass(HomePageActivity.this, RunMapActivity.class);
        startActivity(intent);
    }


    /**
     * 监听按键
     *
     * @param keyCode 返回键的id
     * @param event   event
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //监听返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showExitDialog() {
        XMDialog.showDialog(HomePageActivity.this, "是否退出打卡系统", new XMDialog.DialogResult() {
            @Override
            public void clickResult(int resultCode) {
                if (resultCode == XMDialog.CLICK_SURE) {
//                    BusService.getInstance().stopForeground(true);
//                    stopService(BusService.createIntent(getBaseContext()));
                    DriverApplication.getApplication().requestToClose();
                    UtilTool.clearExit();
                    System.exit(0);
                    finish();
                }
            }
        });
    }

    private void checkPhoneState(){
        if (!UtilTool.isNotificationEnabled()){
            showNoticeDialog();
        }
    }

    public void showNoticeDialog() {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限开启提醒");
        builder.setPositiveButton(R.string.setting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UtilTool.goToNotificationSetting(HomePageActivity.this);
                        dialog.dismiss();
                    }
                });
        builder.setMessage(R.string.open_notification_tip);
        Dialog noticeDialog = builder.create();
        noticeDialog.setCancelable(false);
        noticeDialog.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        homePageActivity = null;
    }

    /**
     * 获取定位权限
     */
    public void getPermissions() {
        //6.0权限判断
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION}, GET_PERMISSIONS);
        } else {

//            new UpdateVersion(this).update();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10012){
            checkIsAndroidO();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case GET_PERMISSIONS:
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    T.showShort(getString(R.string.get_permission_error));
                }
//                if(Build.VERSION.SDK_INT >= 26&&!getPackageManager().canRequestPackageInstalls()){
//                    checkIsAndroidO();
//                }else {
////                    new UpdateVersion(this).update();
//                }

                break;
            case INSTALL_PACKAGES_REQUESTCODE:
                if(grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    if(Build.VERSION.SDK_INT>=26) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                        startActivityForResult(intent, 10012);
                    }
                }else {
                    showUpdateDialog();
//                    new UpdateVersion(this).update();
                }
                break;
            default:
        }
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        showMessage();
    }

    /**
     * 加载列表
     */
    public void loadTask() {
        if(dialog!=null){
            dialog.show();
        }
//        UtilTool.refreshTodayTask();
        UtilTool.getInstance().getSchedules();
    }

    //刷新列表
    public void refreshTask() {
        handle.sendEmptyMessage(REFRESH_GET_TASK);
    }

    private void operateBus(){
        mCompositeDisposable.add(RxBus.getInstance().tObservable(UpdateEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UpdateEvent>() {
                    @Override
                    public void accept(UpdateEvent updateEvent) throws Exception {
                        Log.i("Home", updateEvent.toString());
                        refreshTask();
                        if(updateEvent.isShow()){
                            if(dialog!=null&&!isFinishing()){
                                Log.i("Home", "updateEvent.isShow(): "+updateEvent.isShow());
                                dialog.show();
                            }
                        }else {
                            if(dialog!=null&&!isFinishing()){
                                dialog.dismiss();
                            }
                        }

                    }
                })
        );


    }

    /**
     * 显示未读提示
     */
    public void showMessage() {
        if (CommonData.notReadNumber == 0) {
            isRead.setVisibility(View.GONE);
        } else {
            isRead.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        CommonData.holderTrip = null;
//        UtilTool.refreshTodayTask();
        UtilTool.getInstance().getSchedules();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refresh.isRefreshing()) {
                    refresh.setRefreshing(false);
                    T.showShort(getString(R.string.fresh_failure));
                }
            }
        }, 10000);
    }

    /**
     * 判断是否是第一次进入
     *
     * @return boolean
     */
    private boolean dealIsFirst() {
        String pName = "com.meibanlu.driver";
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(pName, PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String nowVersion = packageInfo != null ? packageInfo.versionName : null;
        String shareVersion = SharePreData.getInstance().getStrData("shareVersion");
        if (!TextUtils.isEmpty(nowVersion)) {
            CommonData.version = nowVersion;
        }
        if (shareVersion == null || !shareVersion.equals(nowVersion)) {
            SharePreData.getInstance().addStrData("shareVersion", nowVersion);
            //启动线程删除apk文件
            dealApkFile();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 启动线程，删除不用的apk文件
     */
    private void dealApkFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String downloadPath = FilePath.getAppRoute();
                    File folder = new File(downloadPath);
                    if (folder.exists() && folder.isDirectory()) {
                        File[] files = folder.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                return pathname.getName().endsWith(".apk");
                            }
                        });
                        //删除
                        for (File file : files) {
                            file.delete();
                        }
                    }
                } catch (Exception e) {
                    Log.e("delete apk exception: ", e.getLocalizedMessage());
                }
            }
        }).start();
    }


    //移动的图片
    private int moveX;
    private int moveY;
    private int lastX;
    private int lastY;
    private int maxRight;
    private int maxBottom;
    private long currentMS;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //得到事件的坐标
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();
        T.log("eventX" + eventX);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //得到父视图的right/bottom
                //保证只赋一次值
                if (maxRight == 0) {
                    maxRight = CommonData.windowWidth;
                    maxBottom = CommonData.WindowHeight;
                }
                //第一次记录lastX/lastY
                lastX = eventX;
                lastY = eventY;
                moveX = 0;
                moveY = 0;
                //long currentMS     获取系统时间
                currentMS = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                //移动时间
                long moveTime = System.currentTimeMillis() - currentMS;
                //判断是否继续传递信号
                if (moveTime < 200 && moveX < 20 && moveY < 20) {
                    T.startActivity(PersonalActivity.class);
                    return true;
                    //不再执行后面的事件，在这句前可写要执行的点击相关代码。
                    // 点击事件是发生在触摸弹起后
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //计算事件的偏移
                int dx = eventX - lastX;
                int dy = eventY - lastY;
                //根据事件的偏移来移动imageView
                int left = rlPersonal.getLeft() + dx;
                int top = rlPersonal.getTop() + dy;
                int right = rlPersonal.getRight() + dx;
                int bottom = rlPersonal.getBottom() + dy;
                //限制left >=0
                if (left < 0) {
                    right += -left;
                    left = 0;
                }
                //限制top
                if (top < 0) {
                    bottom += -top;
                    top = 0;
                }
                //限制right <=maxRight
                if (right > maxRight) {
                    left -= right - maxRight;
                    right = maxRight;
                }
                //限制bottom <=maxBottom
                if (bottom > maxBottom) {
                    top -= bottom - maxBottom;
                    bottom = maxBottom;
                }
                rlPersonal.layout(left, top, right, bottom);
                //再次记录lastX/lastY
                lastX = eventX;
                lastY = eventY;
                break;
            default:
                break;
        }
        //所有的motionEvent都交给imageView处理
        return true;
    }

    /**
     * 刷新提示显示状态
     *
     * @param str str
     */
    public void freshState(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvState.setText(str);
            }
        });
    }

    private void openWifi(){
        WifiManager wifiManager= (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
    }

    private int versionCode=0;
    private String type;
    private CompositeDisposable mCompositeDisposable;

    private void checkVersion(){
        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        GetVersionRequest request=new GetVersionRequest();
        Header header=new Header();
        body.versionRequest=request;
        envelope.header=header;
        envelope.body=body;
        mCompositeDisposable.add(
                RetrofitGenerator.getInstance().getApiStore()
                        .checkVersion(envelope)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseEnvelope>() {
                            @Override
                            public void accept(ResponseEnvelope responseEnvelope) throws Exception {
                                String response = responseEnvelope.body.versionResponse.model.value;
                                if (response.contains(",")) {
                                    versionCode = Integer.valueOf(response.split(",")[0]);
                                    type = response.split(",")[1];
                                    if (versionCode > UtilTool.getLocalVersion()) {
                                        if (Build.VERSION.SDK_INT >= 26 && !HomePageActivity.this.getPackageManager().canRequestPackageInstalls()) {
                                            checkIsAndroidO();
                                        } else {
//                                            update();
                                            showUpdateDialog();
                                        }
                                    }
                                }else {
                                    versionCode = Integer.valueOf(response);
                                    if (versionCode > UtilTool.getLocalVersion()) {
                                        if (Build.VERSION.SDK_INT >= 26 && !HomePageActivity.this.getPackageManager().canRequestPackageInstalls()) {
                                            checkIsAndroidO();
                                        } else {
//                                            update();
                                            showUpdateDialog();
                                        }
                                    }
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e("", "" + throwable.getMessage());
//                    MobclickAgent.reportError(LoginActivity.this, throwable);
                            }
                        })
        );

    }
    private void checkIsAndroidO() {
        if (Build.VERSION.SDK_INT >= 26) {
            boolean b = getPackageManager().canRequestPackageInstalls();
            if (!b) {
                //请求安装未知应用来源的权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUESTCODE);
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialog!=null){
            dialog.dismiss();
        }
        mCompositeDisposable.clear();
    }

    private void update(){
        if(versionCode==0){
            return;
        }
        UpdateEntity entity = new UpdateEntity(versionCode, "");
        UpdateManager manager = new UpdateManager(HomePageActivity.this, entity);
        manager.showNoticeDialog();
    }

    /**
     * 显示软件更新对话框
     */
    private void showUpdateDialog() {
        XMDialog.showDialog("更新提示", "您好检测到新的版本，是否更新到最新版本", XMDialog.NO_TITLE, new XMDialog.DialogResult() {
            @Override
            public void clickResult(int resultCode) {
                if (resultCode == CLICK_SURE) {
                    // 显示下载对话框
                    Intent i = new Intent(HomePageActivity.this, UpdateService.class);
                    i.putExtra("url", Constants.Update_URL);
                    startService(i);
                }
            }
        });


    }

//    private void getState(final String code){
//        RequestEnvelope envelope=new RequestEnvelope();
//        RequestBody body=new RequestBody();
//        final GetCodeStateRequest request=new GetCodeStateRequest();
//        request.code=code;
//        Header header=new Header();
//        body.codeStateRequest=request;
//        envelope.header=header;
//        envelope.body=body;
//        WebService.getInstance().getData(envelope, "urn:TYWJAPPIntf-ITYWJAPP#getjjqianbao", new DataCallBack() {
//            @Override
//            public void success(ResponseEnvelope result) {
//                if(result.body!=null){
//                    if(result.body.codeStateResponse.model.value!=null){
//                        String data=result.body.codeStateResponse.model.value;
//                        if(data.equalsIgnoreCase("未使用")){
//                            updateCode(code);
//                        }else if(data.equalsIgnoreCase("已兑换")){
//                            cancel(code);
//                        }else {
//                            T.hideLoading();
//                            T.showShort(data);
//                        }
//                    }
//                }
//
//            }
//            @Override
//            public void error(String responseMessage) {
//                T.hideLoading();
//                T.showShort("扫码失败，请重新扫描");
//            }
//        });
//    }
//
//    private void updateCode(String code){
//        RequestEnvelope envelope=new RequestEnvelope();
//        RequestBody body=new RequestBody();
//        final UpdateCodeRequest request=new UpdateCodeRequest();
//        request.setCode(code);
//        request.setPhone(SharePreferenceUtil.getInstance().getString("phone"));
//        Header header=new Header();
//        body.codeRequest=request;
//        envelope.header=header;
//        envelope.body=body;
//        WebService.getInstance().getData(envelope, "urn:TYWJAPPIntf-ITYWJAPP#getjjqianbao", new DataCallBack() {
//            @Override
//            public void success(ResponseEnvelope result) {
//                T.hideLoading();
//                if(result.body!=null){
//                    if(result.body.codeResponse.model.value!=null){
//                        String data=result.body.codeResponse.model.value;
//                        if(data.equalsIgnoreCase("ok")){
//                            T.showShort("兑换成功");
//                            detail.setText("兑换成功");
//                        }else {
//                            T.showShort("兑换失败");
//                            detail.setText("兑换失败");
//                        }
//                    }
//                }
//
//            }
//            @Override
//            public void error(String responseMessage) {
//                T.hideLoading();
//                detail.setText("兑换失败");
//                T.showShort("兑换失败，请重新扫描");
//            }
//        });
//    }
//
//    private void cancel(String code){
//        RequestEnvelope envelope=new RequestEnvelope();
//        RequestBody body=new RequestBody();
//        final CancelCodeRequest request=new CancelCodeRequest();
//        request.setCode(code);
//        request.setPhone(SharePreferenceUtil.getInstance().getString("phone"));
//        Header header=new Header();
//        body.cancelCodeRequest=request;
//        envelope.header=header;
//        envelope.body=body;
//        WebService.getInstance().getData(envelope, "urn:TYWJAPPIntf-ITYWJAPP#getjjqianbao", new DataCallBack() {
//            @Override
//            public void success(ResponseEnvelope result) {
//                T.hideLoading();
//                if(result.body!=null){
//                    if(result.body.cancelCodeResponse.model.value!=null){
//                        String data=result.body.cancelCodeResponse.model.value;
//                        if(data.equalsIgnoreCase("ok")){
//                            T.showShort("取消兑换成功");
//                            detail.setText("取消兑换成功");
//                        }else {
//                            T.showShort("取消兑换失败");
//                            detail.setText("取消兑换失败");
//                        }
//                    }
//                }
//
//            }
//            @Override
//            public void error(String responseMessage) {
//                T.hideLoading();
//                T.showShort("取消兑换失败");
//                detail.setText("取消兑换失败");
//            }
//        });
//    }


}
