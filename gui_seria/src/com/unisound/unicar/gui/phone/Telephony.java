/**
 * Copyright (c) 2012-2014 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : Telephony.java
 * @ProjectName : Tool
 * @PakageName : cn.yunzhisheng.phone
 * @Author : Brant
 * @CreateDate : 2014-6-21
 */
package com.unisound.unicar.gui.phone;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import com.unisound.unicar.gui.utils.Logger;

import com.android.internal.telephony.ITelephony;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-6-21
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-6-21
 * @Modified: 2014-6-21: 实现基本功能
 */
public class Telephony {
    private static final String TAG = "Telephony";

    public static void answerRingingCall(Context context) {
        // Make sure the phone is still ringing
        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Logger.d(TAG, "!--->answerRingingCall tm.getCallState() : " + tm.getCallState());
        if (tm.getCallState() != TelephonyManager.CALL_STATE_RINGING) {
            return;
        }

        // Answer the phone
        try {
            Logger.d(TAG, "!--->answerRingingCall in");
            answerPhoneAIDL(context);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG,
                    "Error trying to answer using telephony service.  Falling back to headset.");
            answerPhoneHeadsetHook(context);
        }
    }

    public static ITelephony getITelephony(Context context) {
        TelephonyManager mTelephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        Class c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            // 获取声明的方法
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            return (ITelephony) getITelephonyMethod.invoke(mTelephonyManager, (Object[]) null); // 获取实例
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void answerPhoneHeadsetHook(Context context) {
        Logger.d(TAG, "answerPhoneHeadsetHook");
        Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_HEADSETHOOK));
        try {
            context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
            Logger.d(TAG, "ACTION_MEDIA_BUTTON broadcasted...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
        headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        headSetUnPluggedintent.putExtra("state", 1); // 0 = unplugged 1 =
                                                     // Headset with
                                                     // microphone 2 =
                                                     // Headset without
                                                     // microphone
        headSetUnPluggedintent.putExtra("name", "Headset");
        // TODO: Should we require a permission?
        try {
            context.sendOrderedBroadcast(headSetUnPluggedintent, null);
            Logger.d(TAG, "ACTION_HEADSET_PLUG broadcasted ...");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.d(TAG, "Catch block of ACTION_HEADSET_PLUG broadcast");
            Logger.d(TAG, "Call Answered From Catch Block !!");
        }

        Logger.d(TAG, "Call Answered using headsethook");
    }

    public static void silenceRinger(Context context) throws RemoteException {
        ITelephony telephonyService = getITelephony(context);
        // Silence the ringer and answer the call!
        telephonyService.silenceRinger();
    }

    public static void answerPhoneAIDL(Context context) throws RemoteException {
        ITelephony telephonyService = getITelephony(context);
        telephonyService.answerRingingCall();
    }

    public static void endCall(Context context) {
        ITelephony telephonyService = getITelephony(context);
        // Silence the ringer and answer the call!
        Logger.d(TAG, "endCall");
        try {
            Logger.d(TAG, "endCall in");
            telephonyService.endCall();
        } catch (RemoteException e) {
            e.printStackTrace();
            Logger.d(TAG,
                    "Error trying to endCall using telephony service.  Falling back to headset.");
            answerPhoneHeadsetHook(context);
        }
    }

    /**
     * @Description : TODO 判断当前是否在通话
     * @Author : yujun
     * @CreateDate : 2014-11-25
     */
    public static boolean phoneIsInUse(Context context) {
        boolean phoneInUse = false;
        try {
            ITelephony phone = getITelephony(context);
            if (phone != null) {
                phoneInUse = !phone.isIdle();
            }
        } catch (Exception e) {
            Logger.d(TAG, "phone.isIdle() failed");
        }
        return phoneInUse;
    }
}
