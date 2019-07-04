package com.meibanlu.driver.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.tool.ImageUtil;
import com.meibanlu.driver.tool.SharePreData;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.UtilTool;
import com.meibanlu.driver.tool.XMDialog;
import com.meibanlu.driver.view.CircularImage;

import java.util.ArrayList;
import java.util.List;

/**
 * PersonalDetailActivity 个人详细信息
 * Created by lhq on 2017/9/14.
 */

public class PersonalDetailActivity extends BaseActivity {
    ListView lvPersonalDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_detail);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        initPersonalList();
    }

    private void initView() {
        setTitle(getString(R.string.personal_detail));
        lvPersonalDetail = (ListView) findViewById(R.id.lv_personal_detail);
        CircularImage ciHead = (CircularImage) findViewById(R.id.ci_user_portrait);
        String photo = SharePreData.getInstance().getStrData("photo");
        //设置头像
        if (!TextUtils.isEmpty(photo)) {
            ImageUtil.loadImageWithAllSize(PersonalDetailActivity.this, photo, ciHead);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

        }

    }

    //初始化userList
    private void initPersonalList() {
        final int[] type = {R.string.name1, R.string.phone_number1, R.string.sex, R.string.age, R.string.change_password, R.string.exit_login};
        final List<String> listData = new ArrayList<>();
        SharePreData sharePreData = SharePreData.getInstance();
        String name = sharePreData.getStrData("name");
        String phone = sharePreData.getStrData("phone");
        String sex = sharePreData.getStrData("sex");
        String age = sharePreData.getStrData("age");
        listData.add(name);
        listData.add(phone);
        listData.add(sex);
        listData.add(age);
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return type.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @SuppressLint("ViewHolder")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(PersonalDetailActivity.this, R.layout.item_personal_message, null);
                TextView tvTitleDetail = (TextView) convertView.findViewById(R.id.tv_detail_title);
                TextView tvContent = (TextView) convertView.findViewById(R.id.tv_content);
                View tvBottom = convertView.findViewById(R.id.view_divide);
                View tvLine = convertView.findViewById(R.id.view_line);
                if (position == 1 || position == 3 || position == 4) {
                    tvBottom.setVisibility(View.VISIBLE);
                    tvLine.setVisibility(View.INVISIBLE);
                } else if (position == 5) {
                    tvBottom.setVisibility(View.GONE);
                    tvLine.setVisibility(View.GONE);
                } else {
                    tvBottom.setVisibility(View.GONE);
                    tvLine.setVisibility(View.VISIBLE);
                }
                if (position < 4) {
                    tvContent.setText(listData.get(position));
                }
                tvTitleDetail.setText(getString(type[position]));
                return convertView;
            }
        };
        lvPersonalDetail.setAdapter(adapter);
        lvPersonalDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 4) {
                    T.startActivity(ResetPasswordActivity.class);
                } else if (position == 5) {
                    //退出
                    XMDialog.showDialog(PersonalDetailActivity.this, "提示", "确认退出当前账号？", new XMDialog.DialogResult() {
                        @Override
                        public void clickResult(int resultCode) {
                            if (resultCode == XMDialog.CLICK_SURE) {
                                UtilTool.cleanAll();
                                T.startActivity(UserLoginActivity.class);
                                UtilTool.clearExit();//清除班次信息
                                UtilTool.getDbManager().deleteAll();
                            }
                        }
                    });
                }
            }
        });

    }


}
