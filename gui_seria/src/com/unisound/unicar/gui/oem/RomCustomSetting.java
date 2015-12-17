package com.unisound.unicar.gui.oem;

import android.content.Context;
import android.content.Intent;

import com.unisound.unicar.gui.utils.Logger;

public class RomCustomSetting {
    private final static String TAG = "RomCustomSetting";
    private final static String ACTION_CALL = "cn.inet.call";
    private final static String ACTION_ANSWER = "cn.inet.answer";
    private final static String ACTION_HANGUP = "cn.inet.hang.up";

    public static void excuteCall(Context mContext, String number) {
        Intent mIntent = new Intent(ACTION_CALL);
        mIntent.putExtra("number", number);
        mContext.sendBroadcast(mIntent);
        Logger.d(TAG, "Action : " + mIntent.toString() + " Number : " + number);
    }

    public static void answerCall(Context mContext) {
        mContext.sendBroadcast(new Intent(ACTION_ANSWER));
        Logger.d(TAG, "Action : " + ACTION_ANSWER);
    }

    public static void hangupCall(Context mContext) {
        mContext.sendBroadcast(new Intent(ACTION_HANGUP));
        Logger.d(TAG, "Action : " + ACTION_HANGUP);
    }
}
