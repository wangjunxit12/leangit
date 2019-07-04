package com.meibanlu.driver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.bean.LineStation;
import com.meibanlu.driver.bean.TaskDetail;
import com.meibanlu.driver.bean.TodayTaskBean;
import com.meibanlu.driver.tool.JSONFactory;
import com.meibanlu.driver.tool.SharePreData;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.TimeTool;
import com.meibanlu.driver.tool.datepicker.DateTool;
import com.meibanlu.driver.tool.web.SimpleCallBack;
import com.meibanlu.driver.tool.web.WebInterface;
import com.meibanlu.driver.tool.web.WebService;
import com.meibanlu.driver.webservice.RetrofitGenerator;
import com.meibanlu.driver.webservice.mappers.ScheduleMapper;
import com.meibanlu.driver.webservice.mappers.StationMapper;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.LinesRequest;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.requeset.StationRequest;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 行车记录
 * Created by lhq on 2017/9/25.
 */

public class RouteDepartureActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView lvRouteRecord;
    private TextView tvDay, tvTimes, realityMileage, tvMileage;
    private String strDate;//显示的date
    private LinearLayout llTitle;
    private View viewBackground;
    private final int ROUTE_DATA = 1;
    private final int EMPTY_ROUTE_DATA = 2;
    private boolean depart;
    @NonNull
    private CompositeDisposable mCompositeDisposable=new CompositeDisposable();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ROUTE_DATA:
                    viewBackground.setBackgroundResource(R.color.F7F7F7);
                    List<TaskDetail> bean = (List<TaskDetail>) msg.obj;
                    initAdapter(bean);
                    break;
                case EMPTY_ROUTE_DATA:
                    initAdapter(new ArrayList<TaskDetail>());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_record);
        initView();
        initData();
    }

    private void initView() {
        lvRouteRecord = (ListView) findViewById(R.id.lv_all_route_record);//班次打卡
        tvTimes = (TextView) findViewById(R.id.tv_times);//次数
        realityMileage = (TextView) findViewById(R.id.tv_reality_mileage);//实际总的里程
        tvMileage = (TextView) findViewById(R.id.tv_mileage);//预计总的里程
        tvDay = (TextView) findViewById(R.id.tv_day);//日期
        llTitle = (LinearLayout) findViewById(R.id.ll_title);
        viewBackground = findViewById(R.id.ll_layout);
        lvRouteRecord.setOnItemClickListener(this);
        registerBtn(tvDay);
    }

    private void initData() {
        depart = getIntent().getBooleanExtra("routeDepart", false);
        setTitle(getString(depart ? R.string.route_departure : R.string.driving_record));
        initDate();
        lvRouteRecord.setEmptyView((findViewById(R.id.empty_view)));
    }

    /**
     * 初始化日期
     */
    private void initDate() {
        String toMorrow = getIntent().getStringExtra("toMorrow");
        if (TextUtils.isEmpty(toMorrow)) {
            strDate = TimeTool.getCurrentTime("yyyy-MM-dd");
        } else {
            strDate = TimeTool.getTomorrowTime("yyyy-MM-dd");
        }
//        getDriveRoute();
        getSchedules();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_day:
                new DateTool(this, new DateTool.DatePickerResult() {
                    @Override
                    public void clickResult(String date) {
                        strDate = date;
                        getSchedules();
//                        getDriveRoute();
                    }
                });
                break;
            default:
        }
    }



    private Map<Integer, TaskDetail> map = new HashMap<>();
    private int index = 0;

    public void getSchedules() {
        String date = strDate.replace("-", "/");
        tvDay.setText(date);
        T.showLoading();
        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        LinesRequest request=new LinesRequest();
        Header header=new Header();
        request.cityNumber= SharePreData.getInstance().getStrData("id");
        request.date=strDate;
        body.linesRequest=request;
        envelope.header=header;
        envelope.body=body;
        mCompositeDisposable.add(RetrofitGenerator.getInstance().getApiStore().getSchedules(envelope)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseEnvelope, List<TaskDetail>>() {
                    @Override
                    public List<TaskDetail> apply(ResponseEnvelope responseEnvelopeResult) throws Exception {
                        return new ScheduleMapper().transform(responseEnvelopeResult);
                    }
                })
                .subscribe(new Consumer<List<TaskDetail>>() {
                    @Override
                    public void accept(List<TaskDetail> schedules) throws Exception {
                        T.hideLoading();
                        if(schedules!=null&&schedules.size()>0){
                            if (schedules.size() > 0) {
                                Collections.sort(schedules);
                                Message msg = Message.obtain();
                                msg.obj = schedules;
                                msg.what = ROUTE_DATA;
                                handler.sendMessage(msg);
                            } else {
                                handler.sendEmptyMessage(EMPTY_ROUTE_DATA);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        T.hideLoading();
                        handler.sendEmptyMessage(EMPTY_ROUTE_DATA);
                    }
                })

        );
    }

    private void initAdapter(List<TaskDetail> allBean) {
        final List<TaskDetail> bean;
        if (depart) {
            bean = new ArrayList<>();
            for (TaskDetail recordBean : allBean) {
                if (recordBean.getShiftingTimes() != null && recordBean.getShiftingTimes() > 30) {
                    bean.add(recordBean);
                }
            }
        } else {
            bean = allBean;
        }
        if (bean.size() > 0) {
            llTitle.setVisibility(View.VISIBLE);
            tvTimes.setText(bean.size() + "");
            double allPlanDis = 0;//所有的计划的路线长度
            int allRealityDis = 0;//所有的实际的路线长度
            for (TaskDetail item : bean) {
                if (item.getLine() != null) {
                    String planDis = item.getLine().getDistance(); //预定
                    int realityDis = item.getDistance(); //实际
                    if (!TextUtils.isEmpty(planDis)) {
                        double longPlanDis = Double.parseDouble(planDis);
                        allRealityDis += realityDis;
                        allPlanDis += longPlanDis;
                    }
                }
            }
            String realtyDis = intToStr(allRealityDis);
            tvMileage.setText(new DecimalFormat("#0.00").format(allPlanDis)); //计划公里数
            realityMileage.setText(realtyDis);
        } else {
            llTitle.setVisibility(View.GONE);
        }
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return bean.size();
            }

            @Override
            public TaskDetail getItem(int position) {
                return bean.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View v, ViewGroup viewGroup) {
                TaskDetail item = getItem(position);
                View view = View.inflate(RouteDepartureActivity.this, R.layout.item_all_route, null);
                TextView tvCarNumber = (TextView) view.findViewById(R.id.tv_car_number);//车牌号
                TextView tvStartStation = (TextView) view.findViewById(R.id.tv_start_station);//出发点
                TextView tvEndStation = (TextView) view.findViewById(R.id.tv_end_station);//到站点
                TextView tvDepartTime = (TextView) view.findViewById(R.id.tv_depart_time);//出发时间
                TextView tvArriveTime = (TextView) view.findViewById(R.id.tv_arrive_time);//到站时间
                TextView tvPlanDistance = (TextView) view.findViewById(R.id.tv_plan_distance);//应跑距离
                TextView tvRealityDistance = (TextView) view.findViewById(R.id.tv_reality_distance);//实跑距离
                TextView tvShiftTime = (TextView) view.findViewById(R.id.tv_shift_time);//班次时间
                TextView tvSeats = (TextView) view.findViewById(R.id.tv_seats);//班次时间
                tvCarNumber.setText(item.getCarNumber());
                tvShiftTime.setText(item.getSchedule());
                tvStartStation.setText(item.getDepartStation().getName());
                tvEndStation.setText(item.getArriveStation().getName());
                tvSeats.setText(String.valueOf(item.getOccupiedSeats()));
                tvDepartTime.setText(TimeTool.stampToDate(item.getDepartTime(), "(HH:mm)"));
                tvArriveTime.setText(TimeTool.stampToDate(item.getArriveTime(), "(HH:mm)"));
                if (item.getLine() != null) {
                    tvPlanDistance.setText(item.getLine().getDistance() + "km");
                }
                tvRealityDistance.setText(intToStr(item.getDistance()) + "km");
                return view;
            }
        };
        lvRouteRecord.setAdapter(baseAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        TaskDetail bean = ((TaskDetail) adapterView.getAdapter().getItem(position));
        Intent intent = new Intent();
        //是否是偏离
        intent.putExtra("routeDepart", depart);
        intent.putExtra("departTime", bean.getDepartTime());
        intent.putExtra("arriveTime", bean.getArriveTime());
        intent.putExtra("tripLineFile", bean.getTripLineFile());
        intent.putExtra("startStation", bean.getDepartStation().getName());
        intent.putExtra("endStation", bean.getArriveStation().getName());
        if(bean.getLine()!=null) {
            intent.putExtra("planDistance", bean.getLine().getDistance());
        }
        intent.putExtra("startId", bean.getDepartStation().getId());
        intent.putExtra("endId", bean.getArriveStation().getId());
        intent.putExtra("realtyDistance", intToStr(bean.getDistance()));
        intent.setClass(RouteDepartureActivity.this, RouteDepartureMapActivity.class);
        startActivity(intent);
    }

    /**
     * 将int类型距离米转换成km
     *
     * @param allRealityDis 距离
     * @return str距离km
     */
    private String intToStr(int allRealityDis) {
        double longRealtyDis = (double) allRealityDis / 1000;
        return new java.text.DecimalFormat("#0.00").format(longRealtyDis);
    }
}
