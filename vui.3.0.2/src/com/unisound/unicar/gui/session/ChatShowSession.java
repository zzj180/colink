package com.unisound.unicar.gui.session;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.unisound.unicar.gui.chat.ChatDataBaseUtil;
import com.unisound.unicar.gui.chat.ChatObject;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.ChatView;

/**
 * Chat Show Session
 * 
 * @author
 * @modify XD
 * @date 20150820
 */
public class ChatShowSession extends BaseSession {

    private static final String TAG = ChatShowSession.class.getSimpleName();

    private Context mContext;
    private ChatView mChatView = null;
    private static ChatShowSession mChatSession;

    /** all ChatObject List */
    private List<ChatObject> mAllChatObjList = new ArrayList<ChatObject>();

    /** new ChatObject List */
    private List<ChatObject> mNewChatObjList = new ArrayList<ChatObject>();

    public static ChatShowSession getInstance(Context context, Handler sessionManagerHandler) {
        if (mChatSession == null) {
            mChatSession = new ChatShowSession(context, sessionManagerHandler);
        }
        return mChatSession;
    }

    private ChatShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        mContext = context;
        mSessionManagerHandler = sessionManagerHandler;
        getExistChatData();
    }

    @Override
    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "ChatUiSession putProtocol jsonProtocol=" + jsonProtocol);
        updateChatObjectList(jsonProtocol);

        if (mChatView == null) {
            mChatView = new ChatView(mContext, mAllChatObjList);
        } else {
            mChatView.notifyDataChanged();
        }

        addAnswerView(mChatView);
    }

    /**
     * get exist chat data from DB
     */
    private void getExistChatData() {
        mAllChatObjList.clear();
        mNewChatObjList.clear();
        mAllChatObjList = ChatDataBaseUtil.getExistChatDataFromDB(mContext);
    }

    /**
     * update ChatObject List from protocol
     * 
     * @param objc
     */
    private void updateChatObjectList(JSONObject objc) {
        JSONObject obj = JsonTool.getJSONObject(objc, SessionPreference.KEY_DATA);
        String text = JsonTool.getJsonValue(obj, SessionPreference.KEY_TEXT);
        String answer = JsonTool.getJsonValue(obj, SessionPreference.KEY_ANSWER);
        Logger.d(TAG, "!--->updateChatObjectList----text =" + text + "  answer =" + answer);
        if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(answer)) {
            mNewChatObjList.add(new ChatObject(text, answer));
            mAllChatObjList.add(new ChatObject(text, answer));
        }

    }

    @Override
    public void release() {
        Logger.d(TAG, "!--->release-----");
        ChatDataBaseUtil.saveNewChatDataIntoDB(mContext, mNewChatObjList);
        mAllChatObjList.clear();
        mNewChatObjList.clear();
        super.release();
        mChatSession = null;
    }

    @Override
    public void onTTSEnd() {
        // TODO Auto-generated method stub
        super.onTTSEnd();

    }

}
