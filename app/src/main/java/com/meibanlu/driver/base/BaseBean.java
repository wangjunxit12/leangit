package com.meibanlu.driver.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * BaseBean 封装的Adapter
 * Created by lhq on 2018-01-02.
 */
public abstract class BaseBean<T> extends BaseAdapter {
    private Context mContext;
    private List<T> mDatas;
    private final int mItemLayoutId;

    public BaseBean(Context mContext, List<T> mDatas, int mItemLayoutId) {
        this.mItemLayoutId = mItemLayoutId;
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BaseViewHolder viewHolder = getViewHolder(position, convertView, parent);
        setData(viewHolder, getItem(position));
        return viewHolder.getConvertView();
    }

    public abstract void setData(BaseViewHolder viewHolder, T item);

    private BaseViewHolder getViewHolder(int position, View convertView,
                                         ViewGroup parent) {
        return BaseViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
    }

    /**
     * 下拉刷新
     *
     * @param mDatas mDatas
     */
    public void refreshData(List<T> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    /**
     * 上拉加载
     *
     * @param mDatas mDatas
     */
    public void loadMoreData(List<T> mDatas) {
        this.mDatas.addAll(mDatas);
        notifyDataSetChanged();
    }

    /**
     * 获取data
     */
    public List<T> getData() {
        return mDatas;
    }

    /**
     * 是否加载完成
     */
    public boolean loadFinish() {
        if (mDatas != null && mDatas.size() % 10 == 0) {
            return true;
        }
        return false;
    }

    /**
     * 加载第几页
     */
    public int loadPage() {
        return mDatas.size();
    }

}