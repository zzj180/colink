package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.unisound.unicar.gui.model.ContactInfo;
import com.unisound.unicar.gui.model.PhoneNumberInfo;
import com.unisound.unicar.gui.oem.RomContact;
import com.unisound.unicar.gui.oem.RomCustomerProcessing;
import com.unisound.unicar.gui.oem.RomDevice;
import com.unisound.unicar.gui.oem.RomSystemSetting;
import com.unisound.unicar.gui.preference.PrivatePreference;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.CallContentView;
import com.unisound.unicar.gui.view.CallContentView.ICallContentViewListener;

/**
 * Call out Confirm session
 * 
 * @author xiaodong
 * 
 */
public class CallConfirmShowSession extends ContactSelectBaseSession {
    public static final String TAG = "CallConfirmShowSession";
    public static final String BACK_PROTOCAL = "{\"service\":\"DOMAIN_KEY\",\"key\":\"back\"}";
    // public static final String OK_PROTOCAL =
    // "{\"message\":\"确定\",\"service\":\"DOMAIN_LOCAL\",\"confirm\":\"ok\"}";
    protected ContactInfo mSelectedContactInfo = new ContactInfo();
    protected PhoneNumberInfo mSelectedPhoneNumberInfo = new PhoneNumberInfo();
    protected CallContentView mCallContentView = null;
    private boolean mStepTag;
    private boolean mCancleConfirmCallTag = true;

    private static final String KEY_DELAY_CALL_PHONE = "KEY_DELAY_CALL_PHONE";
    private static final boolean DEFAULT_DELAY_CALL_PHONE = true;

    private boolean mDelayCall = false;

    protected ICallContentViewListener mCallContentViewListener = new ICallContentViewListener() {

        @Override
        public void onCancel() {
            Logger.d(TAG, "!--->mCallContentViewListener---onCancel()--mOriginType = "
                    + mOriginType);
            RomCustomerProcessing.sendMessageToOtherHangUp(mContext,
                    mSelectedPhoneNumberInfo.getNumber());
            mCancelProtocal = SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_CANCEL_CALL;
            onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_CANCEL, mCancelProtocal); // xd
                                                                                           // modify
                                                                                           // 20150702
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE); // xd
                                                                                             // modify
                                                                                             // 20150702
        }

        @Override
        public void onOk() {
            Logger.d(TAG, "!--->mCallContentViewListener---onOk()--mOkProtocal=" + mOkProtocal);
            // executeCall(mSelectedPhoneNumberInfo.getNumber()); //XD delete 20150826
            mOkProtocal = SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_OK;
            onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_OK, mOkProtocal); // xd modify
                                                                                   // 20150702
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        }

        @Override
        public void onTimeUp() {
            Logger.d(TAG, "!--->mCallContentViewListener---onTimeUp()-----");
            executeCall(mSelectedPhoneNumberInfo.getNumber());

            onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_TIME_UP,
                    SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_TIME_UP); // xd added 20150702
            // mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        }
    };

    public CallConfirmShowSession(Context context, Handler handle) {
        super(context, handle);
        Logger.d(TAG, "!--->----CallConfirmShowSession()------");
        mDelayCall =
                PrivatePreference.getBooleanValue(KEY_DELAY_CALL_PHONE, DEFAULT_DELAY_CALL_PHONE);
        mStepTag = false;
    }

    protected void addTextCommonView() {
        if (!mUserPreference.getConfirmBeforeCall() && !mOkProtocal.equals("")) {
            onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_OK, mOkProtocal);
            mStepTag = true;
        } else {
            super.addTextCommonView();
        }
    }

    /*
     * {"data":{"functionName":"onTalkProtocol","params":{"type":{"origin_code":"CALL","domain":
     * "SESSION_SHOW",
     * "data":{"schedule_type":"CONFIRM_CALL","number":"11111","text":"第一个","name":"张三"},
     * "tts":"正在为您准备电话"
     * ,"type":"CONFIRM_CALL","origin_type":"cn.yunzhisheng.call"},"functionID":"4"}}
     * ,"type":"CALL","moduleName":"VUI"}
     */


    /*
     * {"origin_code":"CALL","domain":"SESSION_SHOW","data":{"schedule_type":"CONFIRM_CALL","number":
     * "11111","text":"第一个","name":"张三"},
     * "tts":"正在为您准备电话","type":"CONFIRM_CALL","origin_type":"cn.yunzhisheng.call"}
     */
    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "!--->--putProtocol()--mStepTag+" + mStepTag + "; jsonProtocol = "
                + jsonProtocol);
        if (mStepTag) return;
        Logger.d(TAG, "!--->--putProtocol()--mDataObject = " + mDataObject);// mJSONObject--->mDataObject
        // mDataObject =
        // {"schedule_type":"CONFIRM_CALL","text":"打电话给刘蓓","number":"10086","on_confirm_ok":"{\"service\":\"cn.yunzhisheng.setting\",\"semantic\":{\"intent\":{\"confirm\":\"OK\"}},\"text\":\"确定\",\"code\":\"SETTING_EXEC\"}","on_confirm_cancel":"{\"service\":\"cn.yunzhisheng.setting\",\"semantic\":{\"intent\":{\"confirm\":\"CANCEL\"}},\"text\":\"取消\",\"code\":\"SETTING_EXEC\"}","name":"刘备"}

        String displayName = JsonTool.getJsonValue(mDataObject, "name");
        final String number = JsonTool.getJsonValue(mDataObject, "number");

        if (displayName == null || "".equals(displayName)) {
            displayName = number;
        }

        String tag = JsonTool.getJsonValue(mDataObject, "numberAttribution");
        Logger.d(TAG, "!--->--putProtocol()--number = " + number + "; displayName = " + displayName
                + "; tag = " + tag);
        int photoId = 0;
        try {
            if (mDataObject.has("pic")) {
                photoId = Integer.parseInt(JsonTool.getJsonValue(mDataObject, "pic"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSelectedContactInfo.setDisplayName(displayName);
        mSelectedContactInfo.setPhotoId(photoId);
        mSelectedContactInfo.setContactId(-1);
        mSelectedPhoneNumberInfo.setNumber(number);
        mSelectedPhoneNumberInfo.setContactId(-1);
        mSelectedPhoneNumberInfo.setId(-1);

        if (mCallContentView == null) {
            mCallContentView = new CallContentView(mContext);
            Drawable drawable =
                    RomContact.loadContactDrawable(mContext, mSelectedContactInfo.getPhotoId());
            mCallContentView.initView(drawable, mSelectedContactInfo.getDisplayName(),
                    mSelectedPhoneNumberInfo.getNumber(), tag);

            mCallContentView.setListener(mCallContentViewListener);
        }

        Logger.d(TAG, "!--->sendMessage : MESSAGE_ADD_ANSWER_VIEW--mCallContentView = "
                + mCallContentView);
        Message msg =
                mSessionManagerHandler.obtainMessage(SessionPreference.MESSAGE_ADD_ANSWER_VIEW,
                        mCallContentView);
        mSessionManagerHandler.sendMessage(msg);

        if (mCancleConfirmCallTag) {
            // onUiProtocal(BACK_PROTOCAL); //XD delete 20150702
            addAnswerViewText(mAnswer);
            setAutoStart(true);
        } else {
            startCallAction();
            // if (mType.equals("CONFIRM_CALL")) {
            // if (TextUtils.isEmpty(displayName)) {
            // ttsAnswer = mContext.getString(R.string.call_phone_confirm, number);
            // } else {
            // ttsAnswer = mContext.getString(R.string.call_phone_confirm_number, number);
            // }
            // } else if (TextUtils.isEmpty(displayName)) {
            // ttsAnswer = mContext.getString(R.string.connect_number, number);
            // mAnswer = ttsAnswer;
            // } else {
            // ttsAnswer = mContext.getString(R.string.connect_name, displayName);
            // mAnswer = ttsAnswer;
            // }
            addAnswerViewText(mAnswer);
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        }
    }

    protected void startCallAction() {
        mCallContentView.setModeConfirm();
        addSessionView(mCallContentView);
    }

    private void executeCall(String number) {
        Logger.d(TAG, "!--->executeCall: number " + number);
        if (mContext != null) {
            try {
            	if(RomDevice.hasBluePhoneClient(mContext)){
					RomSystemSetting.RomCustomDialNumber(mContext, number);
				}else{
					Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
					callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(callIntent);
				}	
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Logger.e(TAG, "call onOk context is null...");
        }
    }

    @Override
    public void onTTSEnd() {
        Logger.d(TAG, "!--->onTTSEnd---mDelayCall = " + mDelayCall);
        super.onTTSEnd();
        if (mDelayCall) {
            mCallContentView.startCountDownTimer(GUIConfig.TIME_DELAY_AUTO_CONFIRM);
        } else {
            executeCall(mSelectedPhoneNumberInfo.getNumber());
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        }
    }

    @Override
    public void release() {
        Logger.d(TAG, "!--->release");
        super.release();
        if (mCallContentView != null) {
            if (mDelayCall) {
                mCallContentView.cancelCountDownTimer();
            }
            mCallContentView.setListener(null);
        }
    }
}
