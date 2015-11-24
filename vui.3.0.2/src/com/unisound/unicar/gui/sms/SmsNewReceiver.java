/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : SMSNewReceiver.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.ishuoshuo.service.sms
 * @Author : CavanShi
 * @CreateDate : 2013-3-13
 */
package com.unisound.unicar.gui.sms;

import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsMessage;

import com.unisound.unicar.gui.preference.UserPreference;
import com.unisound.unicar.gui.sms.SmsNewObserver.SMS;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : CavanShi
 * @CreateDate : 2013-3-13
 * @ModifiedBy : CavanShi
 * @ModifiedDate: 2013-3-13
 * @Modified: 2013-3-13: 实现基本功能
 */
public class SmsNewReceiver extends BroadcastReceiver {
    public static final String TAG = "SMSNewReceiver";

    public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private UserPreference mPreferenceAction;

    private ContentValues mValues;
    private boolean mServiceStarted;
    private ISMSReceiver mMessageReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i(TAG, "onReceive() getAction is :" + intent.getAction());
        mPreferenceAction = new UserPreference(context);
        mValues = new ContentValues();

        if (mPreferenceAction.getSMSSpeech()) {
            mServiceStarted = true;
        }

        if (intent.getAction().equals(ACTION)/*
                                              * && mPhoneRingerMode ==
                                              * AudioManager.RINGER_MODE_NORMAL
                                              */
                && mPreferenceAction.getSMSSpeech()) {
            this.abortBroadcast();

            SmsMessage[] messages = getMessageFromIntent(intent);
            StringBuffer msgBody = new StringBuffer();
            String address = null;
            long time = 0;

            for (SmsMessage message : messages) {
                address = message.getOriginatingAddress();
                msgBody.append(message.getDisplayMessageBody());
            }

            if (mServiceStarted) {
                mValues.put("read", 1);
            } else {
                mValues.put("read", 0);
            }
            time = System.currentTimeMillis();
            mValues.put("address", address);
            mValues.put("body", msgBody.toString());
            mValues.put("date", time);
            // mValues.put("read", 0);
            mValues.put("type", 1);
            mValues.put("status", -1);
            mValues.put("protocol", 0);

            Uri insert = context.getContentResolver().insert(Uri.parse("content://sms"), mValues);

            final String[] PORJECTION =
                    new String[] {SMS._ID, SMS.TYPE, SMS.ADDRESS, SMS.BODY, SMS.DATE,
                            SMS.THREAD_ID, SMS.READ, SMS.PROTOCOL, SMS.PERSON_ID};

            Cursor c = context.getContentResolver().query(insert, PORJECTION, null, null, null);
            if (c.moveToNext()) {
                if (mServiceStarted) {
                    SmsItem item = new SmsItem();
                    item.setSmsUri(insert.toString());
                    item.setMessage(msgBody.toString());
                    item.setNumber(address);
                    item.setTime(System.currentTimeMillis());

                    /*
                     * Intent i = new Intent(context, SmsNewPopDialog.class);
                     * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |
                     * Intent.FLAG_ACTIVITY_CLEAR_TOP); i.putExtra("sms", item);
                     * context.startActivity(i);
                     */
                    if (mMessageReceiverListener != null) {
                        mMessageReceiverListener.onMessageReveived(item);
                    }
                    mServiceStarted = false;
                }
            }
        }
    }

    public void setMessageReveiverListener(ISMSReceiver l) {
        mMessageReceiverListener = l;
    }

    private SmsMessage[] getMessageFromIntent(Intent intent) {

        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObj = new byte[messages.length][];
        for (int i = 0; i < messages.length; i++) {
            pduObj[i] = (byte[]) messages[i];
        }

        byte[][] pdus = new byte[pduObj.length][];
        int pduCount = pdus.length;
        SmsMessage[] mess = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObj[i];
            mess[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return mess;
    }

    private boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList =
                activityManager.getRunningServices(100);
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public interface ISMSReceiver {
        void onMessageReveived(SmsItem msg);
    }
}
