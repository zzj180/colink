package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.PhoneNumberInfo;
import com.unisound.unicar.gui.oem.RomCustomSetting;
import com.unisound.unicar.gui.oem.RomDevice;
import com.unisound.unicar.gui.oem.RomSystemSetting;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.CallContentView.ICallContentViewListener;

/**
 * schedule_type: CALL_OK Confirm Call --> say "que ding" -->schedule_type:CALL_OK
 * 
 * @author xiaodong
 * 
 */
public class CallShowSession extends CallConfirmShowSession {
    public static final String TAG = "CallShowSession";
    PhoneNumberInfo phoneNumberInfo = new PhoneNumberInfo();

    public CallShowSession(Context context, Handler handle) {
        super(context, handle);
        mCallContentViewListener = new ICallContentViewListener() {

            public void onCancel() {
                Logger.d(TAG, "!--->CallShowSession---onCancel()---");
                cancelSession();
                // mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
                // onUiProtocal(mCancelProtocal);
                // mCallContentView.
                mAnswer = mContext.getString(R.string.operation_cancel);
                addSessionAnswerText(mAnswer);
                Logger.d(TAG, "--CallShowSession mAnswer : " + mAnswer + "--");
            }

            public void onOk() {
                Logger.d(TAG, "!--->CallShowSession---onOk()---");
                // onUiProtocal(mOkProtocal);
                String number = JsonTool.getJsonValue(mDataObject, "number");
                if(RomDevice.hasBluePhoneClient(mContext)){
        			RomSystemSetting.RomCustomDialNumber(mContext, number);
        		}else{
        			Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        			callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        			mContext.startActivity(callIntent);
        		}
            }

            @Override
            public void onTimeUp() {
                // TODO Auto-generated method stub
                Logger.w(TAG, "!--->CallShowSession---onTimeUp()-->onOk()--");
                onOk();
            }
        };
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "!--->putProtocol()-----jsonProtocol = " + jsonProtocol);
        mBlockAutoStart = true;
        Logger.d(TAG, "!--->putProtocol()-----mDataObject = " + mDataObject);
        // mDataObject =
        // {"contact":{"number":"10086","name":"刘备"},"schedule_type":"CALL_OK","text":"确定"}
        // String contactJson = JsonTool.getJsonValue(mDataObject, "contact");
        // JSONObject contactObj = JsonTool.parseToJSONObject(contactJson);

        // mDataObject = {"schedule_type":"CALL_OK","text":"确定","number":"10086","name":"测试"}
        String number = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_NUMBER);
        Logger.d(TAG, "!--->number = " + number);

        phoneNumberInfo.setNumber(number);
        // phoneNumberInfo.setAttribution(JsonTool.getJsonValue(mDataObject, "numberAttribution"));

        if (mContext != null) {
            try {
                Logger.d(TAG, "!--->Begin call:" + phoneNumberInfo.getNumber());
                if(RomDevice.hasBluePhoneClient(mContext)){
        			RomSystemSetting.RomCustomDialNumber(mContext, phoneNumberInfo.getNumber());
        		}else{
        			Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumberInfo.getNumber()));
        			callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        			mContext.startActivity(callIntent);
        		}
            } catch (Exception e) {
                cancelSession();
                Logger.e(TAG, mContext.getString(R.string.no_support_phone));
                Toast.makeText(mContext, R.string.no_support_phone_toast, Toast.LENGTH_LONG).show();
            }
        } else {
            Logger.e(TAG, "call onOk context is null...");
        }

        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }

    protected void startCallAction() {
        addSessionView(mCallContentView);
        mCallContentView.setModeNomal();
        // mCallContentView.startCountDownTimer(3000);
    }
}
