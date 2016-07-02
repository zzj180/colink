package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.ChatWebView;

/**
 * Chat Web Show Session
 * 
 * @author xiaodong
 * @date 20150909
 */
public class ChatWebShowSession extends BaseSession {
    private static final String TAG = ChatWebShowSession.class.getSimpleName();

    private Context mContext;
    private ChatWebView mChatWebView;

    private static ChatWebShowSession mInstance;

    private ChatWebShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        Logger.d(TAG, "ChatWebShowSession create");
        this.mContext = context;
    }

    /**
     * 
     * @param context
     * @param sessionManagerHandler
     * @return
     */
    public synchronized static ChatWebShowSession getInstance(Context context,
            Handler sessionManagerHandler) {
        if (null == mInstance) {
            mInstance = new ChatWebShowSession(context, sessionManagerHandler);
        }
        return mInstance;
    };

    @Override
    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "putProtocal--jsonProtocol = " + jsonProtocol.toString());

        JSONObject data = JsonTool.getJSONObject(jsonProtocol, SessionPreference.KEY_DATA);
        String chatUrl = JsonTool.getJsonValue(data, SessionPreference.KEY_CHAT_H5URL);
        String answer = mAnswer;
        if (mChatWebView == null) {
            mChatWebView = new ChatWebView(mContext, mSessionManagerHandler);
        }
        // else {
        // Logger.d(TAG, "reload-----");
        // mChatWebView.reload(chatUrl, answer, false);
        // }
        mChatWebView.updateView(chatUrl, answer, false);
        addAnswerView(mChatWebView);
    }


    @Override
    public void release() {
        // TODO Auto-generated method stub
        super.release();
        mChatWebView.release();
    }

    @Override
    public void onTTSEnd() {
        Logger.d(TAG, "onTTSEnd");
        super.onTTSEnd();
    }

}
