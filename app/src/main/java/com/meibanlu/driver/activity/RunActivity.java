package com.meibanlu.driver.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;

/**
 * 运行状态，屏幕长亮
 *
 * @author lhq
 * @date 2017/11/11
 */

public class RunActivity extends BaseActivity {
    private long clickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_runing);
        setBrightness(this, 1);
        initView();
    }

    public static void setBrightness(Activity activity, int brightness) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }

    private void initView() {
        setTitle(getString(R.string.run));
        RelativeLayout rlAll = (RelativeLayout) findViewById(R.id.rl_all);
        registerBtn(rlAll);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_all:
                long currentTime = System.currentTimeMillis();
                if (currentTime - clickTime < 200) {
                    openScreen();
                }
                clickTime = System.currentTimeMillis();
                break;
            default:
        }
    }

    /**
     * 点亮屏幕
     */
    private void openScreen() {
        setBrightness(this, 255);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(10000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setBrightness(RunActivity.this, 1);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
