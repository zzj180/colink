package com.unisound.unicar.gui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 
 * @author xiaodong
 * @date 20150805
 */
@SuppressLint("NewApi")
public class FloatView extends FrameLayout {
    private static final String TAG = "FloatView";

    /** float view Window Params */
    protected WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();

    protected WindowManager mWindowManager;

    /** status Bar height */
    protected int mStatusBarHeight = 25;

    /** screen size */
    protected Point mScreenSize = new Point();

    private boolean mShown = false;

    public FloatView(Context context) {
        super(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        /*
         * Display display = mWindowManager.getDefaultDisplay(); if
         * (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) { mScreenSize.y =
         * display.getHeight(); mScreenSize.x = display.getWidth(); } else {
         * display.getSize(mScreenSize); }
         */

        getScreenSize(context);
        mStatusBarHeight = DeviceTool.getStatusBarHeight(context);
        Logger.d(TAG, "!--->mScreenSize.x = " + mScreenSize.x + "; mScreenSize.y = "
                + mScreenSize.y + "; statusBarHeight = " + mStatusBarHeight);
    }

    /**
     * get screen size XD 20150901 added
     * 
     * @param context
     */
    protected void getScreenSize(Context context) {
        DisplayMetrics dm = DeviceTool.getDisplayMetrics(context);
        mScreenSize.x = dm.widthPixels;
        mScreenSize.y = dm.heightPixels;
    }

    @Override
    public boolean isShown() {
        return mShown;
    }

    public void show() {
        if (mShown) {
            return;
        }
        Logger.d(TAG, "show");
        mShown = true;
        mWindowManager.addView(this, mWindowParams);
    }

    public void hide() {
        if (mShown) {
            Logger.d(TAG, "hide");
            mShown = false;
            mWindowManager.removeView(this);
        }
    }
}
