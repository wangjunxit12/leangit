package com.meibanlu.driver.adapter;

import android.content.Intent;
import android.os.Bundle;
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
import com.meibanlu.driver.R;
import com.meibanlu.driver.activity.HomePageActivity;
import com.meibanlu.driver.activity.PhoneActivity;
import com.meibanlu.driver.activity.ScanActivity;
import com.meibanlu.driver.activity.WebActivity;
import com.meibanlu.driver.bean.LineStation;
import com.meibanlu.driver.bean.TaskDetail;
import com.meibanlu.driver.bean.UpdateEvent;
import com.meibanlu.driver.tool.CommonData;
import com.meibanlu.driver.tool.Constants;
import com.meibanlu.driver.tool.GpsTool;
import com.meibanlu.driver.tool.HolderTrip;
import com.meibanlu.driver.tool.MapUtil;
import com.meibanlu.driver.tool.RxBus;
import com.meibanlu.driver.tool.ScheduleTaskDaemon;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.TimeTool;
import com.meibanlu.driver.tool.UtilTool;
import com.meibanlu.driver.tool.XMDialog;
import com.meibanlu.driver.view.AutoNextLineLinearlayout;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * @author Administrator
 */
public class TaskListAdapter extends BaseAdapter {

    private List<TaskDetail> taskList;

    private HomePageActivity activity;


    private CompositeDisposable mCompositeDisposable;

    private List<LineStation> stations;
    public TaskListAdapter(List<TaskDetail> taskList, HomePageActivity activity) {
        this.taskList = taskList;
        this.activity = activity;
        mCompositeDisposable=new CompositeDisposable();
//        operateRx();
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public TaskDetail getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final TaskDetail item = getItem(position);
        ViewHolder viewHolder;
        if (view == null) {
            view = View.inflate(activity, R.layout.item_not_finish_task, null);
            viewHolder = new ViewHolder();
            viewHolder.tvLineTime = (TextView) view.findViewById(R.id.tv_line_time);
            viewHolder.endSign = (TextView) view.findViewById(R.id.end_sign);
            viewHolder.tvPerson = (TextView) view.findViewById(R.id.tv_person);
            viewHolder.tvSeats = (TextView) view.findViewById(R.id.tv_seats);
            viewHolder.circlePoint = view.findViewById(R.id.circle_point);
            viewHolder.tvIsFinish = (TextView) view.findViewById(R.id.tv_is_finish);
            viewHolder.ivRoll = (ImageView) view.findViewById(R.id.iv_roll);
            viewHolder.tvStartStation = (TextView) view.findViewById(R.id.tv_start_station);
            viewHolder.stationLine = view.findViewById(R.id.line);
            viewHolder.tvEndStation = (TextView) view.findViewById(R.id.tv_end_station);
            viewHolder.tvSetTrip = (TextView) view.findViewById(R.id.tv_set_trip);
            viewHolder.rlOut = (RelativeLayout) view.findViewById(R.id.rl_out);
            viewHolder.rlCurrent = (RelativeLayout) view.findViewById(R.id.rl_current);
            viewHolder.tvOtherStation = (TextView) view.findViewById(R.id.tv_other_station);
            viewHolder.llOtherStation = view.findViewById(R.id.ll_other_station);
            viewHolder.tvItinerary = (TextView) view.findViewById(R.id.tv_itinerary);
            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tvRoll = (TextView) view.findViewById(R.id.tv_roll);
            viewHolder.distance = (TextView) view.findViewById(R.id.distance);
            viewHolder.listView=view.findViewById(R.id.item_list);
            viewHolder.scan=view.findViewById(R.id.scan);

            viewHolder.scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) v.getTag();
                    TaskDetail detail=getItem(pos);
                    Intent intent=new Intent();
                    intent.putExtra("id",detail.getId());
                    intent.setClass(activity, ScanActivity.class);
                    activity.startActivity(intent);
                }
            });

            viewHolder.tvItinerary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent();
                    intent.setClass(activity,PhoneActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("id",item.getId());
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }
            });
            viewHolder.endSign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) v.getTag();
                    final TaskDetail taskDetail=getItem(pos);
                    if(CommonData.aMapLocation==null){
                        T.showShort("定位失败");
                        return;
                    }
                    boolean in= GpsTool.checkPointInPolygon(
                            CommonData.aMapLocation.getLongitude() + ";" + CommonData.aMapLocation.getLatitude(),
                            taskDetail.getArriveStation().getLngLat(),
                            taskDetail.getArriveStation().getAreaRadius());
                    if(in){
                        ScheduleTaskDaemon.sign(taskDetail.toHolderTrip(), Constants.STATUS_ARRIVE, null, null, Constants.MODE_MAN_SUCCESS);
                    }else {
                        XMDialog.showDialog(activity, "不在打卡范围内,确认打异常卡", new XMDialog.DialogResult() {
                            @Override
                            public void clickResult(int resultCode) {
                                    if (resultCode == XMDialog.CLICK_SURE) {
                                        ScheduleTaskDaemon.sign(taskDetail.toHolderTrip(), Constants.STATUS_ARRIVE, null, null, Constants.MODE_MAN_ABNORMAL);
                                    }
                            }
                        });
                    }
                }
            });
            viewHolder.tvSetTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence text = ((TextView) v).getText();
                    int pos = (int) v.getTag();
                    if (T.getStringById(R.string.cancel_current).equals(text)) {
                        CommonData.holderTrip = null;
//                        UtilTool.refreshTodayTask();
                        notifyDataSetChanged();
                    } else if (T.getStringById(R.string.set_current).equals(text)) {
                        CommonData.holderTrip = getItem(pos).toHolderTrip(true);
                        notifyDataSetChanged();
                    }
                    activity.initTaskAdapter();
                }
            });
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvSetTrip.setTag(position);
        viewHolder.endSign.setTag(position);
        viewHolder.scan.setTag(position);
        //定义一堆变量表示颜色
        int scheduleStationColor, personSeatColor, statusColor, setTextColor, rollColor;
        //首先全部赋值为 333
        scheduleStationColor = personSeatColor = statusColor = setTextColor = rollColor = T.getColorById(R.color.text_color_cccccc);

        int circleDraw = R.drawable.shape_circle_gray;

        int rollMipmap = R.mipmap.ic_finish_roll;

        int shapeSetTrip = R.drawable.shape_set_shift;

        int statusText = R.string.have_finish;

        int setText = R.string.set_current;


        //如果运行中，设置按钮不存在
        HolderTrip holderTrip = CommonData.holderTrip;

        //当前HolderTrip 高亮
        boolean active = holderTrip != null && holderTrip.getTrip().getId().equals(item.getId());
        viewHolder.rlCurrent.setBackgroundResource(active ? R.drawable.shape_run_content : R.drawable.shape_white);
        viewHolder.rlOut.setBackgroundResource(active ? R.drawable.shape_run_title : R.drawable.shape_task);
        DecimalFormat df = new DecimalFormat("#.000");

        //设置按钮的单独处理
        boolean showSetTrip = holderTrip == null;
        if (showSetTrip){
            //不能兼容跨夜的问题
            String time = TimeTool.getCurrentTime("HH:mm");
            //处理正常班次
            String scheduleTime = item.getSchedule();
            int duration = TimeTool.getDistanceTimesSign(time, scheduleTime);
            if (duration >= Constants.SET_TRIP_SELECT_TIME_LEFT && duration <= Constants.SET_TRIP_SELECT_TIME_RIGHT) {
                viewHolder.tvSetTrip.setVisibility(View.VISIBLE);
            }else {
                viewHolder.tvSetTrip.setVisibility(View.INVISIBLE);
            }
            if(item.isRoll()){
                viewHolder.tvSetTrip.setVisibility(View.VISIBLE);
            }
        } else {
            if(!item.getId().equals(holderTrip.getTrip().getId())){
                viewHolder.tvSetTrip.setVisibility(View.INVISIBLE);
            }else {
                viewHolder.tvSetTrip.setVisibility(View.VISIBLE);
            }
        }
        if(!item.isRoll()&&item.getStatus()==Constants.STATUS_DEPART){
            viewHolder.tvSetTrip.setVisibility(View.INVISIBLE);
        }
        if (holderTrip == null ||!item.getId().equals(holderTrip.getTrip().getId())) {
            setText = R.string.set_current;
            shapeSetTrip = R.drawable.shape_set_shift;
            setTextColor = T.getColorById(R.color.text_color_333333);
        } else {
            setText = R.string.cancel_current;
            shapeSetTrip = R.drawable.shape_cancel_shift;
            setTextColor = T.getColorById(R.color.white);
        }
        //完成变成隐藏
        if (item.getStatus() == Constants.STATUS_ARRIVE) {
            viewHolder.tvSetTrip.setVisibility(View.INVISIBLE);
        }

        switch (item.getStatus()) {
            case Constants.STATUS_PRE_DEPART:
                scheduleStationColor = T.getColorById(R.color.text_color_333333);
                personSeatColor = T.getColorById(R.color.text_color_999999);
                statusColor = T.getColorById(R.color.textfd8900);
                circleDraw = R.drawable.shape_circle_red;
                rollMipmap = R.mipmap.ic_roll;
                rollColor = T.getColorById(R.color.textFdd000);
                statusText = R.string.not_finish;
                viewHolder.endSign.setVisibility(View.GONE);
                if (active) {
                    statusText = R.string.steam_car;
                }
                break;
            case Constants.STATUS_DEPART:
                if(active) {
                    scheduleStationColor = T.getColorById(R.color.text_color_333333);
                    personSeatColor = T.getColorById(R.color.text_color_999999);
                    statusColor = T.getColorById(R.color.text00d4b4);
                    circleDraw = R.drawable.shape_circle_blue;
                    rollMipmap = R.mipmap.ic_roll;
                    rollColor = T.getColorById(R.color.text_color_999999);
                    statusText = R.string.run;
                    viewHolder.endSign.setVisibility(View.VISIBLE);
                }else {
                    scheduleStationColor = T.getColorById(R.color.text_color_333333);
                    personSeatColor = T.getColorById(R.color.text_color_999999);
                    statusColor = T.getColorById(R.color.textfd8900);
                    circleDraw = R.drawable.shape_circle_red;
                    rollMipmap = R.mipmap.ic_roll;
                    rollColor = T.getColorById(R.color.textFdd000);
                    statusText = R.string.not_finish;
                    viewHolder.endSign.setVisibility(View.GONE);
                }
                break;
            case Constants.STATUS_ARRIVE:
                viewHolder.endSign.setVisibility(View.GONE);
                break;
            default:
        }
        //滚动班次圆圈
        viewHolder.ivRoll.setVisibility(item.isRoll() ? View.VISIBLE : View.INVISIBLE);
        viewHolder.tvRoll.setVisibility(item.isRoll() ? View.VISIBLE : View.INVISIBLE);

        viewHolder.tvLineTime.setText(item.getSchedule());
        viewHolder.tvLineTime.setTextColor(scheduleStationColor);

        viewHolder.tvPerson.setTextColor(personSeatColor);

        if(item.getOccupiedSeats()!=null){
            viewHolder.tvSeats.setText(String.valueOf(item.getOccupiedSeats()));
        }
//        viewHolder.tvSeats.setTextColor(personSeatColor);

        viewHolder.circlePoint.setBackgroundResource(circleDraw);

        viewHolder.tvIsFinish.setText(statusText);
        viewHolder.tvIsFinish.setTextColor(statusColor);
        if(item.getDepartStation()!=null){
            viewHolder.tvStartStation.setText(item.getDepartStation().getName());
        }
        viewHolder.tvStartStation.setTextColor(scheduleStationColor);

        viewHolder.stationLine.setBackgroundColor(scheduleStationColor);

        if(item.getArriveStation()!=null){
            viewHolder.tvEndStation.setText(item.getArriveStation().getName());
        }
        viewHolder.tvEndStation.setTextColor(scheduleStationColor);

        viewHolder.ivRoll.setImageResource(rollMipmap);
        viewHolder.tvRoll.setTextColor(rollColor);

        viewHolder.tvSetTrip.setText(setText);
        viewHolder.tvSetTrip.setBackgroundResource(shapeSetTrip);
        viewHolder.tvSetTrip.setTextColor(setTextColor);

        String name = item.getInstructorName();
        if (!TextUtils.isEmpty(name)) {
            viewHolder.tvName.setText(name);
        } else {
            viewHolder.tvName.setText("无");
        }
        if(item.isCircle()) {
            viewHolder.listView.setVisibility(View.VISIBLE);
        }else {
            viewHolder.listView.setVisibility(View.GONE);
        }
        return view;
    }

    public List<TaskDetail> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskDetail> taskList) {
        this.taskList = taskList;
    }

    class ViewHolder {
        TextView tvLineTime, tvPerson, tvSeats,
                tvIsFinish, tvStartStation,
                tvEndStation, tvSetTrip,
                tvOtherStation, tvRoll,
                tvItinerary, tvName,endSign,distance;
        View circlePoint, stationLine;
        ImageView ivRoll;
        RelativeLayout llOtherStation;
        RelativeLayout rlOut, rlCurrent;
        Button scan;
        LinearLayout listView;
    }

//    public  void  operateRx(){
//        mCompositeDisposable.add(RxBus.getInstance().tObservable(AMapLocation.class)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<AMapLocation>() {
//                    @Override
//                    public void accept(AMapLocation location) throws Exception {
//                        if(CommonData.holderTrip!=null){
//                            if(CommonData.holderTrip.getTrip().getBackupStations()!=null){
//                                stations=CommonData.holderTrip.getTrip().getBackupStations();
//                            }
//                        }
//                        notifyDataSetChanged();
//                    }
//                })
//        );
//
//    }


}
