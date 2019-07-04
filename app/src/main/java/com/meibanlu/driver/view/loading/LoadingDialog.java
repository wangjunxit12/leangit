package com.meibanlu.driver.view.loading;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.meibanlu.driver.R;
import com.meibanlu.driver.tool.ActivityControl;




public class LoadingDialog extends BaseDialog {
    private ImageView progress;

    public static LoadingDialog newInstance(){
        Log.i("LoadingDialog", ActivityControl.getCurrentActivity().getLocalClassName());
        return new LoadingDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_loading,null);
        progress=view.findViewById(R.id.loading);
        Dialog dialog=new Dialog(getActivity(),R.style.common_dialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        setAnimation();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        progress.clearAnimation();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        progress.clearAnimation();
    }

    private void setAnimation(){
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.dialog_progress_anim);
        progress.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(!LoadingDialog.this.isAdded()){
                    animation.cancel();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
