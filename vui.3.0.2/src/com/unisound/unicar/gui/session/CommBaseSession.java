package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPreference;
import com.unisound.unicar.gui.utils.Logger;

public class CommBaseSession extends BaseSession implements ISessionUpdate {
    public static String TAG = "CommBaseSession";
    public String mCancelProtocal = null;
    public String mOkProtocal = null;
    // public JSONObject mJsonObject = null;
    protected UserPreference mUserPreference = new UserPreference(mContext);
    protected String mTtsText = "";
    protected boolean mBlockAutoStart;

    protected void addTextCommonView() {
        Logger.d(TAG, "!--->addTextCommonView()----mIsNeedAddTextView = " + mIsNeedAddTextView);
        if (mIsNeedAddTextView) {
            addSessionAnswerText(mQuestion);
            addSessionAnswerText(mAnswer);
        }
    }

    protected void editShowContent() {

    }

    CommBaseSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        // TAG = this.getClass().getName();
        mIsNeedAddTextView = true;
    }

    public void addSessionAnswerText(String ttsText) {
        addAnswerViewText(ttsText);
    }

    public void addSessionView(View view) {
        Logger.d(TAG, "!--->addSessionView()----view=" + view);
        addAnswerView(view);
    }

    public void addSessionView(View view, boolean fullScroll) {
        addAnswerView(view, fullScroll);
    }

    // {"origin_code":"CALL","domain":"SESSION_SHOW","data":{"schedule_type":"MUTIPLE_NUMBERS","number":[{"to_select":{"service":"DOMAIN_LOCAL","select":"1"},"number":"11111"},{"to_select":{"service":"DOMAIN_LOCAL","select":"2"},"number":"22222"},{"to_select":{"service":"DOMAIN_LOCAL","select":"3"},"number":"33333"}],"text":"第一个","name":"张三"},"tts":"请说第几个选择或说取消","type":"MUTIPLE_NUMBERS","origin_type":"cn.yunzhisheng.call"}

    public void putProtocol(JSONObject jsonObject) {
        Logger.d(TAG, "!--->putProtocol()--jsonObject " + jsonObject);
        super.putProtocol(jsonObject);

        // {"schedule_type":"MUTIPLE_NUMBERS","number":[{"to_select":{"service":"DOMAIN_LOCAL","select":"1"},"number":"11111"},{"to_select":{"service":"DOMAIN_LOCAL","select":"2"},"number":"22222"},{"to_select":{"service":"DOMAIN_LOCAL","select":"3"},"number":"33333"}],"text":"第一个","name":"张三"}
        Logger.d(TAG, "!--->putProtocol()--mDataObject = " + mDataObject);

        // mJsonObject = getJSONObject(mDataObject, SessionPreference.KEY_RESULT);//TODO:
        // KEY_RESULT-->"data" error NO THIS KEY KEY_RESULT
        // Logger.d(TAG, "!--->putProtocol()--mJsonObject = "+mJsonObject);

        mCancelProtocal = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_ON_CANCEL);
        Logger.d(TAG, "!--->putProtocol()--mCancelProtocal = " + mCancelProtocal);

        mOkProtocal = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_ON_OK);
        if (mOkProtocal == null) {
            // mOkProtocal = SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_OK;
            Logger.d(TAG, "!--->putProtocol()-mOkProtocal is null, set mOkProtocal = "
                    + mOkProtocal);
        }
        Logger.d(TAG, "!--->putProtocol()--mOkProtocal = " + mOkProtocal);
        addTextCommonView();
    }

    public void setAutoStart(boolean b) {
        this.mBlockAutoStart = b;
    }

    @Override
    public void editSession() {
        editShowContent();
    }

    @Override
    public void updateSession(JSONObject jsonObject) {

    }

    @Override
    public void release() {
        super.release();
        Logger.d(TAG, "!--->CommBaseSession--release----");
        // mJsonObject = null;
        mUserPreference = null;
    }

}
