package com.meibanlu.driver.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.bean.Passenger;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.web.DataCallBack;
import com.meibanlu.driver.tool.web.WebService;
import com.meibanlu.driver.webservice.mappers.PhoneMapper;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.PassengersRequest;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import java.util.ArrayList;
import java.util.List;


public class PhoneActivity extends BaseActivity {
    private ListView listView;
    private List<Passenger> phones = new ArrayList<>();
    private MyAdapter adapter;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        setTitle("乘客电话单");
        listView = findViewById(R.id.listView);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("id");
        }
        initData();
    }

    private void initData() {
        RequestEnvelope envelope = new RequestEnvelope();
        RequestBody body = new RequestBody();
        final PassengersRequest request = new PassengersRequest();
        Header header = new Header();
        request.setId(id);
        body.passengersRequest = request;
        envelope.header = header;
        envelope.body = body;
        WebService.getInstance().getData(envelope, "urn:TYWJAPPIntf-ITYWJAPP#getchengke", new DataCallBack() {
            @Override
            public void success(ResponseEnvelope result) {
                if (result.body != null) {
                    if (result.body.passengersResponse.model.value != null) {
                        phones = new PhoneMapper().transform(result);
                        if (phones != null && phones.size() > 0) {
                            T.log("phones.size()" + phones.size());
                            adapter = new MyAdapter();
                            listView.setAdapter(adapter);
                        } else {
                            T.showShort("没有数据");
                        }
                    }
                }

            }

            @Override
            public void error(String responseMessage) {
                Log.e("Reward", responseMessage);
                T.showShort("没有数据");
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return phones.size();
        }

        @Override
        public Object getItem(int position) {
            return phones.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final Passenger phone = phones.get(position);
            if (convertView == null) {
                convertView = View.inflate(PhoneActivity.this, R.layout.item_phone, null);
                holder = new ViewHolder();
                holder.phone = convertView.findViewById(R.id.item_text);
                holder.state = convertView.findViewById(R.id.item_state);
                holder.phone.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(PhoneActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(PhoneActivity.this, new String[]{Manifest.permission.CALL_PHONE}, GET_PERMISSIONS);
                        }else {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            Uri data = Uri.parse("tel:" + phone);
                            intent.setData(data);
                            startActivity(intent);
                        }

                    }
                });
                convertView.setTag(holder);
            }else {
                holder= (ViewHolder) convertView.getTag();
            }
            holder.state.setText(phone.getState()==null?"":phone.getState());
            holder.phone.setText(phone.getPhone()==null?"":phone.getPhone());
            return convertView;
        }


    }
    static class ViewHolder{
       public TextView phone;
        public TextView state;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==GET_PERMISSIONS){

        }

    }

    /**
     * 获取权限
     */
    private static final int GET_PERMISSIONS = 0X2;
}
