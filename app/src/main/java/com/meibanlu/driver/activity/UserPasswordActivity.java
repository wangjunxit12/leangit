package com.meibanlu.driver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.tool.SharePreData;
import com.meibanlu.driver.tool.T;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改用户密码
 * Created by lhq on 2016/12/13.
 */

public class UserPasswordActivity extends BaseActivity {

    private EditText etOld, etNew, etRepeat;

    /* handler */
    private final byte RESET_SUCCESS = 0x2;
    private Handler resetPwdHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RESET_SUCCESS:
//                    setResult(PersonalMessageActivity.RESET_PWD, new Intent());
                    finish();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.change_password));
        etOld = (EditText) findViewById(R.id.et_old_password);
        etNew = (EditText) findViewById(R.id.et_new_password);
        etRepeat = (EditText) findViewById(R.id.et_re_password);
        TextView tvSure = (TextView) findViewById(R.id.tv_sure);
        TextView tvForget = (TextView) findViewById(R.id.tv_forget_password);
        registerBtn(tvSure, tvForget);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_sure:
                changePassword();
                break;
            case R.id.tv_forget_password:
                T.startActivity(FindPasswordActivity.class);
//                intent.setClass(this, UserRegisteredActivity.class);
//                intent.putExtra("forget", true);
//                startActivityForResult(intent, UserLoginActivity.REGISTER_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
//            case UserLoginActivity.REGISTER_CODE:
            //重置成功
//                resetPwdHandler.sendEmptyMessage(RESET_SUCCESS);
//                break;
        }
    }

    /**
     * 修改密码
     */
    private void changePassword() {
        String oldPwd = etOld.getText().toString();
        if (oldPwd.length() < 4) {
            T.showShort(getString(R.string.old_password_short));
            return;
        }
        String newPwd = etNew.getText().toString();
        String rePwd = etRepeat.getText().toString();
        if (newPwd.length() < 6) {
            T.showShort(getString(R.string.password_short));
            return;
        }
        if (!newPwd.equals(rePwd)) {
            T.showShort(getString(R.string.password_not_match));
            return;
        }
        T.showLoading(UserPasswordActivity.this);
        Map<String, Object> param = new HashMap<>();
        param.put("userId", SharePreData.getInstance().getStrData("userId"));
        param.put("oldPassword", oldPwd);
        param.put("newPassword", newPwd);
//        WebService.doRequest(WebService.POST, WebInterface.CHANGE_PWD, param, new CallBack() {
//            @Override
//            public void success(int code, String message, String data) {
//                if (code == 200) {
//                    resetPwdHandler.sendEmptyMessage(RESET_SUCCESS);
//                } else {
//                    Message msg = Message.obtain();
//                    msg.obj = message;
//                    msg.what = SHORT_TOAST;
//                    baseHandler.sendMessage(msg);
//                }
//            }
//
//            @Override
//            public void success(String result) {
//                hideLoading();
//            }
//
//            @Override
//            public void error(String responseMessage) {
//
//            }
//        });
    }
}
