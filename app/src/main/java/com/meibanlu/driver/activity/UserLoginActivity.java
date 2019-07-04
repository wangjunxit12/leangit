package com.meibanlu.driver.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.bean.UserBean;
import com.meibanlu.driver.tool.ActivityControl;
import com.meibanlu.driver.tool.CommonData;
import com.meibanlu.driver.tool.NetManager;
import com.meibanlu.driver.tool.SharePreData;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.UtilTool;
import com.meibanlu.driver.tool.XmPlayer;
import com.meibanlu.driver.webservice.RetrofitGenerator;
import com.meibanlu.driver.webservice.mappers.LoginMapper;
import com.meibanlu.driver.webservice.requeset.CheckUserRequest;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.LoginRequest;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * UserLoginActivity
 * Created by lhq on 2017/9/20.
 */

public class UserLoginActivity extends BaseActivity {
    private EditText etPhone, etPassword;
    @NonNull
    private CompositeDisposable mCompositeDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mCompositeDisposable = new CompositeDisposable();
        initView();
        initData();
    }

    private void initData() {
        ActivityControl.finishExceptLogin();
        isLogin();
    }

    private void initView() {
        etPhone = (EditText) findViewById(R.id.et_phone_number);//电话
        etPassword = (EditText) findViewById(R.id.et_password);//密码
        TextView tvLogin = (TextView) findViewById(R.id.tv_login);//登录
        TextView tvResetPassword = (TextView) findViewById(R.id.tv_reset_password);//重置密码
        registerBtn(tvLogin, tvResetPassword);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_login:
                userLogin(etPhone.getText().toString().trim(),etPassword.getText().toString().trim());
                break;
            case R.id.tv_reset_password:
                T.startActivity(ResetPasswordActivity.class);
                break;
        }

    }

    /**
     * 登录
     */
    private void userLogin(String phone,String password) {
        if (phone.length()<11) {
            T.showShort(getString(R.string.phone_error));
            return;
        }
        SharePreData.getInstance().addStrData("phone",etPhone.getText().toString().trim());
        if(!NetManager.connect()){
            T.showShort(T.getStringById(R.string.network_error));
            return;
        }
        T.showLoading();
        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        LoginRequest request=new LoginRequest();
        Header header=new Header();
        request.setPassword(password);
        request.setPhone(phone);
        request.setUid(UtilTool.getDeviceID().length()==0?UtilTool.getPseudo():UtilTool.getDeviceID());
        body.loginRequest=request;
        envelope.header=header;
        envelope.body=body;
        mCompositeDisposable.add(RetrofitGenerator.getInstance().getApiStore().login(envelope)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseEnvelope, UserBean>() {
                    @Override
                    public UserBean apply(ResponseEnvelope responseEnvelopeResult) throws Exception {
                        return new LoginMapper().transform(responseEnvelopeResult);
                    }
                })
                .subscribe(new Consumer<UserBean>() {
                    @Override
                    public void accept(UserBean userBean) throws Exception {
                        T.hideLoading();
                        if (userBean != null) {
                            if (userBean.getSex().equalsIgnoreCase("ok")&&!userBean.getId().contains("error")) {
                                if(userBean.getId()!=null){
                                    CommonData.isLogin=true;
                                    SharePreData.getInstance().addStrData("id",userBean.getId());
                                    SharePreData.getInstance().addStrData("password",etPassword.getText().toString().trim());
                                    T.toast(getString(R.string.login_success));
                                    T.startActivity(HomePageActivity.class);
                                    finish();
                                }
                            }else {
                                if(userBean.getId().contains("error")){
                                    T.show("设备ID错误，更换手机登录请联系车务",4000);
                                    UtilTool.cleanAll();
                                    XmPlayer.getInstance().playTTS("设备ID错误，更换手机登录请联系车务");
                                }
                            }
                        }else {
                            T.toast("登录失败,账号或密码错误");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        T.hideLoading();
                        if(throwable.getMessage()!=null){
                            Log.e("Login",throwable.getMessage());
                        }
                        T.toast("登录出错，请稍后重试");
                    }
                }));

    }

    public void isLogin() {
        String token = SharePreData.getInstance().getStrData("id");
        String phone = SharePreData.getInstance().getStrData("phone");
        String password=SharePreData.getInstance().getStrData("password");
        if (!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(password)){
            etPhone.setText(phone);
            etPassword.setText(password);
            userLogin(phone,password);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        T.hideLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
