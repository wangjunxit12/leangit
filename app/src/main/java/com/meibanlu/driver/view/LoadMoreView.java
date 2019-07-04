package com.meibanlu.driver.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.tool.T;

/**
 * LoadMoreView
 * Created by lhq on 2018-01-08.
 */

public class LoadMoreView extends ListView implements AbsListView.OnScrollListener {
    public int last_index;
    public int total_index;
    public int pageSize = 10;
    private boolean isLoading = false;//表示是否正处于加载状态
    public View loadMoreView;
    private LoadMoreListener loadMore;
    private TextView tvLoad;

    public void setLoadListener(LoadMoreListener loadMore) {
        this.loadMore = loadMore;
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadMoreView = View.inflate(context, R.layout.load_more, null);//获得刷新视图
        tvLoad = (TextView) loadMoreView.findViewById(R.id.tv_load);
        loadMoreView.setVisibility(View.GONE);
        addFooterView(loadMoreView, null, false);
        setOnScrollListener(this);
    }


    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
//        if (last_index == total_index - 2) {
//            tvLoad.setText(T.getStringById(R.string.loading));
//            loadMoreView.setVisibility(View.GONE);
//        }
        if (last_index == total_index &&
                (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)) {
            loadMoreView.setVisibility(View.GONE);
            //表示此时需要显示刷新视图界面进行新数据的加载(要等滑动停止)
            if ((total_index - 1) % pageSize != 0) {
                tvLoad.setText(T.getStringById(R.string.load_finish));
            } else if (!isLoading) {
                //不处于加载状态的话对其进行加载
                isLoading = true;
                //设置刷新界面可见
                loadMore.load(total_index / pageSize + 1);
                tvLoad.setText(T.getStringById(R.string.loading));
            }
            loadMoreView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        last_index = firstVisibleItem + visibleItemCount;
        total_index = totalItemCount;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public interface LoadMoreListener {
        void load(int pageNumber);
    }

    public void setEndTxt(int size) {
        if (size % 10 == 0) {
            tvLoad.setText(T.getStringById(R.string.loading));
        } else {
            tvLoad.setText(T.getStringById(R.string.load_finish));
        }
    }
}
