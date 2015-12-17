/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : BaseAsyncSearch.java
 * @ProjectName : vui_datamodel
 * @PakageName : cn.yunzhisheng.vui.location.operation
 * @Author : Brant
 * @CreateDate : 2014-11-1
 */
package com.unisound.unicar.gui.search.operation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-11-1
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-11-1
 * @Modified: 2014-11-1: 实现基本功能
 */
public class BaseAsyncSearchClient {
    public static final String TAG = "BaseAsyncSearchClient";
    private static final int MSG_WHAT_TIMEOUT = 0;
    private boolean mCancel;
    private boolean mTimeout;
    private boolean mResult;
    private Runnable mRunnableTimeout;

    protected Context mContext;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Logger.d(TAG, "handleMessage:msg " + msg);
            switch (msg.what) {
                case MSG_WHAT_TIMEOUT:
                    mTimeout = true;
                    if (mRunnableTimeout != null && !mCancel && !mResult) {
                        Logger.d(TAG, "!--->MSG_WHAT_TIMEOUT---mRunnableTimeout.run");
                        mRunnableTimeout.run();
                    }
                    break;
            }
        };
    };

    public BaseAsyncSearchClient(Context context) {
        mContext = context;
    }

    protected void requestSearch(Runnable runnableTimeout) {
        if (runnableTimeout == null) {
            Logger.w(TAG, "requestSearch : runnableTimeout null!");
        }
        mTimeout = false;
        mCancel = false;
        mResult = false;
        mRunnableTimeout = runnableTimeout;
        Logger.d(TAG, "!--->requestSearch---sendEmptyMessageDelayed-10s-MSG_WHAT_TIMEOUT--Begin--");
        mHandler.sendEmptyMessageDelayed(MSG_WHAT_TIMEOUT, 10000);
    }

    protected void onSearchResultReach() {
        Logger.d(TAG, "!--->onSearchResultReach---removeTimeoutMessage");
        mResult = true;
        removeTimeoutMessage();
    }

    private void removeTimeoutMessage() {
        Logger.d(TAG, "!--->removeTimeoutMessage---");
        if (mHandler != null) {
            mHandler.removeMessages(MSG_WHAT_TIMEOUT);
        } else {
            Logger.w(TAG, "removeTimeoutMessage:mHandler has been released!");
        }
    }

    public boolean isTimeout() {
        return mTimeout;
    }

    public boolean isCanceled() {
        return mCancel;
    }

    public void cancel() {
        Logger.d(TAG, "!--->cancel---removeTimeoutMessage");
        mCancel = true;
        removeTimeoutMessage();
    }

    public void release() {
        Logger.d(TAG, "release");
        mContext = null;
        mHandler.removeMessages(MSG_WHAT_TIMEOUT);
        mHandler = null;
    }

}
