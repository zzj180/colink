package com.unisound.unicar.gui.smsdefault;

import java.text.SimpleDateFormat;

import com.unisound.unicar.gui.utils.ContactsUtil;
import com.unisound.unicar.gui.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * SmsReceiver
 * 
 * @author xiaodong
 * @date 20150826
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = SmsReceiver.class.getSimpleName();

    private long date;
    private String phoneNumber;
    private StringBuilder messageBody = new StringBuilder();

    /**
     * receive sms
     */
    public static final String SMS_RECEIVE_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Logger.d(TAG, "!--->onReceive--intent " + intent);
        String action = intent.getAction();
        if (action.equals(SMS_RECEIVE_ACTION)) {
            Bundle bundle = intent.getExtras();
            Logger.d(TAG, "!--->onReceive--bundle:" + bundle);
            if (null != bundle) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] msg = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                for (SmsMessage currMsg : msg) {
                    messageBody.append(currMsg.getDisplayMessageBody());
                    phoneNumber = currMsg.getDisplayOriginatingAddress();
                    date = currMsg.getTimestampMillis();
                }
                String time = formatTimeStampString(date);
                String sender = ContactsUtil.queryContactNameByNumber(context, phoneNumber);
                if ("".equals(sender)) {
                    sender = phoneNumber;
                }
                Logger.d(TAG, "!--->onReceive--time = " + time + "; sender = " + sender
                        + "; phoneNumber = " + phoneNumber + "; msg = " + messageBody.toString());
                addSmsToDB(context, phoneNumber, messageBody.toString());
            }
        }

    }


    public static String formatTimeStampString(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ctime = sdf.format(time);
        return ctime;
    }


    /**
     * add SMS To DB
     * 
     * @param context
     * @param address
     * @param content
     */
    private void addSmsToDB(Context context, String address, String content) {
        ContentValues values = new ContentValues();
        values.put("date", System.currentTimeMillis());
        values.put("read", 0);// 0为未读信息
        values.put("type", 1);// 1为收件箱信息
        values.put("address", address);
        values.put("body", content);
        context.getContentResolver().insert(Uri.parse("content://sms"), values);
    }

}
