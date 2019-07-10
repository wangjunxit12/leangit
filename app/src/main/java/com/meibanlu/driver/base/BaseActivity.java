package com.meibanlu.driver.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.tool.ActivityControl;
import com.meibanlu.driver.tool.T;

import java.util.List;
import java.util.Map;

public class BaseActivity extends AppCompatActivity implements OnClickListener {
    protected final int IN_OUT = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //标记每一个Activity用于对话框；往list里面添加每一个Activity；并且设置当前activity
        ActivityControl.addActivity(this);
        setStatusBarNull();
        statusBarLightMode(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_app_return) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityControl.setCurrentActivity(this);
    }

    /**
     * 隐藏软键盘
     *
     * @param v
     */
    public void hideSoftInput(View v) {
        //1.得到InputMethodManager对象
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //隐藏软键盘
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭activity后将它从listView里面移除
        ActivityControl.removeActivity(this);
        T.hideLoading();
    }

    /**
     * 跳转加入动画效果
     *
     * @param intent
     * @param animation 动画类型
     */
    protected void startActivity(Intent intent, int animation) {
        startActivity(intent);
        switch (animation) {
            case IN_OUT:
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                break;
            default:
        }
    }

    /**
     * activity的跳转
     *
     * @param intentClass 跳转后的activity
     */
    public static void startActivity(Class intentClass, String... param) {
        Activity activity = ActivityControl.getCurrentActivity();
        Intent intent = new Intent();
        if (param != null && param.length > 0) {
            for (int i = 0; i < param.length; i += 2) {
                intent.putExtra(param[i], param[i + 1]);
            }
        }
        intent.setClass(activity, intentClass);
        activity.startActivity(intent);
    }

    /**
     * 清理list数据
     *
     * @param listData listData
     */
    public void cleanListCache(List... listData) {
        for (List list : listData) {
            if (list != null) {
                list.clear();
            }
        }
    }

    /**
     * 清理map数据
     *
     * @param mapData mapData
     */
    public void cleanMapCache(Map[] mapData) {
        for (Map map : mapData) {
            if (map != null) {
                map.clear();
            }
        }
    }

    /**
     * 注册按钮
     */
    protected void registerBtn(View... views) {

        for (View view : views) {
            T.log(view.toString());
            view.setOnClickListener(this);
        }
    }

    /**
     * 设置title
     *
     * @param title title
     */
    protected void setTitle(String title) {
        ImageView ivReturn = (ImageView) findViewById(R.id.iv_app_return);
        TextView tvTitle = (TextView) findViewById(R.id.tv_project_title);
        tvTitle.setText(title);
        registerBtn(ivReturn);
    }

    protected void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T.showShort(msg);
            }
        });
    }

    protected void hide() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T.hideLoading();
            }
        });
    }

    //设置状态栏为空
    protected void setStatusBarNull() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION //华为手机最下面那个看是不是顶上去
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //window.setNavigationBarColor(Color.parseColor("#80b3f9")); //华为手机最下面颜色
        }
    }

    protected void setWindowColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(color);
        }
    }

    /**
     * 设置状态栏黑色字体图标，
     */
    public static void statusBarLightMode(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
