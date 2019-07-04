package com.meibanlu.driver.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.tool.SharePreData;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.web.DataCallBack;
import com.meibanlu.driver.tool.web.SimpleCallBack;
import com.meibanlu.driver.tool.web.WebInterface;
import com.meibanlu.driver.tool.web.WebService;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.ModifyPasswordRequest;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import java.util.HashMap;
import java.util.Map;

/**
 * ResetPasswordActivity 重置密码
 * Created by lhq on 2017/9/29.
 */

public class ResetPasswordActivity extends BaseActivity {
    EditText etOldPassword;//旧的密码
    EditText etNewPassword;//新的密码
    EditText etRePassword;//确认的密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
        initData();
    }

    private void initData() {
        setTitle(getString(R.string.change_password));
    }

    private void initView() {
        etOldPassword = (EditText) findViewById(R.id.et_old_password);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etRePassword = (EditText) findViewById(R.id.et_re_password);
        TextView tvSure = (TextView) findViewById(R.id.tv_sure);//确认
        registerBtn(tvSure);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_sure:
                changePassword();
                break;
        }
    }

    /**
     * 修改密码
     */
    private void changePassword() {
        Map<String, Object> param = new HashMap<>();
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String rePassword = etRePassword.getText().toString();
        if (TextUtils.isEmpty(oldPassword)) {
            T.showShort("原密码为空");
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            T.showShort("请输入新的密码");
            return;
        }
        if (TextUtils.isEmpty(rePassword)) {
            T.showShort("请再次输入密码");
            return;
        }
        if (!newPassword.equals(rePassword)) {
            T.showShort("输入的密码不一致");
            return;
        }
        T.showLoading(ResetPasswordActivity.this);
        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        final ModifyPasswordRequest request=new ModifyPasswordRequest();
        Header header=new Header();
        request.setPassword(newPassword);
        request.setPhone(SharePreData.getInstance().getStrData("phone"));
        body.passwordRequest=request;
        envelope.header=header;
        envelope.body=body;
        WebService.getInstance().getData(envelope, "urn:TYWJAPPIntf-ITYWJAPP#ck_jjupdatepsd", new DataCallBack() {
            @Override
            public void success(ResponseEnvelope result) {
                T.hideLoading();
                if(result.body!=null){
                    String data=result.body.passwordResponse.model.value;
                    if(data.equalsIgnoreCase("ok")){
                        T.showShort("密码修改成功");
                    }
                }

            }

            @Override
            public void error(String responseMessage) {
                T.hideLoading();
            }
        });

    }
}
