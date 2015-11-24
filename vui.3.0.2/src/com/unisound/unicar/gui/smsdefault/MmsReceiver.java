package com.unisound.unicar.gui.smsdefault;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.unisound.unicar.gui.utils.Logger;

/**
 * MmsReceiver
 * 
 * @author xiaodong
 * @date 20150826
 */
public class MmsReceiver extends BroadcastReceiver {

    private static final String TAG = MmsReceiver.class.getSimpleName();

    /**
     * receive MMS
     */
    public static final String MMS_RECEIVE_ACTION = "android.provider.Telephony.WAP_PUSH_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Logger.d(TAG, "!--->onReceive-MMS-intent:" + intent);
        String action = intent.getAction();
        if (action.equals(MMS_RECEIVE_ACTION)) {

        }

    }



}
