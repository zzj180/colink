package com.aispeech.aios.adapter.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.service.FloatWindowService;

/**
 * @desc 定制节点接收器
 * @auth AISPEECH
 * @date 2016-01-14
 * @copyright aispeech.com
 */
public class CustomizeNodeReceiver extends BaseReceiver {

    private static final String TAG = "AIOS-Adapter-CustomizeNodeReceiver";

    public CustomizeNodeReceiver(IntentFilter filter) {
        super(filter);
    }

    @Override
    protected void onReceiveIml(Context context, Intent intent) {
        AILog.i(TAG,mAction);

        if (mAction.equals(Intent.ACTION_PACKAGE_ADDED) || mAction.equals(Intent.ACTION_PACKAGE_REMOVED)) {

            FloatWindowService.getRunningService().restartCustomizeNode();

        }
    }

    @Override
    protected void addAction() {
        mFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        mFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        mFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
    }

}
