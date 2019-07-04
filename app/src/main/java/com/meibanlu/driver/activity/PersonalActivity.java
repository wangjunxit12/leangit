package com.meibanlu.driver.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.tool.CommonData;
import com.meibanlu.driver.tool.ImageUtil;
import com.meibanlu.driver.tool.SharePreData;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.view.CircularImage;

/**
 * 个人中心
 * Created by lhq on 2017/9/25.
 */

public class PersonalActivity extends BaseActivity {
    private ListView lvPersonal;
    public static boolean isForeground;
    private static BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initView();
        initData();
    }

    private void initView() {
        ImageView ivReturn = (ImageView) findViewById(R.id.iv_return);
        CircularImage circularImage = (CircularImage) findViewById(R.id.ci_user_head);
        String photo = SharePreData.getInstance().getStrData("photo");
        //设置头像
        if (!TextUtils.isEmpty(photo)) {
            ImageUtil.loadImageWithAllSize(PersonalActivity.this, photo, circularImage);
        }
        lvPersonal = (ListView) findViewById(R.id.lv_personal);
        registerBtn(circularImage, ivReturn);
    }

    private void initData() {
        TextView tvName = (TextView) findViewById(R.id.tv_name);//姓名
        TextView tvPhone = (TextView) findViewById(R.id.tv_phone);//电话
        SharePreData shareData = SharePreData.getInstance();
        tvName.setText(shareData.getStrData("name"));
        tvPhone.setText(shareData.getStrData("phone"));
        initUserList();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ci_user_head:
                T.startActivity(PersonalDetailActivity.class);
                break;
            case R.id.iv_return:
                finish();
                break;
        }
    }

    //初始化userList
    private void initUserList() {
        final int[] images = {R.mipmap.ic_my_message, R.mipmap.ic_driver_record, R.mipmap.ic_route_false, R.mipmap.ic_reward, R.mipmap.ic_oil, R.mipmap.ic_user_introduce};
        final int[] type = {R.string.my_notice, R.string.driving_record, R.string.route_departure, R.string.times_reward, R.string.add_oil, R.string.use_introduce};
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return images.length;
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
            public View getView(int position, View view, ViewGroup parent) {
                ViewHolder viewHolder;
                if (view == null) {
                    view = View.inflate(PersonalActivity.this, R.layout.item_user, null);
                    viewHolder = new ViewHolder();
                    viewHolder.ivType = (ImageView) view.findViewById(R.id.iv_type);
                    viewHolder.tvType = (TextView) view.findViewById(R.id.tv_type);
                    viewHolder.tvLine = view.findViewById(R.id.view_line);
                    viewHolder.tvNotReadNumber = (TextView) view.findViewById(R.id.tv_not_read_number);//未读条数
                    view.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }
                if (position == 0) {
                    showMessage(viewHolder.tvNotReadNumber);
                }
                viewHolder.ivType.setImageResource(images[position]);
                viewHolder.tvType.setText(getString(type[position]));
                if (position == images.length - 1) {
                    viewHolder.tvLine.setVisibility(View.GONE);
                } else {
                    viewHolder.tvLine.setVisibility(View.VISIBLE);
                }
                return view;
            }

            class ViewHolder {
                ImageView ivType;
                TextView tvType;
                TextView tvNotReadNumber;
                View tvLine;
            }
        };
        lvPersonal.setAdapter(adapter);
        lvPersonal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://我的消息
                        T.startActivity(PersonalNoticeActivity.class);
                        break;
                    case 1://行车记录
                        inRecord(false);
                        break;
                    case 2://路线偏离
                        inRecord(true);
                        break;
                    case 3://
                        T.startActivity(RewardActivity.class);
                        break;
                    case 4://录入油耗
                        T.startActivity(RecordOilActivity.class);
                        break;
                    case 5://使用说明
                        T.startActivity(UseIntroduceActivity.class);
                        break;
                }
            }
        });

    }

    private void inRecord(boolean depart) {
        Intent intent = new Intent();
        intent.putExtra("routeDepart", depart);
        intent.setClass(PersonalActivity.this, RouteDepartureActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        refreshAdapter();//刷新adapter
    }

    /**
     * 清空内存
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter = null;
    }

    /**
     * 显示未读提示
     */
    private void showMessage(TextView tvNotReadNumber) {
        if (tvNotReadNumber != null) {
            if (CommonData.notReadNumber == 0) {
                tvNotReadNumber.setVisibility(View.GONE);
            } else {
                T.log(CommonData.notReadNumber + "");
                tvNotReadNumber.setVisibility(View.VISIBLE);
                tvNotReadNumber.setText(CommonData.notReadNumber + "");
            }
        }
    }

    public static void refreshAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

}
