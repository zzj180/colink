package com.unisound.unicar.gui.session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.GuiProtocolUtil;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : Session层基础类
 * @Comments : 用于构建整个Session层的生命周期和提供核心方法
 * @Author : Dancindream
 * @CreateDate : 2014-4-1
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2014-4-1
 * @Modified: 2014-4-1: 实现基本功能
 */
public abstract class BaseSession {
    private static final String TAG = "BaseSession";
    protected static final String[] MESSAGE_TRIM_END = new String[] {"。", "，"};
    public boolean mIsNeedAddTextView;
    protected Handler mSessionManagerHandler = null;
    protected Context mContext = null;

    protected String mDomain = "";
    protected String mType = "", mOriginType = "";
    protected String mQuestion = "", mAnswer = "";
    protected String mOriginCode;
    protected String mErrorCode;
    protected JSONObject mDataObject = null;

    private boolean mReleased = false;

    public BaseSession(Context context, Handler sessionManagerHandler) {
        Logger.d(TAG, "!--->BaseSession()------");
        mContext = context;
        mSessionManagerHandler = sessionManagerHandler;
    }

    /**
     * @Description : TODO 主流程，将协议导入Session
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     * @param jsonProtocolArray
     */
    public void putProtocol(JSONArray jsonProtocolArray) {
        Logger.d(TAG, "!--->putProtocol()------");
        try {
            if (jsonProtocolArray != null && jsonProtocolArray.length() > 0) {
                putProtocol(jsonProtocolArray.getJSONArray(0));
            }
        } catch (JSONException e) {
            e.printStackTrace();// added xd
            showUnSupport();
            return;
        }
    }

    /**
     * @Description : TODO 主流程，将协议导入Session
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     * @param jsonProtocol
     */
    public void putProtocol(JSONObject jsonProtocol) {
        // {"origin_code":"CALL","domain":"SESSION_SHOW","data":{"schedule_type":"CONFIRM_CALL","number":"11111","text":"第一个","name":"张三"},"tts":"正在为您准备电话","type":"CONFIRM_CALL","origin_type":"cn.yunzhisheng.call"}
        Logger.d(TAG, "!--->--putProtocol:" + jsonProtocol);

        mDomain = JsonTool.getJsonValue(jsonProtocol, SessionPreference.KEY_DOMAIN, "");
        Logger.d(TAG, "!--->putProtocol()---mDomain = " + mDomain);
        /* xd 20150619 modify Begin */
        // mType = JsonTool.getJsonValue(jsonProtocol, SessionPreference.KEY_TYPE, "");
        /* xd 20150619 modify End */
        mOriginType = JsonTool.getJsonValue(jsonProtocol, SessionPreference.KEY_ORIGIN_TYPE);
        Logger.d(TAG, "!--->putProtocol()---mOriginType = " + mOriginType);

        mOriginCode = JsonTool.getJsonValue(jsonProtocol, SessionPreference.KEY_ORIGIN_CODE);
        Logger.d(TAG, "!--->putProtocol()---mOriginCode = " + mOriginCode);

        mDataObject = JsonTool.getJSONObject(jsonProtocol, SessionPreference.KEY_DATA);
        // {"schedule_type":"CONFIRM_CALL","number":"11111","text":"第一个","name":"张三"}
        Logger.d(TAG, "!--->putProtocol()---mDataObject = " + mDataObject);

        if (mDataObject != null) {

            mType =
                    JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_DATA_SCHEDULE_TYPE, "");// XD
                                                                                                     // added
            Logger.d(TAG, "!--->mType = " + mType);

            mQuestion = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_QUESTION, "");
            Logger.d(TAG, "!--->putProtocol()---mQuestion = " + mQuestion);

            mErrorCode = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_ERROR_CODE);
            Logger.d(TAG, "!--->putProtocol()---mErrorCode = " + mErrorCode);

            if (TextUtils.isEmpty(mQuestion)) {
                mQuestion = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_TEXT);
                Logger.d(TAG, "!--->putProtocol()---mQuestion = " + mQuestion);
            }
            mAnswer = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_ANSWER, "");
            Logger.d(TAG, "!--->putProtocol()---mAnswer = " + mAnswer);
        }
    }

    /**
     * @Description : TODO 主流程，获取ActivityResult的数据时触发
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    /**
     * @Description : TODO 主流程，TTS结束时触发
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     */
    public void onTTSEnd() {
        Logger.d(TAG, "onTTSEnd");
    }

    /**
     * @Description : TODO 主方法，在不支持时的统一处理方案
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     */
    protected void showUnSupport() {}

    /**
     * @Description : TODO 主流程，在取消操作时触发
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     */
    public void cancelSession() {
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_CANCEL_TALK);
        addAnswerViewText(mContext.getString(R.string.operation_cancel));
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_CANCEL);
    }

    /**
     * @Description : TODO 主方法，判断是否已经release
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     * @return
     */
    public boolean isReleased() {
        return mReleased;
    }

    /**
     * @Description : TODO 主方法，获取Session的Domain字段
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     * @return
     */
    public String getSessionDomain() {
        return mDomain;
    }

    /**
     * @Description : TODO 主方法，release Session
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     */
    public void release() {
        Logger.d(TAG, "!--->release");
        mReleased = true;
        mContext = null;
    }

    /**
     * @Description : TODO 主方法，将问句（用户说的内容）添加到主界面容器
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     * @param text
     */
    // 已废弃 zhuoran
    @Deprecated
    protected void addQuestionViewText(String text) {
        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_ADD_QUESTION_TEXT;
        msg.obj = text;
        mSessionManagerHandler.sendMessage(msg);
    }

    /**
     * @Description : TODO 主方法，将View（语音魔方要显示的内容）添加到主界面容器
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     * @param view
     */
    protected void addAnswerView(View view) {
        addAnswerView(view, true);
    }

    protected void addAnswerView(View view, boolean fullScroll) {
        Logger.d(TAG, "!--->addAnswerView----view=" + view);
        if (view == null) {
            return;
        }
        // SessionContainer.addViewNow(view.hashCode());
        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_ADD_ANSWER_VIEW;
        msg.obj = view;
        msg.arg1 = fullScroll ? 1 : 0;
        mSessionManagerHandler.sendMessage(msg);
    }

    /**
     * @Description : TODO 主方法，将答句（语音魔方说的内容）添加到主界面容器
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     * @param text
     */
    // 已废弃 zhuoran
    @Deprecated
    protected void addAnswerViewText(String text) {
        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_ADD_ANSWER_TEXT;
        msg.obj = text;
        mSessionManagerHandler.sendMessage(msg);
    }

    /**
     * xd added 20150716
     * 
     * @param view : which contains ListLiew
     */
    protected void addSessionViewContainsListView(View view) {
        Logger.d(TAG, "!--->addSessionViewContainsListView----view=" + view);
        if (view == null) {
            return;
        }

        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_ADD_SESSION_LIST_VIEW;
        msg.obj = view;
        mSessionManagerHandler.sendMessage(msg);
    }

    /**
     * @Description : TODO 主方法，当界面操作后触发，需将触发协议返还语音魔方
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     * @param protocal
     */
    // protected void onUiProtocal(String protocal) {
    // Logger.d(TAG, "onUiProtocal : " + protocal);
    // Message msg = new Message();
    // msg.what = SessionPreference.MESSAGE_UI_OPERATE_PROTOCAL;
    // msg.obj = protocal;
    // mSessionManagerHandler.sendMessage(msg);
    // }


    /**
     * @Description : TODO 主方法，当界面操作后触发，需将触发协议返还语音魔方
     * @Author : xiaodong
     * @CreateDate : 20150702
     * @param eventName
     * @param protocal
     */
    protected void onUiProtocal(String eventName, String protocal) {
        Logger.d(TAG, "onUiProtocal : " + protocal);
        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_UI_OPERATE_PROTOCAL;

        Bundle bundle = new Bundle();
        bundle.putString(SessionPreference.KEY_BUNDLE_EVENT_NAME, eventName);
        bundle.putString(SessionPreference.KEY_BUNDLE_PROTOCAL, protocal);
        msg.obj = bundle;

        mSessionManagerHandler.sendMessage(msg);
    }

    /**
     * send Recording Control Event
     * 
     * @author xiaodong.he
     * @date 2015-10-26
     * @param isStart
     * @param domain
     */
    protected void sendRecordingControlEvent(boolean isStart, String domain) {
        Logger.d(TAG, "sendRecordingControlEvent--isStart = " + isStart + "; domain = " + domain);
        String param =
                isStart
                        ? SessionPreference.PARAM_RECORDING_CONTROL_START
                        : SessionPreference.PARAM_RECORDING_CONTROL_STOP;
        onUiProtocal(SessionPreference.EVENT_NAME_RECORDING_CONTROL,
                GuiProtocolUtil.getRecordingControlParamProtocol(param, domain));
    }

    protected void onRespParams(String respParams) {
        Logger.d(TAG, "onRespParams : " + respParams);
        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_SEND_RESP_PROTOCAL;

        Bundle bundle = new Bundle();
        bundle.putString(SessionPreference.KEY_PARAMS, respParams);
        msg.obj = bundle;
        mSessionManagerHandler.sendMessage(msg);

    }

    protected void onRespParams(String respParams, String state) {
        Logger.d(TAG, "onRespParams : " + respParams);
        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_SEND_RESP_PROTOCAL;

        Bundle bundle = new Bundle();
        bundle.putString(SessionPreference.KEY_PARAMS, respParams);
        bundle.putString(SessionPreference.PARAM_KEY_STATE_INCOMING, state);
        msg.obj = bundle;
        mSessionManagerHandler.sendMessage(msg);

    }

    /**
     * delay send route and localsearch result message add tyz 20150729
     * 
     * @param str
     */
    protected void onRespParamsDelay(String str) {
        Logger.d(TAG, "onRespParams : " + str);
        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_SEND_RESP_PROTOCAL;

        Bundle bundle = new Bundle();
        bundle.putString(SessionPreference.KEY_PARAMS, str);
        msg.obj = bundle;
        mSessionManagerHandler.sendMessageDelayed(msg, 0);
    }

    /**
     * onNewSessionStart before session end
     * 
     * @author xiaodong.he
     * @date 20151019
     */
    public void onNewSessionStart() {
        Logger.d(TAG, "onNewSessionStart");
        // TODO Auto-generated method stub
    }

    /**
     * update Session Mic Enable Status
     * 
     * @author xiaodong.he
     * @date 20151102
     * @param isEnable
     */
    protected void updateMicEnableStatus(boolean isEnable) {
        Logger.d(TAG, "updateMicStatus--isEnable = " + isEnable);
        if (null == mSessionManagerHandler) {
            return;
        }
        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_UPDATE_MIC_EANBLE_STATE;
        msg.obj = isEnable;
        mSessionManagerHandler.sendMessageDelayed(msg, 300);
    }
}
