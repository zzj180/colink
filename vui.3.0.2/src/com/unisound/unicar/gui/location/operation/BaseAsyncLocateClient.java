/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : BaseAsyncLocateClient.java
 * @Author : Zhuoran
 * @CreateDate : 2015-7-20
 */
package com.unisound.unicar.gui.location.operation;

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
public class BaseAsyncLocateClient {
    public static final String TAG = "BaseAsyncLocateClient";
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
                        mRunnableTimeout.run();
                    }
                    break;
            }
        };
    };

    public BaseAsyncLocateClient(Context context) {
        mContext = context;
    }

    protected void requestLocate(Runnable runnableTimeout) {
        if (runnableTimeout == null) {
            Logger.w(TAG, "requestLocate : runnableTimeout null!");
        }
        mTimeout = false;
        mCancel = false;
        mResult = false;
        mRunnableTimeout = runnableTimeout;
        mHandler.sendEmptyMessageDelayed(MSG_WHAT_TIMEOUT, 10000);
    }

    protected void onLocateResultReach() {
        Logger.d(TAG, "onSearchResultReach");
        mResult = true;
        removeTimeoutMessage();
    }

    private void removeTimeoutMessage() {
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
        Logger.d(TAG, "cancel");
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
