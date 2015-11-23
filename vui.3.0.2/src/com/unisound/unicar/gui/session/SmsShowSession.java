package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.model.PhoneNumberInfo;
import com.unisound.unicar.gui.model.Telephony.Threads;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.sms.SmsMessageSender;
import com.unisound.unicar.gui.sms.SmsSenderService;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * SmsInputShowSession -->say "OK" --> SmsShowSession --> send SMS
 * 
 * @author xiaodong
 * 
 */
public class SmsShowSession extends CommBaseSession {

    public static final String TAG = SmsShowSession.class.getSimpleName();

    SmsShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
    }

    public void putProtocol(JSONObject jsonObject) {
        Logger.d(TAG, "!--->-putProtocol: jsonObject " + jsonObject);
        super.putProtocol(jsonObject);
        mBlockAutoStart = true;
        PhoneNumberInfo mSelectedPhoneNumberInfo = new PhoneNumberInfo();
        mSelectedPhoneNumberInfo.setNumber(JsonTool.getJsonValue(mDataObject, "number")); // mJsonObject
                                                                                          // -->
                                                                                          // mDataObject
        String sendText = JsonTool.getJsonValue(mDataObject, "content"); // mJsonObject -->
                                                                         // mDataObject
        if (sendText == null || "".equals(sendText)) {
            sendText = "";
        }
        Logger.d(TAG, "!--->-putProtocol()--number = " + mSelectedPhoneNumberInfo.getNumber()
                + "; sendText = " + sendText);
        String phoneNumber = mSelectedPhoneNumberInfo.getNumber();

        sendSMS(mContext, phoneNumber, sendText);

        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SMS_SENDING);
    }

    /**
     * 
     * @param context
     * @param phoneNumber
     * @param sendText
     */
    private void sendSMS(Context context, String phoneNumber, String sendText) {
        Logger.d(TAG, "!--->sendSMS()--phoneNumber = " + phoneNumber + ";--sendText = " + sendText);
        String[] phoneNumberArray = {phoneNumber};
        try {
            SmsMessageSender smsSender =
                    new SmsMessageSender(context, phoneNumberArray, sendText,
                            Threads.getOrCreateThreadId(context, phoneNumber));
            smsSender.sendMessage(SmsSenderService.NO_TOKEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
