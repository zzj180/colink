/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : SessionLinearLayout.java
 * @ProjectName : CarPlayNew
 * @PakageName : cn.yunzhisheng.carplay.view
 * @Author : Brant
 * @CreateDate : 2014-9-30
 */
package com.unisound.unicar.gui.view;

import com.unisound.unicar.gui.utils.Logger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @Module :
 * @Comments : LoadView LinearLayout
 * @Author : xiaodong
 * @CreateDate : 2015-7-6
 * @ModifiedBy :
 * @ModifiedDate:
 * @Modified:
 * 
 */
@SuppressLint("ClickableViewAccessibility")
public class LoadViewLinearLayout extends LinearLayout {
    public static final String TAG = "LoadViewLinearLayout";
    private DispatchKeyEventListener mDispatchKeyEventListener;
    private OnTouchEventListener mOnTouchEventListener;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public LoadViewLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LoadViewLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadViewLinearLayout(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Logger.d(TAG, "!--->dispatchKeyEvent: getKeyCode = " + event.getKeyCode() + ", Action = "
                + event.getAction());
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            // Do not response ACTION_UP
            Logger.d(TAG, "!--->Back pressed. KeyCode = " + event.getKeyCode() + ", Action = "
                    + event.getAction());
            return true;
        }
        if (mDispatchKeyEventListener != null) {
            return mDispatchKeyEventListener.dispatchKeyEvent(event);
        }

        return super.dispatchKeyEvent(event);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.d(TAG, "!--->onKeyDown: keyCode = " + keyCode);
        if (mDispatchKeyEventListener != null) {
            return mDispatchKeyEventListener.dispatchKeyEvent(event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Logger.d(TAG, "!--->onTouchEvent: getAction = " + event.getAction());
        if (mOnTouchEventListener != null) {
            return mOnTouchEventListener.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    public DispatchKeyEventListener getDispatchKeyEventListener() {
        return mDispatchKeyEventListener;
    }

    public void setDispatchKeyEventListener(DispatchKeyEventListener mDispatchKeyEventListener) {
        this.mDispatchKeyEventListener = mDispatchKeyEventListener;
    }

    public static interface DispatchKeyEventListener {
        boolean dispatchKeyEvent(KeyEvent event);
    }


    public OnTouchEventListener getOnTouchEventListener() {
        return mOnTouchEventListener;
    }

    public void setOnTouchEventListener(OnTouchEventListener onTouchEventListener) {
        this.mOnTouchEventListener = onTouchEventListener;
    }

    public static interface OnTouchEventListener {
        boolean onTouchEvent(MotionEvent event);
    }
}
