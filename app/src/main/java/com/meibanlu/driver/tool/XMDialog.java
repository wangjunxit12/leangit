package com.meibanlu.driver.tool;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.meibanlu.driver.R;
import com.meibanlu.driver.view.LetterSpacingTextView;

/**
 * Created by lhq on 2016/12/6.
 * 完美全局对话框
 */

public class XMDialog {
    public static final int CLICK_SURE = 0x1; //点击确定
    public static final int CLICK_CANCEL = 0X2;//点击取消
    public static final int HAVE_TITLE = 0X3;//有标题
    public static final int NO_TITLE = 0X4;//没有标题

    /**
     * @param title        标题
     * @param message      内容
     * @param dialogResult 回调结果
     * @param buttonLeft   左边按钮显示文字
     * @param buttonRight  右边按钮显示文字
     */
    public static void showDialog(String title, String message, String buttonLeft, String buttonRight, int type, final DialogResult dialogResult) {
        Activity curActivity = ActivityControl.getCurrentActivity();
        if (curActivity != null) {
            View view;
            if (type == HAVE_TITLE) {
                view = LayoutInflater.from(curActivity).inflate(R.layout.activity_dialog, null);
                TextView title_dialog = (TextView) view.findViewById(R.id.dialog_title);
                title_dialog.setText(title);
            } else {
                view = LayoutInflater.from(curActivity).inflate(R.layout.activity_no_title_dialog, null);
            }
            LetterSpacingTextView content_dialog = (LetterSpacingTextView) view.findViewById(R.id.dialog_content);
            Button sure_dialog = (Button) view.findViewById(R.id.dialog_sure);
            Button cancel_dialog = (Button) view.findViewById(R.id.dialog_cancel);
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(curActivity);
            content_dialog.setLetterSpacing(6);
            content_dialog.setText(message);
            cancel_dialog.setText(buttonLeft);
            sure_dialog.setText(buttonRight);
            dialogBuilder.setContentView(view);
            dialogBuilder
                    // 重点设置
                    .withEffect(Effectstype.Slideleft)        //设置对话框弹出样式
                    .withDuration(700)              //动画显现的时间（时间长就类似放慢动作）
                    .isCancelable(false)
                    .show();
            sure_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogResult.clickResult(CLICK_SURE);
                    dialogBuilder.cancel();
                }
            });
            cancel_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogResult.clickResult(CLICK_CANCEL);
                    dialogBuilder.cancel();
                }
            });
        }
    }

    /**
     * @param message      内容
     * @param dialogResult 回调结果
     */
    public static void showDialog(Context context, String message, final DialogResult dialogResult) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_no_title_dialog, null);
        LetterSpacingTextView content_dialog = (LetterSpacingTextView) view.findViewById(R.id.dialog_content);
        Button sure_dialog = (Button) view.findViewById(R.id.dialog_sure);
        Button cancel_dialog = (Button) view.findViewById(R.id.dialog_cancel);
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        content_dialog.setLetterSpacing(6);
        content_dialog.setText(message);
        dialogBuilder.setContentView(view);
        dialogBuilder
                // 重点设置
                .withEffect(Effectstype.Slideleft)        //设置对话框弹出样式
                .withDuration(700)              //动画显现的时间（时间长就类似放慢动作）
                .isCancelable(false)
                .show();
        sure_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.clickResult(CLICK_SURE);
                dialogBuilder.cancel();
            }
        });
        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.clickResult(CLICK_CANCEL);
                dialogBuilder.cancel();
            }
        });
    }

    /**
     * 景区详细文字介绍的对话框
     */
    public static void showDialog(String title, String message, int type, final DialogResult dialogResult) {
        showDialog(title, message, T.getStringById(R.string.cancel), T.getStringById(R.string.sure), type, dialogResult);
    }

    /**
     * 景区详细文字介绍的对话框
     */
    public static Button showDialog(Context context, String title, String message, final DialogResult dialogResult) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_dialog, null);
        TextView title_dialog = (TextView) view.findViewById(R.id.dialog_title);
        LetterSpacingTextView content_dialog = (LetterSpacingTextView) view.findViewById(R.id.dialog_content);
        Button sure_dialog = (Button) view.findViewById(R.id.dialog_sure);
        Button cancel_dialog = (Button) view.findViewById(R.id.dialog_cancel);
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        title_dialog.setText(title);
        content_dialog.setLetterSpacing(6);
        content_dialog.setText(message);
        dialogBuilder.setContentView(view);
        dialogBuilder
                // 重点设置
                .withEffect(Effectstype.Slideleft)        //设置对话框弹出样式
                .withDuration(700)              //动画显现的时间（时间长就类似放慢动作）
                .isCancelable(false)
                .show();
        sure_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.clickResult(CLICK_SURE);
                dialogBuilder.cancel();
            }
        });
        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.clickResult(CLICK_CANCEL);
                dialogBuilder.cancel();
            }
        });
        return sure_dialog;
    }

    public interface DialogResult {
        void clickResult(int resultCode);
    }

    /**
     * 景区详细文字介绍的对话框
     */
    public static void stateDialog(Context context, String state) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_state, null);
        TextView sureDialog = (TextView) view.findViewById(R.id.tv_sure);
        LetterSpacingTextView dialogContent = (LetterSpacingTextView) view.findViewById(R.id.dialog_content);
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        dialogContent.setText(state);
        dialogContent.setLetterSpacing(6);
        dialogBuilder.setContentView(view);
        dialogBuilder
                // 重点设置
                .withEffect(Effectstype.Slideleft)        //设置对话框弹出样式
                .withDuration(700)              //动画显现的时间（时间长就类似放慢动作）
                .isCancelable(false)
                .show();
        sureDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.cancel();
            }
        });
    }
}
