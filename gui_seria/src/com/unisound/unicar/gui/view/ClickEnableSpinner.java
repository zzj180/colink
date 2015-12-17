/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : ClickControlledSpinner.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-23
 */
package com.unisound.unicar.gui.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Spinner;

/**
 * 
 * The class inherited from Spinner. The drop-down list has the following characteristics:
 * <ol>
 * <li>Use {@link #setOnClickMyListener(OnClickSpinnerListener)} to listen the click event and can
 * implement self-fulfilling click response action (Spinner's default response action is to open the
 * list selection box).</ li>
 * <li>Click event occurs, the default response is no longer open the list selection box, please
 * call {@link #performClick()} instead.</ li>
 * </ol>
 * The purpose of the control is to compensate for that Spinner click event can not be caught, the
 * Spinner has its own solution with the click event response, and
 * {@link #setOnClickListener(OnClickListener)} interface is shielded, if we need to do something
 * (such as update list) after click event happened and before the list selection box appears, we'll
 * find no way, but ClickControlledSpinner did, it listens the click event, can let us perform some
 * actions after this, after actions finished, we must revoke {@link #performClick()} to display the
 * list selection box.
 * 
 * @date 2015-10-23
 * @author xiaodong.he
 */
public class ClickEnableSpinner extends Spinner {

    public ClickEnableSpinner(Context context) {
        super(context);
    }

    public ClickEnableSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ClickEnableSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isMoved = false;
    private Point touchedPoint = new Point();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchedPoint.x = x;
                touchedPoint.y = y;
                break;
            case MotionEvent.ACTION_MOVE:
                isMoved = true;
                break;
            case MotionEvent.ACTION_UP:
                if (isMoved) {
                    // 从上向下滑动
                    if (y - touchedPoint.y > 20) {}
                    // 从下向上滑动
                    else if (touchedPoint.y - y > 20) {}
                    // 滑动幅度小时，当作点击事件
                    else {
                        onClick();
                    }
                    isMoved = false;
                } else {
                    onClick();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void onClick() {
        if (mOnClickSpinnerListener != null && isEnabled()) {
            // use a thread to do something which maybe spend many times.
            new Thread() {
                public void run() {
                    mOnClickSpinnerListener.onClick();
                }
            }.start();
        }
    }


    private OnClickSpinnerListener mOnClickSpinnerListener;

    /**
     * 注册自定义的点击事件监听 Register the click event self-fulfilling listener.
     * 
     * @param onClickSpinnerListener
     */
    public void setOnClickMyListener(OnClickSpinnerListener onClickSpinnerListener) {
        this.mOnClickSpinnerListener = onClickSpinnerListener;
    }

    /**
     * 自定义点击事件监听. Click event self-fulfilling listener.
     * 
     * @author xiaodong.he
     */
    public interface OnClickSpinnerListener {
        /**
         * 点击时触发 警告：该方法在非UI线程中执行
         * 
         * Triggers when click event occurs. Warning: this method does not run in UI thread.
         */
        public void onClick();
    }
}
