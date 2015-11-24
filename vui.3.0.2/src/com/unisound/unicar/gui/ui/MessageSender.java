/**
 * Copyright (c) 2012-2013 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : MessageSender.java
 * @ProjectName : VoiceCenter
 * @PakageName : cn.yunzhisheng.voicecenter.service
 * @Author : Brant
 * @CreateDate : 2013-7-5
 */
package com.unisound.unicar.gui.ui;

import com.unisound.unicar.gui.utils.Logger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2013-7-5
 * @ModifiedBy : Brant
 * @ModifiedDate: 2013-7-5
 * @Modified: 2013-7-5: 实现基本功能
 */
public class MessageSender {
    private static final String TAG = "MessageSender";
    private Context mContext;

    public MessageSender(Context context) {
        mContext = context;
    }

    public void sendMessage(Intent intent, String receiverPermission) {
        Logger.d(TAG, "sendMessage:intent " + intent + ",permission " + receiverPermission);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Logger.d(TAG, "sendMessage:intent key " + key + ",value " + bundle.get(key));
            }
        }
        mContext.sendBroadcast(intent, receiverPermission);
    }

    public void sendMessage(Intent intent) {
        sendMessage(intent, null);
    }

    public void sendMessage(String message, Bundle extras, String receiverPermission) {
        Intent intent = new Intent(message);
        intent.putExtras(extras);
        sendMessage(intent, receiverPermission);
    }

    public void sendMessageWithBundle(String message, Bundle extras) {
        sendMessage(message, extras, null);
    }

    public void sendMessage(String action, String receiverPermission) {
        Intent intent = new Intent(action);
        sendMessage(intent, receiverPermission);
    }

    public void sendMessage(String action) {
        sendMessage(action, null);
    }

    public void sendOrderedMessage(String action) {
        sendOrderedMessage(action, null);
    }

    public void sendOrderedMessage(String action, String receiverPermission) {
        Intent intent = new Intent(action);
        sendOrderedMessage(intent, receiverPermission);
    }

    public void sendOrderedMessage(Intent intent, String receiverPermission) {
        Logger.d(TAG, "sendOrderedMessage:intent " + intent + ",permission " + receiverPermission);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Logger.d(TAG, "sendOrderedMessage:intent key " + key + ",value " + bundle.get(key));
            }
        }
        mContext.sendOrderedBroadcast(intent, receiverPermission);
    }
}
