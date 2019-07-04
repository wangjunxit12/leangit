package com.meibanlu.driver.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.bean.Bonus;
import com.meibanlu.driver.bean.Mileage;
import com.meibanlu.driver.bean.MileageBean;
import com.meibanlu.driver.tool.EditTool;
import com.meibanlu.driver.tool.SharePreData;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.TimeTool;
import com.meibanlu.driver.tool.TxtChangeListener;
import com.meibanlu.driver.tool.XMDialog;
import com.meibanlu.driver.tool.web.DataCallBack;
import com.meibanlu.driver.tool.web.SimpleCallBack;
import com.meibanlu.driver.tool.web.WebInterface;
import com.meibanlu.driver.tool.web.WebService;
import com.meibanlu.driver.webservice.mappers.BonusMapper;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.MoneyRequest;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.requeset.UpLoadOilRequest;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecordOilActivity油耗录入
 * Created by lhq on 2017-12-29.
 */

public class RecordOilActivity extends BaseActivity implements TxtChangeListener {
    private EditText etCar, etOil;
    private TextView tvSure;
    private TextView tvCarNumber, tvMileage, tvDate, tvShowRecord;
    private EditText tvOilNumber, tvoilL, tvFee, tvPerson,reMark;
    private LinearLayout llLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oil);
        initView();
        initData();
    }


    private void initView() {
        setTitle(getString(R.string.add_oil));
        etCar = (EditText) findViewById(R.id.et_car);
        etOil = (EditText) findViewById(R.id.et_oil);
        tvSure = (TextView) findViewById(R.id.tv_sure);
        tvCarNumber = (TextView) findViewById(R.id.tv_car_number);
        tvMileage = (TextView) findViewById(R.id.tv_mileage);
        tvDate = (TextView) findViewById(R.id.tv_date);
        llLast = (LinearLayout) findViewById(R.id.ll_last);
        tvShowRecord = (TextView) findViewById(R.id.tv_show_record);

        tvOilNumber = (EditText) findViewById(R.id.oil_record);
        tvPerson = (EditText) findViewById(R.id.person);
        tvoilL = (EditText) findViewById(R.id.oil_number);
        tvFee = (EditText) findViewById(R.id.oil_fee);
        reMark = (EditText) findViewById(R.id.remark);

        EditTool.setTxtChange(this, etCar, etOil);
        tvSure.setEnabled(false);
        registerBtn(tvSure, tvShowRecord);
    }

    private void initData() {
        String carNumber = SharePreData.getInstance().getStrData("carNumber");
        if (!TextUtils.isEmpty(carNumber)) {
            etCar.setText(carNumber);
//            getMileage(carNumber);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_sure:
                showSureDialog();
                break;
            case R.id.tv_show_record:
                startActivity(MileageRecordActivity.class);
                break;
        }
    }

    private void showSureDialog() {
        String carNumber = etCar.getText().toString().replace(" ", "");
        String mileage = etOil.getText().toString().replace(" ", "");
        String message = "车牌号:" + carNumber + "\n" + "里程:" + mileage + "KM";
        Button buttonSure = XMDialog.showDialog(RecordOilActivity.this, "录入信息",
                message, new XMDialog.DialogResult() {
                    @Override
                    public void clickResult(int resultCode) {
                        if (resultCode == XMDialog.CLICK_SURE) {
                            addOilRecord();
                        }
                    }
                });
        buttonSure.setTextColor(T.getColorById(R.color.fdd000));
    }


    private void addOilRecord() {
        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        final UpLoadOilRequest request=new UpLoadOilRequest();
        Header header=new Header();
        request.setId(SharePreData.getInstance().getStrData("id"));
        request.setCarNumber(SharePreData.getInstance().getStrData("carNumber"));
        request.setOil_time(TimeTool.getCurrentTime("yyyy-MM-dd"));
        request.setAdd_time(TimeTool.getCurrentTime("yyyy-MM-dd"));
        request.setOil_l(tvoilL.getText().toString().length()<=0?" ":tvoilL.getText().toString().trim());
        request.setKm(etOil.getText().toString().length()<=0?" ":etOil.getText().toString().trim());
        request.setOil_fee(tvFee.getText().toString().length()<=0?" ":tvFee.getText().toString().trim());
        request.setHandle(tvPerson.getText().toString().length()<=0?" ":tvPerson.getText().toString().trim());
        request.setRemark(reMark.getText().toString().length()<=0?" ":reMark.getText().toString().trim());
        body.oilRequest=request;
        envelope.header=header;
        envelope.body=body;
        WebService.getInstance().getData(envelope, "urn:TYWJAPPIntf-ITYWJAPP#insert_oil_record", new DataCallBack() {
            @Override
            public void success(ResponseEnvelope result) {
                if(result.body!=null){
                    if(result.body.oilResponse.model.value!=null){
                        if (result.body.oilResponse.model.value.equalsIgnoreCase("ok")) {
                            toast("上传成功");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            });
                        }
                    }
                }

            }
            @Override
            public void error(String responseMessage) {
                toast("失败");
            }
        });


    }

    @Override
    public void onTxtChange(boolean isEmpty) {
        tvSure.setEnabled(!isEmpty);
    }


}
