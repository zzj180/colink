package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.unisound.unicar.gui.model.ContactInfo;
import com.unisound.unicar.gui.model.PhoneNumberInfo;
import com.unisound.unicar.gui.oem.RomContact;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.ReceiveCallView;
import com.unisound.unicar.gui.view.ReceiveCallView.IReceiveCallViewListener;

/**
 * Call out Confirm session
 * 
 * @author xiaodong
 * 
 */
public class ReceiveCallSession extends ContactSelectBaseSession {

    public static final String TAG = "ReceiveCallSession";

    protected ContactInfo mSelectedContactInfo = new ContactInfo();
    protected PhoneNumberInfo mSelectedPhoneNumberInfo = new PhoneNumberInfo();
    protected ReceiveCallView mReceiveCallView = null;

    public ReceiveCallSession(Context context, Handler handle) {
        super(context, handle);
        Logger.d(TAG, "!--->----ReceiveCallSession()------");
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "!--->--putProtocol()--jsonProtocol = " + jsonProtocol);
        Logger.d(TAG, "!--->--putProtocol()--mDataObject = " + mDataObject);
        // mDataObject =
        // {"schedule_type":"CONFIRM_CALL","text":"打电话给刘蓓","number":"10086","on_confirm_ok":"{\"service\":\"cn.yunzhisheng.setting\",\"semantic\":{\"intent\":{\"confirm\":\"OK\"}},\"text\":\"确定\",\"code\":\"SETTING_EXEC\"}","on_confirm_cancel":"{\"service\":\"cn.yunzhisheng.setting\",\"semantic\":{\"intent\":{\"confirm\":\"CANCEL\"}},\"text\":\"取消\",\"code\":\"SETTING_EXEC\"}","name":"刘备"}

        String displayName = JsonTool.getJsonValue(mDataObject, "name");
        final String number = JsonTool.getJsonValue(mDataObject, "number");

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

        if (mReceiveCallView == null) {
            Drawable drawable =
                    RomContact.loadContactDrawable(mContext, mSelectedContactInfo.getPhotoId());

            mReceiveCallView =
                    new ReceiveCallView(mContext, mSelectedContactInfo.getDisplayName(),
                            mSelectedPhoneNumberInfo.getNumber(), drawable, tag);
            mReceiveCallView.setListener(mReceiveCallViewListener);
        }

        // Logger.d(TAG,
        // "!--->sendMessage : MESSAGE_ADD_ANSWER_VIEW--mCallContentView = "+mCallContentView);
        // Message msg =
        // mSessionManagerHandler.obtainMessage(SessionPreference.MESSAGE_ADD_ANSWER_VIEW,
        // mCallContentView);
        // mSessionManagerHandler.sendMessage(msg);
        addSessionView(mReceiveCallView);

    }

    IReceiveCallViewListener mReceiveCallViewListener = new IReceiveCallViewListener() {

        @Override
        public void onRejectInComingCall() {
            Logger.d(TAG, "!--->onRejectInComingCall----");
            // TODO Auto-generated method stub
            onUiProtocal(SessionPreference.EVENT_NAME_INCOMING_CALL,
                    SessionPreference.EVENT_PROTOCAL_ON_REJECT_CALL);
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_REJECT_CALL);
        }

        @Override
        public void onAnswerInComingCall() {
            Logger.d(TAG, "!--->onAnswerInComingCall----");
            // TODO Auto-generated method stub
            onUiProtocal(SessionPreference.EVENT_NAME_INCOMING_CALL,
                    SessionPreference.EVENT_PROTOCAL_ON_ANSWER_CALL);
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        }

    };

    @Override
    public void onTTSEnd() {
        Logger.d(TAG, "!--->onTTSEnd");
        super.onTTSEnd();

    }

    @Override
    public void release() {
        Logger.d(TAG, "!--->release");
        super.release();

    }
}
