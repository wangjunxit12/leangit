package com.meibanlu.driver.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;

/**
 * PersonalTaskActivity 我的任务
 * Created by lhq on 2017/9/14.
 */

public class PersonalTaskActivity extends BaseActivity {
    ListView lvTask;
    BaseAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_task);
        initView();
        initData();
    }

    private void initData() {
        initAdapter();
        lvTask.setAdapter(taskAdapter);
    }


    private void initView(){
        setTitle(getString(R.string.my_task));
        lvTask = (ListView) findViewById(R.id.lv_task);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    private void initAdapter() {
        taskAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View v, ViewGroup viewGroup) {
                View view = View.inflate(PersonalTaskActivity.this, R.layout.item_personal_task, null);
                return view;
            }
        };


    }

}
