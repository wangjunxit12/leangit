package com.meibanlu.driver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.util.Constant;
import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.tool.SharePreData;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.XmPlayer;
import com.meibanlu.driver.tool.web.DataCallBack;
import com.meibanlu.driver.tool.web.WebService;
import com.meibanlu.driver.webservice.requeset.CancelCodeRequest;
import com.meibanlu.driver.webservice.requeset.GetCodeStateRequest;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.requeset.UpdateCodeRequest;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

public class ScanActivity extends BaseActivity {
    public static final int REQ_QR_CODE_CANCEL = 11003;
    private TextView detail,message;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ImageView scan=findViewById(R.id.iv_scan);
        ImageView cancel=findViewById(R.id.iv_scan_cancel);
//        ImageView back = (ImageView) findViewById(R.id.iv_app_return);
        detail=findViewById(R.id.detail);
        message=findViewById(R.id.message);
        id=getIntent().getStringExtra("id");
        scan.setOnClickListener(this);
        cancel.setOnClickListener(this);
//        back.setOnClickListener(this);
        setTitle("扫码验票");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){

            case R.id.iv_scan:
                if(SharePreData.getInstance().getStrData("phone").length()<1){
                    T.showShort("登录过期，请重新登录");
                    Intent intent = new Intent(ScanActivity.this, UserLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                }
                // 二维码扫码
                Intent intent = new Intent(ScanActivity.this, CaptureActivity.class);
                startActivityForResult(intent, Constant.REQ_QR_CODE);
                break;
            case R.id.iv_scan_cancel:
                if(SharePreData.getInstance().getStrData("phone").length()<1){
                    T.showShort("登录过期，请重新登录");
                    Intent intent2 = new Intent(ScanActivity.this, UserLoginActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);
                    finish();
                    return;
                }
                // 二维码扫码
                Intent intent2 = new Intent(ScanActivity.this, CaptureActivity.class);
                startActivityForResult(intent2, REQ_QR_CODE_CANCEL);
                break;


        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //监听返回键

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            //将扫描出的信息显示出来
            if(scanResult!=null){
                message.setText("消费码："+scanResult);
                updateCode(scanResult);
                T.showLoading();
            }else {
                T.showShort("扫码失败，请重新扫描");
            }
        }else if(requestCode ==REQ_QR_CODE_CANCEL && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            //将扫描出的信息显示出来
            if(scanResult!=null){
                message.setText("消费码："+scanResult);
                cancel(scanResult);
                T.showLoading();
            }else {
                T.showShort("扫码失败，请重新扫描");
            }
        }

    }


    private void getState(final String code){
        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        final GetCodeStateRequest request=new GetCodeStateRequest();
        request.code=code;
        Header header=new Header();
        body.codeStateRequest=request;
        envelope.header=header;
        envelope.body=body;
        WebService.getInstance().getData(envelope, "urn:TYWJAPPIntf-ITYWJAPP#xiaofeimazhuangtai", new DataCallBack() {
            @Override
            public void success(ResponseEnvelope result) {
                if(result.body!=null){
                    if(result.body.codeStateResponse.model.value!=null){
                         String data=result.body.codeStateResponse.model.value;
                         if(data.equalsIgnoreCase("未使用")){
                             updateCode(code);
                         }else {
                             T.hideLoading();
                             T.showShort(data);
                             detail.setText("无效票，该票"+data);
                             XmPlayer.getInstance().playTTS("无效票，该票"+data);
                         }
                    }
                }

            }
            @Override
            public void error(String responseMessage) {
                T.hideLoading();
                T.showShort("扫码失败，请重新扫描");
                XmPlayer.getInstance().playTTS("验票失败");
            }
        });
    }

    private void updateCode(String code){
        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        final UpdateCodeRequest request=new UpdateCodeRequest();
        request.setCode(code);
        request.setSchId(id==null?" ":id);
        request.setPhone(SharePreData.getInstance().getStrData("phone"));
        Header header=new Header();
        body.codeRequest=request;
        envelope.header=header;
        envelope.body=body;
        WebService.getInstance().getData(envelope, "urn:TYWJAPPIntf-ITYWJAPP#updatexiaofeima", new DataCallBack() {
            @Override
            public void success(ResponseEnvelope result) {
                T.hideLoading();
                if(result.body!=null){
                    if(result.body.codeResponse.model.value!=null){
                        String data=result.body.codeResponse.model.value;
                        if(data.equalsIgnoreCase("ok")){
                            XmPlayer.getInstance().playTTS("验票成功");
                            detail.setText("验票成功");
                        }else if (data.equalsIgnoreCase("已经检票")){
                            XmPlayer.getInstance().playTTS("已经检票");
                            detail.setText("已经检票");
                        } else {
                            XmPlayer.getInstance().playTTS("验票失败");
                            detail.setText(data);
                        }
                    }
                }

            }
            @Override
            public void error(String responseMessage) {
                T.hideLoading();
                XmPlayer.getInstance().playTTS("验票失败");
                detail.setText("验票失败");
            }
        });
    }

    private void cancel(String code){
        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        final CancelCodeRequest request=new CancelCodeRequest();
        request.setCode(code);
        request.setSchId(id==null?" ":id);
        request.setPhone(SharePreData.getInstance().getStrData("phone"));
        Header header=new Header();
        body.cancelCodeRequest=request;
        envelope.header=header;
        envelope.body=body;
        WebService.getInstance().getData(envelope, "urn:TYWJAPPIntf-ITYWJAPP#quxiaoupdatexiaofeima", new DataCallBack() {
            @Override
            public void success(ResponseEnvelope result) {
                T.hideLoading();
                if(result.body!=null){
                        if(result.body.cancelCodeResponse.model.value!=null){
                            String data=result.body.cancelCodeResponse.model.value;
                            if(data.equalsIgnoreCase("ok")){
                                detail.setText("取消验票成功");
                                XmPlayer.getInstance().playTTS("取消验票成功");
                            }else {
                                XmPlayer.getInstance().playTTS("取消验票失败");
                                detail.setText("取消验票失败");
                            }
                        }
                }

            }
            @Override
            public void error(String responseMessage) {
                T.hideLoading();
                XmPlayer.getInstance().playTTS("取消验票失败");
                detail.setText("取消验票失败");
            }
        });
    }
}
