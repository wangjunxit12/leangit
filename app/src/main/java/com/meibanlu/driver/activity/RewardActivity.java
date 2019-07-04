package com.meibanlu.driver.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.bean.Bonus;
import com.meibanlu.driver.bean.RewardBean;
import com.meibanlu.driver.tool.SharePreData;
import com.meibanlu.driver.tool.TimeTool;
import com.meibanlu.driver.tool.datepicker.DateTool;
import com.meibanlu.driver.tool.web.CallBack;
import com.meibanlu.driver.tool.web.DataCallBack;
import com.meibanlu.driver.tool.web.SimpleCallBack;
import com.meibanlu.driver.tool.web.WebInterface;
import com.meibanlu.driver.tool.web.WebService;
import com.meibanlu.driver.webservice.mappers.BonusMapper;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.LinesRequest;
import com.meibanlu.driver.webservice.requeset.MoneyRequest;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.meibanlu.driver.tool.web.WebService.GET;

/**
 * 绩效
 * Created by lhq on 2017-11-23.
 */

public class RewardActivity extends BaseActivity {
    private final static int GET_SUCCESS = 0;
    private final static int GET_EMPTY = 1;
    private TextView tvDay, tvAllReward, tvEmpty, tvTodayReward,type;
    private ListView lvReward;
    private LinearLayout llListView;
    private String day;
    BaseAdapter rewardAdapter;
    List<Bonus> bonus;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_SUCCESS:
                    RewardBean bean = (RewardBean) msg.obj;
                    List<Bonus> getBonus = bean.getBonus();
                    if (getBonus != null && getBonus.size() > 0) {
                        tvEmpty.setVisibility(View.GONE);
                        bonus = bean.getBonus();
                        llListView.setVisibility(View.VISIBLE);
                        initAdapter();
                        setTodayTxt(getBonus);
                    } else {
                        setEmpty();
                    }
                    tvAllReward.setText(new DecimalFormat("#0.00").format(bean.getMonthSum()));
                    break;
                case GET_EMPTY:
                    setEmpty();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        initView();
        initData();
    }

    private void initView() {
        lvReward = (ListView) findViewById(R.id.lv_reward);
        tvDay = (TextView) findViewById(R.id.tv_day);
        tvAllReward = (TextView) findViewById(R.id.tv_all_reward);
        tvEmpty = (TextView) findViewById(R.id.tv_empty);
        tvTodayReward = (TextView) findViewById(R.id.tv_today_reward);
        llListView = (LinearLayout) findViewById(R.id.ll_listView);

        registerBtn(tvDay);
    }

    private void initData() {
        day = TimeTool.getCurrentTime("yyyy-MM-dd");
        String currentDate = day.replace("-", ".");
        tvDay.setText(currentDate);
        setTitle(getString(R.string.times_reward));
        getReward();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_day:
                new DateTool(this, new DateTool.DatePickerResult() {
                    @Override
                    public void clickResult(String date) {
                        RewardActivity.this.day = date;
                        getReward();
                    }
                });
                break;
        }
    }

    /**
     * 设置当天的绩效
     */
    private void setTodayTxt(List<Bonus> bonus) {
        double todayBonus = 0;
        for (Bonus bean : bonus) {
            todayBonus += bean.getAmount();
        }
        tvTodayReward.setText(new DecimalFormat("#0.00").format(todayBonus));
    }

    private void initAdapter() {
        if (rewardAdapter == null) {
            rewardAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return bonus.size();
                }

                @Override
                public Bonus getItem(int position) {
                    return bonus.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View v, ViewGroup viewGroup) {
                    Bonus bean = getItem(position);
                    View view = View.inflate(RewardActivity.this, R.layout.item_reward, null);
                    TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
                    TextView tvDepart = (TextView) view.findViewById(R.id.tv_depart);
                    TextView tvArrive = (TextView) view.findViewById(R.id.tv_arrive);
                    TextView tvReward = (TextView) view.findViewById(R.id.tv_reward);
                    TextView type=(TextView)  view.findViewById(R.id.type);
                    View viewLine = view.findViewById(R.id.view_line);
                    tvTime.setText(bean.getCreateDate());
                    tvDepart.setText(bean.getDepart());
                    tvArrive.setText(bean.getArrive());
                    tvReward.setText(new DecimalFormat("#0.00").format(bean.getAmount()));
                    type.setText(bean.getType());
                    if (position == bonus.size() - 1) {
                        viewLine.setVisibility(View.GONE);
                    } else {
                        viewLine.setVisibility(View.VISIBLE);
                    }
                    return view;
                }
            };
            lvReward.setAdapter(rewardAdapter);
        } else {
            rewardAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置为空
     */
    private void setEmpty() {
        tvTodayReward.setText("0.00");
        tvEmpty.setVisibility(View.VISIBLE);
        llListView.setVisibility(View.GONE);
    }

    /**
     * 获取绩效
     */
    public void getReward() {
        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        final MoneyRequest request=new MoneyRequest();
        Header header=new Header();
        request.userName= SharePreData.getInstance().getStrData("id");
        request.date=day;
        body.moneyRequest=request;
        envelope.header=header;
        envelope.body=body;
        WebService.getInstance().getData(envelope, "urn:TYWJAPPIntf-ITYWJAPP#getsijiqianbao", new DataCallBack() {
            @Override
            public void success(ResponseEnvelope result) {
                if(result.body!=null){
                    if(result.body.moneyResponse.model.value!=null){
                        if(result.body.moneyResponse.model.value.split(",").length>2){
                            List<Bonus> bonuses=new BonusMapper().transform(result);
                            if (bonuses != null && bonuses.size() > 0) {
                                tvEmpty.setVisibility(View.GONE);
                                llListView.setVisibility(View.VISIBLE);
                                tvAllReward.setText(bonuses.get(0).getMonthAmount());
                                if(bonuses.get(0).getTodayAmount()!=null){
                                    tvTodayReward.setText(bonuses.get(0).getTodayAmount());
                                }else {
                                    tvTodayReward.setText("0");
                                }
                                bonus=bonuses;
                                initAdapter();
                            }
                        }else {
                            String data=result.body.moneyResponse.model.value;
                            tvAllReward.setText(data.substring(1,data.length()));
                            bonus.clear();
                            tvTodayReward.setText("0");
                            rewardAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }
            @Override
            public void error(String responseMessage) {
                setEmpty();
            }
        });
//        Map<String, Object> param = new HashMap<>();
        String date = day.replace("-", "");
        String currentDate = day.replace("-", ".");
        tvDay.setText(currentDate);
//        param.put("startDay", date);
//        param.put("pageNum", 1);
//        param.put("pageSize", 50);
////        param.put("endDay",20171001);
//        WebService.doRequest(GET, WebInterface.GET_BONE, param, new SimpleCallBack() {
//            @Override
//            public void success(int code, String message, String data) {
//                if (code == 0) {
//                    RewardBean bean = new Gson().fromJson(data, RewardBean.class);
//                    Message msg = Message.obtain();
//                    msg.obj = bean;
//                    msg.what = GET_SUCCESS;
//                    handler.sendMessage(msg);
//                } else {
//                    handler.sendEmptyMessage(GET_EMPTY);
//                }
//            }
//        });
    }
}
