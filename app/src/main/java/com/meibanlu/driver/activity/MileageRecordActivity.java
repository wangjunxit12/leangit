package com.meibanlu.driver.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.base.BaseBean;
import com.meibanlu.driver.base.BaseViewHolder;
import com.meibanlu.driver.bean.Bonus;
import com.meibanlu.driver.bean.Mileage;
import com.meibanlu.driver.bean.MileageBean;
import com.meibanlu.driver.tool.SharePreData;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.TimeTool;
import com.meibanlu.driver.tool.UntilPopWindow;
import com.meibanlu.driver.tool.web.DataCallBack;
import com.meibanlu.driver.tool.web.SimpleCallBack;
import com.meibanlu.driver.tool.web.WebInterface;
import com.meibanlu.driver.tool.web.WebService;
import com.meibanlu.driver.view.LoadMoreView;
import com.meibanlu.driver.webservice.mappers.BonusMapper;
import com.meibanlu.driver.webservice.mappers.OilRecordMapper;
import com.meibanlu.driver.webservice.requeset.GetOilRecordRequest;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.MoneyRequest;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MileageRecordActivity
 * Created by lhq on 2018-01-08.
 */

public class MileageRecordActivity extends BaseActivity implements LoadMoreView.LoadMoreListener {
    private LoadMoreView lvAllMileage;
    private BaseBean<Mileage> beanAdapter;
    private RelativeLayout rlAllCar;
    private List<String> allCar;
    private int clickPosition = 0;
    private TextView tvChooseCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mileage_record);
        initView();
        initData();

    }

    private void initView() {
        setTitle(getString(R.string.record_mileage));
        lvAllMileage = (LoadMoreView) findViewById(R.id.lv_all_mileage);
        rlAllCar = (RelativeLayout) findViewById(R.id.rl_all_car);
        tvChooseCar = (TextView) findViewById(R.id.tv_choose_car);
        lvAllMileage.setLoadListener(this);
        registerBtn(rlAllCar);
    }

    private void initData() {
        getMileage(1, null);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_all_car:
                carNumberPop();
                break;
        }
    }

    /**
     * 获取历史油耗
     */
    private void getMileage(final int pageNumber, String carNumber) {
//        Map<String, Object> param = new HashMap<>();
//        param.put("pageSize", 10);
//        param.put("pageNum", pageNumber);
//        if (!TextUtils.isEmpty(carNumber)) {
//            param.put("carNumber", carNumber);
//        }
//        WebService.doRequest(WebService.GET, WebInterface.UPLOAD_OIL, param, new SimpleCallBack() {
//            @Override
//            public void success(int code, String message, final String data) {
//                if (code == 0) {
//                    final MileageBean bean = new Gson().fromJson(data, MileageBean.class);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (pageNumber == 1 && beanAdapter != null) {
//                                beanAdapter.getData().clear();
//                            }
//                            List<Mileage> mileages = bean.getMileageLogs();
//                            allCar = bean.getCarNumbers();
//                            if (mileages != null && mileages.size() > 0) {
//                                allCar.add(0, getString(R.string.all_car));
//                                lvAllMileage.setEndTxt(mileages.size());
//                                if (beanAdapter == null) {
//                                    beanAdapter = new BaseBean<Mileage>(MileageRecordActivity.this,
//                                            mileages, R.layout.item_mileage_record) {
//                                        @Override
//                                        public void setData(BaseViewHolder viewHolder, Mileage item) {
//                                            String date = item.getLogTime();
//                                            if (!TextUtils.isEmpty(date)) {
//                                                viewHolder.setText(R.id.tv_date, TimeTool.stampToDate(date, "yyyy/MM/dd HH:mm"));
//                                            }
//                                            viewHolder.setText(R.id.tv_car_number, item.getCarNumber());
//                                            String txtMileage = item.getMileage() + "km";
//                                            viewHolder.setText(R.id.tv_mileage, txtMileage);
//                                        }
//                                    };
//                                    lvAllMileage.setAdapter(beanAdapter);
//                                } else {
//                                    beanAdapter.loadMoreData(mileages);
//                                }
//                            }
//                        }
//                    });
//                    lvAllMileage.setLoading(false);
//                }
//            }
//        });

        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        final GetOilRecordRequest request=new GetOilRecordRequest();
        Header header=new Header();
        request.setId(SharePreData.getInstance().getStrData("id"));
        body.getOilRecordRequest=request;
        envelope.header=header;
        envelope.body=body;
        WebService.getInstance().getData(envelope, "urn:TYWJAPPIntf-ITYWJAPP#oil_record", new DataCallBack() {
            @Override
            public void success(ResponseEnvelope result) {
                List<Mileage> mileages=new OilRecordMapper().transform(result);
                if (mileages != null && mileages.size() > 0) {
                    allCar.add(0, getString(R.string.all_car));
                    lvAllMileage.setEndTxt(mileages.size());
                    if (beanAdapter == null) {
                        beanAdapter = new BaseBean<Mileage>(MileageRecordActivity.this,
                                mileages, R.layout.item_mileage_record) {
                            @Override
                            public void setData(BaseViewHolder viewHolder, Mileage item) {
                                String date = item.getLogTime();
                                if (!TextUtils.isEmpty(date)) {
                                    viewHolder.setText(R.id.tv_date, TimeTool.stampToDate(date, "yyyy/MM/dd HH:mm"));
                                }
                                viewHolder.setText(R.id.tv_car_number, item.getCarNumber());
                                String txtMileage = item.getMileage() + "km";
                                viewHolder.setText(R.id.tv_mileage, txtMileage);
                            }
                        };
                        lvAllMileage.setAdapter(beanAdapter);
                    } else {
                        beanAdapter.loadMoreData(mileages);
                    }
                }


            }
            @Override
            public void error(String responseMessage) {

            }
        });

        lvAllMileage.setLoading(false);
    }

    @Override
    public void load(int pageNumber) {
        getMileage(pageNumber, null);
    }

    public void carNumberPop() {
        if (allCar != null && allCar.size() > 0) {
            View view = View.inflate(MileageRecordActivity.this, R.layout.activity_choose_car_number, null);
            final PopupWindow pop = UntilPopWindow.getPop(view);
            //在底部显示
            pop.showAtLocation(rlAllCar, Gravity.CENTER, 0, 0);
            ListView lvCarNumber = (ListView) view.findViewById(R.id.lv_car_number);
            BaseAdapter adapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return allCar.size();
                }

                @Override
                public String getItem(int position) {
                    return allCar.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View v, ViewGroup viewGroup) {
                    String name = getItem(position);
                    View view = View.inflate(MileageRecordActivity.this, R.layout.item_choose_car_number, null);
                    ImageView ivCircle = (ImageView) view.findViewById(R.id.iv_circle);
                    TextView tvStation = (TextView) view.findViewById(R.id.tv_car_number);
                    ivCircle.setSelected(clickPosition == position);
                    int color;
                    if (clickPosition == position) {
                        color = R.color.fdd000;
                    } else {
                        color = R.color.text_color_333333;
                    }
                    tvStation.setText(name);
                    tvStation.setTextColor(T.getColorById(color));
                    return view;
                }
            };
            lvCarNumber.setAdapter(adapter);
            lvCarNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String station = (String) adapterView.getAdapter().getItem(position);
                    clickPosition = position;
                    tvChooseCar.setText(station);
                    if (station.equals(getString(R.string.all_car))) {
                        station = null;
                    }
                    getMileage(1, station);
                    pop.dismiss();
                }
            });
        } else {
            T.toast(getString(R.string.no_data));
        }
    }
}
