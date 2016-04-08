package com.aispeech.aios.adapter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @desc 基类接收器
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public abstract class BaseReceiver extends BroadcastReceiver {

    protected String mAction;
    protected IntentFilter mFilter;
    protected Context mContext;

    protected BaseReceiver(IntentFilter filter) {
        mFilter = filter;

        if (mFilter != null) {
            addAction();
        }
    }

    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mAction = intent.getAction();
        onReceiveIml(context, intent);
    }

    protected abstract void onReceiveIml(Context context, Intent intent);

    protected abstract void addAction();
}
