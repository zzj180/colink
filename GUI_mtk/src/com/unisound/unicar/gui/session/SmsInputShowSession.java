package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;

import com.unisound.unicar.gui.sms.SmsMessageSender;
import com.unisound.unicar.gui.sms.SmsSenderService;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

import com.unisound.unicar.gui.model.ContactInfo;
import com.unisound.unicar.gui.model.Telephony.Threads;
import com.unisound.unicar.gui.oem.RomContact;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.view.SmsContentView;
import com.unisound.unicar.gui.view.SmsContentView.ISmsContentViewListener;

public class SmsInputShowSession extends CommBaseSession {
    public static final String TAG = "SmsInputShowSession";

    private Context mContext;

    private StringBuilder mContentBuilder = new StringBuilder();
    private SmsContentView mSmsContentView = null;

    private String mContactNumber = "";
    private String mSMSContent = "";

    private ISmsContentViewListener mSmsContentViewListener = new ISmsContentViewListener() {
        @Override
        public void onBeginEdit() {
            Logger.d(TAG, "onBeginEdit");
        }

        @Override
        public void onEndEdit(String msg) {
            Logger.d(TAG, "!--->onEndEdit()---TODO:--msg = " + msg);
            mSmsContentView.setMessage(msg);
            // TODO:
            // String protocolStr = "{\"service\":\"DOMAIN_HAND_INPUT_CONTENT\" , \"content\":\"" +
            // msg + "\"}";
            // onUiProtocal(protocolStr);
        }

        @Override
        public void onCancel() {
            Logger.d(TAG, "!--->onCancel----mOriginType = " + mOriginType);
            mCancelProtocal = SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_CANCEL_SMS;
            onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_CANCEL, mCancelProtocal);
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        }

        @Override
        public void onOk() {
            Logger.d(TAG, "!--->onOk---mOkProtocal = " + mOkProtocal);
            // sendSMS(mContext, mContactNumber, mSMSContent); //XD delete 20150826
            mOkProtocal = SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_OK;
            onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_OK, mOkProtocal);
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SMS_SENDING);
        }

        @Override
        public void onTimeUp() {
            Logger.d(TAG, "!--->onTimeUp--------");
            sendSMS(mContext, mContactNumber, mSMSContent);

            onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_TIME_UP,
                    SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_TIME_UP);
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SMS_SENDING);
        }

        @Override
        public void onClearMessage() {
            Logger.d(TAG, "!--->onClearMessage----");
            mContentBuilder.setLength(0);
            mSMSContent = "";
            mSmsContentView.setMessage("");
            // String protocolStr = mContext.getString(R.string.sms_re_enter);
            onUiProtocal(SessionPreference.EVENT_NAME_SMS_CONTENT_RETYPE,
                    SessionPreference.EVENT_PROTOCAL_SMS_CONTENT_RETYPE);
        }

    };

    /**
     * update Sms Content
     * 
     * @param text
     */
    public void updateSmsContent(String text) {
        mSMSContent = text;
        if (null != mSmsContentView) {
            mSmsContentView.setMessage(text);
        }
    }

    public SmsInputShowSession(Context context, Handler handle) {
        super(context, handle);
        Logger.d(TAG, "!--->SmsInputShowSession()-------");
        mContext = context;
        mIsNeedAddTextView = true;
    }


    public void putProtocol(JSONObject jsonProtocol) {
        Logger.d(TAG, "!-->--putProtocol()--jsonProtocol =" + jsonProtocol);
        super.putProtocol(jsonProtocol);

        ContactInfo mSelectedContactInfo = new ContactInfo();

        String name = "";
        int photoId = 0;
        String tag = "";
        // String content = "";

        if (mSmsContentView == null) {
            mSmsContentView = new SmsContentView(mContext);
            mSmsContentView.setListener(mSmsContentViewListener);
        }

        // xd modify all mJsonObject --> mDataObject begin
        if (mDataObject != null) {
            if (mDataObject.has(SessionPreference.KEY_NAME)) {
                name = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_NAME);
            }
            mSelectedContactInfo.setDisplayName(name);
            if (mDataObject.has(SessionPreference.KEY_PIC)) {
                String photoIdStr = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_PIC);
                photoId = Integer.parseInt(photoIdStr);
                tag = JsonTool.getJsonValue(mDataObject, "numberAttribution");
            }
            mSelectedContactInfo.setPhotoId(photoId);
            mContactNumber = JsonTool.getJsonValue(mDataObject, "number");
            Logger.d(TAG, "!-->--putProtocol()--number = " + mContactNumber);

            if (mDataObject.has(SessionPreference.KEY_CONTENT)) {
                mSMSContent = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_CONTENT);
                Logger.d(TAG, "!-->--putProtocol()--mSMSContent = " + mSMSContent);
                onReceiveReInputProtocol();
            }
            Drawable drawable =
                    RomContact.loadContactDrawable(mContext, mSelectedContactInfo.getPhotoId());
            mSmsContentView.initRecipient(drawable, mSelectedContactInfo.getDisplayName(),
                    mContactNumber + " " + tag);
        }
        // xd modify all mJsonObject --> mDataObject end

        if (mIsNeedAddTextView) addSessionView(mSmsContentView);
        mSmsContentView.setMessage(mSMSContent);
        addSessionAnswerText(mAnswer);
    }


    /**
     * if Received ReInput Protocol, cancel CountDownTimer
     */
    private void onReceiveReInputProtocol() {
        if (TextUtils.isEmpty(mSMSContent) && null != mSmsContentView) {
            Logger.d(TAG, "!--->mSMSContent is null, cancelCountDownTimer...");
            mSmsContentView.cancelCountDownTimer();
        }
    }

    @Override
    public void onTTSEnd() {
        Logger.d(TAG, "!--->onTTSEnd");
        super.onTTSEnd();

        if (!TextUtils.isEmpty(mSmsContentView.getMessage())) {
            Logger.d(TAG, "!--->onTTSEnd()-----Sms Content is not null, begin DELAY_AUTO CONFIRM");
            mSmsContentView.startCountDownTimer(GUIConfig.TIME_DELAY_AUTO_CONFIRM);
        }
    }


    @Override
    public void release() {
        Logger.d(TAG, "!--->--release()---");
        super.release();
        if (null != mSmsContentView) {
            mSmsContentView.cancelCountDownTimer();
            mSmsContentView.setListener(null);
        }
    }

    protected void editShowContent() {
        mSmsContentView.becomeFirstRespond();
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
