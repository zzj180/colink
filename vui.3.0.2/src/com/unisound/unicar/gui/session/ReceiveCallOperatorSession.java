package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.phone.Telephony;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * Receive Call Operator Session Incoming call ->say "JieTong" or "Guaduan"
 * 
 * @author xiaodong
 * @date 20150717
 */
public class ReceiveCallOperatorSession extends CommBaseSession {

    public static final String TAG = ReceiveCallOperatorSession.class.getSimpleName();

    public ReceiveCallOperatorSession(Context context, Handler handle) {
        super(context, handle);
        Logger.d(TAG, "!--->----ReceiveCallOperatorSession()------");
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "!--->--putProtocol()--jsonProtocol = " + jsonProtocol);
        Logger.d(TAG, "!--->--putProtocol()--mDataObject = " + mDataObject);
        String operator = JsonTool.getJsonValue(mDataObject, "operator", "");
        Logger.d(TAG, "!--->putProtocol:---operator = " + operator);

        if (SessionPreference.VALUE_OPERATOR_INCOMING_CALL_ANSWER.equals(operator)) {
            Logger.d(TAG, "!--->VALUE_OPERATOR_INCOMING_CALL_ANSWER");
            // mAnswer = mContext.getString(R.string.answer_call);
            Telephony.answerRingingCall(mContext);
        } else if (SessionPreference.VALUE_OPERATOR_INCOMING_CALL_HUNGUP.equals(operator)) {
            Logger.d(TAG, "!--->VALUE_OPERATOR_INCOMING_CALL_HUNGUP");
            // mAnswer = mContext.getString(R.string.end_call);
            Telephony.endCall(mContext);
        }

    }

    @Override
    public void onTTSEnd() {
        Logger.d(TAG, "!--->onTTSEnd");
        super.onTTSEnd();

        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }

    @Override
    public void release() {
        Logger.d(TAG, "!--->release");
        super.release();

    }
}
