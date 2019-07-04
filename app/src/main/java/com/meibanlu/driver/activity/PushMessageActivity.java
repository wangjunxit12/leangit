package com.meibanlu.driver.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.sql.MessageBean;
import com.meibanlu.driver.tool.UtilTool;

import java.util.List;

/**
 * 推送消息点击显示的Activity
 * Created by lhq on 2017/10/11.
 */

public class PushMessageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_message);
        initData();
    }

    private void initData() {
        setTitle(getString(R.string.message_detail));
        String msgId = getIntent().getStringExtra("msgId");
        if (msgId != null) {
            List<MessageBean> listMessage = UtilTool.getDbManager().query("msgId", msgId);
            if (listMessage != null && listMessage.size() > 0) {
                MessageBean msgBean = listMessage.get(0);
                TextView tvTime = (TextView) findViewById(R.id.tv_time);//时间
                TextView tvMessage = (TextView) findViewById(R.id.tv_message);//信息
                tvTime.setText(msgBean.getReceiveTime());
                tvMessage.setText(msgBean.getInformation());
                msgBean.setIsRead(1);//已读
                UtilTool.getDbManager().update(msgBean.getId(), msgBean);
                UtilTool.setMessageNumber();//刷新未阅读消息个数
            }
        }

    }
}
