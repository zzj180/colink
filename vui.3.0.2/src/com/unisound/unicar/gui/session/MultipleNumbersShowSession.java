package com.unisound.unicar.gui.session;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.unisound.unicar.gui.model.PhoneNumberInfo;
import com.unisound.unicar.gui.preference.PrivatePreference;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.PickPhoneNumberView;
import com.unisound.unicar.gui.view.PickPhoneNumberView.IPickPhoneNumberViewListener;

/**
 * One person has multiple number
 * 
 * @author xiaodong
 * 
 */
public class MultipleNumbersShowSession extends ContactSelectBaseSession {
    public static final String TAG = "MultipleNumbersShowSession";
    PickPhoneNumberView mPickPhoneNumberView;
    protected PhoneNumberInfo mSelectedPhoneNumberInfo = new PhoneNumberInfo();

    private static final String KEY_DELAY_CALL_PHONE = "KEY_DELAY_CALL_PHONE";
    private static final boolean DEFAULT_DELAY_CALL_PHONE = true;
    private boolean mDelayCall = false;

    /**
     * One person has multiple number
     * 
     * @param context
     * @param handle
     */
    public MultipleNumbersShowSession(Context context, Handler handle) {
        super(context, handle);
        Logger.d(TAG, "!--->MultipleNumbersShowSession()-------");
        mDelayCall =
                PrivatePreference.getBooleanValue(KEY_DELAY_CALL_PHONE, DEFAULT_DELAY_CALL_PHONE);
    }


    // {"origin_code":"CALL","domain":"SESSION_SHOW","data":{"schedule_type":"MUTIPLE_NUMBERS","number":[{"to_select":{"service":"DOMAIN_LOCAL","select":"1"},"number":"12345"},{"to_select":{"service":"DOMAIN_LOCAL","select":"2"},"number":"23415"},{"to_select":{"service":"DOMAIN_LOCAL","select":"3"},"number":"54321"}],"text":"第二个","name":"张三"},"tts":"请说第几个选择或说取消","type":"MUTIPLE_NUMBERS","origin_type":"cn.yunzhisheng.call"}

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "!--->putProtocol()-------jsonProtocol = " + jsonProtocol);
        // JSONArray dataArray = getJsonArray(mJsonObject, "number");

        JSONObject dataObject = JsonTool.getJSONObject(jsonProtocol, SessionPreference.KEY_DATA);
        String numberValue = JsonTool.getJsonValue(dataObject, SessionPreference.KEY_NUMBER, "");
        JSONArray dataArray = JsonTool.parseToJSONOArray(numberValue);

        if (dataArray != null) {
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = JsonTool.getJSONObject(dataArray, i);
                Logger.d(TAG, "--item " + i + " : " + item); // {"to_select":{"service":"DOMAIN_LOCAL","select":"1"},"number":"12345"}
                PhoneNumberInfo phoneItem = new PhoneNumberInfo();
                String number = JsonTool.getJsonValue(item, SessionPreference.KEY_NUMBER);
                phoneItem.setNumber(number);
                if (i == 0) {
                    mSelectedPhoneNumberInfo.setNumber(number);
                }
                Logger.d(TAG, "!--->putProtocol()--number = " + number);
                phoneItem.setNumber(JsonTool.getJsonValue(item, SessionPreference.KEY_NUMBER));
                Logger.d(
                        TAG,
                        "!--->putProtocol()--number = "
                                + JsonTool.getJsonValue(item, SessionPreference.KEY_NUMBER));
                // phoneItem.setAttribution(getJsonValue(item, "numberAttribution"));
                mPhoneNumberInfoList.add(phoneItem);
                Logger.d(
                        TAG,
                        "!--->to_select = "
                                + JsonTool.getJsonValue(item, SessionPreference.KEY_TO_SELECT));
                mDataItemProtocalList.add(JsonTool.getJsonValue(item,
                        SessionPreference.KEY_TO_SELECT));
            }
            // String displayName = getJsonValue(mJsonObject, "name");
            String displayName = JsonTool.getJsonValue(dataObject, SessionPreference.KEY_NAME);
            Logger.d(TAG, "!--->putProtocol()--name= " + displayName);
            String photoId = "0";
            // if (null != mJsonObject && mJsonObject.has("pic")) {//mJsonObject is null
            if (dataObject.has("pic")) {
                photoId = JsonTool.getJsonValue(mDataObject, "pic");// mJsonObject--->mDataObject
            }
            if (mPickPhoneNumberView == null) {
                mPickPhoneNumberView = new PickPhoneNumberView(mContext);
                mPickPhoneNumberView.initView(displayName, Integer.parseInt(photoId),
                        mPhoneNumberInfoList);

                mPickPhoneNumberView.setPickListener(mPickViewListener);
                mPickPhoneNumberView.setListener(mPickPhoneNumberListener);
            }
            addSessionView(mPickPhoneNumberView);

        }
        addAnswerViewText(mAnswer);
        // playTTS(mTTS);
    }


    @Override
    public void onTTSEnd() {
        // TODO Auto-generated method stub
        super.onTTSEnd();
        Logger.d(TAG, "!--->onTTSEnd()---startCountDownTimer:");
        if (isNeedShowTimer()) {
            mPickPhoneNumberView.startCountDownTimer(GUIConfig.TIME_DELAY_AUTO_CONFIRM);
        }
    }

    /**
     * isNeedShowTimer
     * 
     * @return
     */
    private boolean isNeedShowTimer() {
        return SessionPreference.DOMAIN_CALL.equals(mOriginType) && mDelayCall;
    }

    protected IPickPhoneNumberViewListener mPickPhoneNumberListener =
            new IPickPhoneNumberViewListener() {

                @Override
                public void onOk() {
                    Logger.d(TAG, "!--->mPickPhoneNumberListener---onCancel()----");
                    executeCall(mSelectedPhoneNumberInfo.getNumber());

                    onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_OK, mOkProtocal); // xd
                                                                                           // added
                                                                                           // 20150702
                    mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
                }

                @Override
                public void onCancel() {
                    Logger.d(TAG, "!--->mPickPhoneNumberListener---onCancel()----mOriginType = "
                            + mOriginType);
                    if (SessionPreference.DOMAIN_CALL.equals(mOriginType)) {
                        mCancelProtocal = SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_CANCEL_CALL;
                    } else if (SessionPreference.DOMAIN_SMS.equals(mOriginType)) {
                        mCancelProtocal = SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_CANCEL_SMS;
                    }
                    onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_CANCEL, mCancelProtocal); // xd
                                                                                                   // modify
                                                                                                   // 20150702
                    mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE); // xd
                                                                                                     // modify
                                                                                                     // 20150702
                }

                @Override
                public void onTimeUp() {
                    Logger.d(TAG, "!--->mPickPhoneNumberListener---onTimeUp()-----mOriginType = "
                            + mOriginType);
                    if (SessionPreference.DOMAIN_CALL.equals(mOriginType)) {
                        executeCall(mSelectedPhoneNumberInfo.getNumber());
                        onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_TIME_UP,
                                SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_TIME_UP); // xd added
                                                                                      // 20150702
                        // mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
                    }
                }
            };

    private void executeCall(String number) {
        Logger.d(TAG, "!--->executeCall: number " + number);
        if (mContext != null) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(callIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Logger.e(TAG, "call onOk context is null...");
        }
    }


    @Override
    public void release() {
        Logger.d(TAG, "!--->release()------");
        super.release();
        if (isNeedShowTimer()) {
            mPickPhoneNumberView.cancelCountDownTimer();
        }
        if (mPickPhoneNumberView != null) {
            mPickPhoneNumberView.setListener(null);
        }
    }

}
