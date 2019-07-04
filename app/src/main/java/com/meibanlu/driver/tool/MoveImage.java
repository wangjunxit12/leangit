package com.meibanlu.driver.tool;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class MoveImage extends android.support.v7.widget.AppCompatImageView implements View.OnTouchListener {
    public MoveImage(Context context) {
        super(context);
    }

    public MoveImage(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        setOnTouchListener(this);
    }

    public MoveImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setLocation(int x, int y) {
        this.setFrame(x, y - this.getHeight(), x + this.getWidth(), y);
    }

    // 移动
    public boolean autoMouse(MotionEvent event) {
        boolean rb = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                this.setLocation((int) event.getX(), (int) event.getY());
                rb = true;
                break;
        }
        return rb;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        autoMouse(event);
        return false;
    }
}

