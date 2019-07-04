package com.meibanlu.driver.base;

import android.view.View;

/**
 * 其余的有view，layout的布局
 * Created by lhq on 2017/10/26.
 */

public class BaseViewActivity  implements View.OnClickListener {
    protected View v;
    public <T extends View> T findView(int viewId) {
        View view = v.findViewById(viewId);
        return (T) view;
    }
    /**
     * 注册按钮
     */
    protected void registerBtn(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View view) {

    }
}
