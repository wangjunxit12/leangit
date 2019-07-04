package com.meibanlu.driver.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.bean.AlwaysSpotBean;


/**
 * AlwaysSpotAdapter 常跑的站点
 * Created by lhq on 2017/9/15.
 */

public class AlwaysSpotAdapter extends BaseAdapter {
    private Context context;
    private AlwaysSpotBean alwaysSpotBean;

    public AlwaysSpotAdapter(AlwaysSpotBean alwaysSpotBean, Context context) {
        this.alwaysSpotBean = alwaysSpotBean;
        this.context = context;
    }


    @Override
    public int getCount() {
        return alwaysSpotBean.getData().size();
    }

    @Override
    public AlwaysSpotBean.AlwaysSpot getItem(int position) {
        return alwaysSpotBean.getData().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup viewGroup) {
        final AlwaysSpotBean.AlwaysSpot item = getItem(position);
        View view = View.inflate(context, R.layout.item_manage_spot, null);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        ImageView ivSelect = (ImageView) view.findViewById(R.id.iv_selected);
        tvName.setText(item.getStationName());
        ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setChoose(!item.isChoose());
                notifyDataSetChanged();
            }
        });
        ivSelect.setSelected(item.isChoose());
        if (alwaysSpotBean.isShow()) {
            ivSelect.setVisibility(View.VISIBLE);
        } else {
            ivSelect.setVisibility(View.GONE);
        }
        return view;
    }
}
