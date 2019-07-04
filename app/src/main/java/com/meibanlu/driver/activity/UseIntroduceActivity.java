package com.meibanlu.driver.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.tool.CommonData;
import com.meibanlu.driver.tool.ImageUtil;

/**
 * UseIntroduceActivity
 * Created by lhq on 2017-12-08.
 */

public class UseIntroduceActivity extends BaseActivity {
    String url = "https://www.meibanlu.com/upload/androidInstructions.png";
    int photoHeight;
    ImageView ivIntroduce;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ImageUtil.loadImage(UseIntroduceActivity.this, url, ivIntroduce, CommonData.windowWidth, photoHeight);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_introduce);
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.use_introduce));
        ivIntroduce = (ImageView) findViewById(R.id.iv_introduce);
        ImageUtil.getPhotoSize(UseIntroduceActivity.this, url, ivIntroduce, new ImageUtil.GetPhotoSize() {
            @Override
            public void getSize(double width, double height) {

            }

            @Override
            public void getProportion(double proportion) {
                photoHeight = (int) (CommonData.windowWidth * proportion);
                handler.sendEmptyMessage(1);
            }
        });
    }
}
