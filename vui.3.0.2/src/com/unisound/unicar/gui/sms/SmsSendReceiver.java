/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : SmsSendReceiver.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.vui.assistant.sms
 * @Author : Brant
 * @CreateDate : 2012-5-25
 */
package com.unisound.unicar.gui.sms;

import com.unisound.unicar.gui.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Handle incoming SMSes. Just dispatches the work off to a Service.
 * 
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2012-5-25
 * @ModifiedBy : Brant
 * @ModifiedDate: 2012-5-25
 * @Modified: 2012-5-25: 实现基本功能
 */
public class SmsSendReceiver extends BroadcastReceiver {
    public static final String TAG = "SmsSendReceiver";

    private static SmsSendReceiver sInstance;

    public static SmsSendReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new SmsSendReceiver();
        }
        return sInstance;
    }

    /**
     * @Description : onReceive
     * @Author : Brant
     * @CreateDate : 2012-5-25
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     *      android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "!--->onReceive action = " + intent.getAction() + "; ResultCode = "
                + getResultCode());
        intent.setClass(context, SmsSenderService.class);
        intent.putExtra("result", getResultCode());
        context.startService(intent);
    }

}
