package com.meibanlu.driver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.sql.MessageBean;
import com.meibanlu.driver.tool.UtilTool;
import com.meibanlu.driver.view.BounceScrollView;

import java.util.Collections;
import java.util.List;

/**
 * PersonalNoticeActivity 我的通知
 * Created by lhq on 2017/9/14.
 */

public class PersonalNoticeActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static boolean isForeground;
    private ListView lvNotice;
    private static TextView tvNoMessage;
    private static BaseAdapter noticeAdapter;
    private static List<MessageBean> listMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_notice);
        initView();
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        if (initDbData() > 0) {
            initAdapter();
        } else {
            BounceScrollView shaw = (BounceScrollView) findViewById(R.id.bounceScrollView);
            shaw.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        isForeground = false;
        super.onDestroy();
        tvNoMessage = null;
        noticeAdapter = null;
        listMsg = null;
    }

    /**
     * 读取数据库的数据
     */
    public static int initDbData() {
        listMsg = UtilTool.getDbManager().query();
        if (listMsg.size() > 0) {
            Collections.reverse(listMsg);
            return listMsg.size();
        }
        return 0;
    }

    private void initView() {
        setTitle(getString(R.string.my_notice));
        lvNotice = (ListView) findViewById(R.id.lv_notice);
        lvNotice.setOnItemClickListener(this);
        tvNoMessage = (TextView) findViewById(R.id.tv_no_message);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }

    private void initAdapter() {
        lvNotice.setVisibility(View.VISIBLE);
        tvNoMessage.setVisibility(View.GONE);
        noticeAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return listMsg.size();
            }

            @Override
            public MessageBean getItem(int i) {
                return listMsg.get(i);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                MessageBean msgBean = getItem(position);
                ViewHolder viewHolder;
                if (view == null) {
                    viewHolder = new ViewHolder();
                    view = View.inflate(PersonalNoticeActivity.this, R.layout.item_notice, null);
                    viewHolder.tvTime = (TextView) view.findViewById(R.id.tv_time);
                    viewHolder.ivIsRead = (ImageView) view.findViewById(R.id.iv_is_read);
                    viewHolder.tvInformation = (TextView) view.findViewById(R.id.tv_information);
                    view.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }
                viewHolder.tvTime.setText(msgBean.getReceiveTime());
                viewHolder.tvInformation.setText(msgBean.getInformation());
                if (msgBean.getIsRead() == 0) {
                    viewHolder.ivIsRead.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.ivIsRead.setVisibility(View.INVISIBLE);
                }
                return view;
            }

            class ViewHolder {
                TextView tvTime;
                ImageView ivIsRead;
                TextView tvInformation;
            }
        };
        lvNotice.setAdapter(noticeAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MessageBean msgBean = (MessageBean) adapterView.getAdapter().getItem(i);
        Intent intent = new Intent();
        intent.putExtra("msgId", msgBean.getMsgId());
        intent.setClass(PersonalNoticeActivity.this, PushMessageActivity.class);
        startActivity(intent);
    }

    /**
     * 刷新消息
     */
    public static void refreshMessage() {
        initDbData();
        if (noticeAdapter != null) {
            noticeAdapter.notifyDataSetChanged();
        }
    }
}
