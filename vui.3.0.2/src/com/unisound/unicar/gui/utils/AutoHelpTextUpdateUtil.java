package com.unisound.unicar.gui.utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

import com.unisound.unicar.gui.ui.GUIMainActivity;

/**
 * Auto HelpText Update Util
 * 
 * @author XiaoDong
 * @date 20150728
 */
public class AutoHelpTextUpdateUtil {

    private static final String TAG = AutoHelpTextUpdateUtil.class.getSimpleName();

    private ArrayList<String> mHelpTextList = null;
    private Handler mUIHandler = null;
    private Timer mTimer = null;
    private MyTimerTask mTimerTask = null;
    private int mIndex = 0;

    private AutoHelpTextUpdateUtil() {}

    public AutoHelpTextUpdateUtil(ArrayList<String> helpTextList, Handler handler) {
        mHelpTextList = helpTextList;
        mUIHandler = handler;
    }

    public void setHelpTextList(ArrayList<String> helpTextList) {
        mHelpTextList = helpTextList;
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (null == mHelpTextList || mIndex >= mHelpTextList.size()) {
                Logger.w(TAG, "!--->mHelpTextList is null or index error, mIndex = " + mIndex);
                return;
            }
            String helpText = mHelpTextList.get(mIndex);
            mIndex++;
            if (mIndex == mHelpTextList.size()) {
                mIndex = 0;
            }
            updateAutoTextView(helpText);
        }
    };

    /**
     * start update help text
     */
    public void start() {
        Logger.d(TAG, "!--->------start--------mTimer = " + mTimer);
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimerTask = new MyTimerTask();
        mTimer.schedule(mTimerTask, 0, GUIConfig.TIME_AUTO_SHOW_HELP_TEXT);
    }

    /**
     * stop update help text
     */
    public void stop() {
        Logger.d(TAG, "!--->------stop--------");
        if (null != mTimerTask) {
            mTimerTask.cancel();
        }
        if (null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }
        mIndex = 0;
    }

    /**
     * Notify UI update AutoTextView
     * 
     * @param text
     */
    private void updateAutoTextView(String text) {
        if (null == mUIHandler) {
            return;
        }
        Message msg = new Message();
        msg.what = GUIMainActivity.MSG_UPDATE_AUTO_TEXT_VIEW;
        msg.obj = text;
        mUIHandler.sendMessage(msg);
    }
}
