package com.meibanlu.driver.tool.datepicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.meibanlu.driver.R;
import com.meibanlu.driver.base.BaseViewActivity;
import com.meibanlu.driver.tool.T;

/**
 * 日历工具类
 * Created by lhq on 2017/10/26.
 */
@SuppressLint("InflateParams")
public class DateTool extends BaseViewActivity implements MonthDateView.DateTouch {
    private MonthDateView monthDateView;//月日
    private Activity activity;
    private DatePickerResult pickerResult;

    public DateTool(Activity activity, DatePickerResult pickerResult) {
        this.activity = activity;
        this.pickerResult = pickerResult;
        showDateDialog();
    }

    private void showDateDialog() {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
        v = LayoutInflater.from(activity).inflate(R.layout.activity_date, null);
        ImageView iv_left = findView(R.id.iv_left);
        ImageView iv_right = findView(R.id.iv_right);
        ImageView ivClose = findView(R.id.iv_close);
        monthDateView = findView(R.id.monthDateView);
        TextView tv_date = findView(R.id.date_text);
        TextView tv_week = findView(R.id.week_text);
        monthDateView.setTextView(tv_date, tv_week);
        monthDateView.setDateClick(new MonthDateView.DateClick() {
            @Override
            public void onClickOnDate() {
                T.log("点击了：" + monthDateView.getmSelDate());     //D点击天
                pickerResult.clickResult(monthDateView.getDate());
                dialogBuilder.cancel();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.cancel();
            }
        });
        monthDateView.setDateTouch(this);
        registerBtn(iv_left, iv_right);
        dialogBuilder.setContentView(v);
        dialogBuilder
                // 重点设置
                .withEffect(Effectstype.Slideleft)        //设置对话框弹出样式
                .withDuration(700)              //动画显现的时间（时间长就类似放慢动作）
                .isCancelable(true)
                .show();

    }

    @Override
    public void onTouchOnDate(String touch) {
        if (touch.equals("L")) {
            monthDateView.onRightClick();
        }
        if (touch.equals("RunActivity")) {
            monthDateView.onLeftClick();
        }
    }

    public interface DatePickerResult {
        void clickResult(String date);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int clickId = v.getId();
        switch (clickId) {
            case R.id.iv_left:
                monthDateView.onLeftClick();
                break;
            case R.id.iv_right:
                monthDateView.onRightClick();
                break;
        }
    }
}
