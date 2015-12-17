/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : WaitingSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-2
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.view.WaitingContentView;
import com.unisound.unicar.gui.view.WaitingContentView.IWaitingContentViewListener;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-2
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-2
 * @Modified: 2013-9-2: 实现基本功能
 */
public class WaitingSession extends BaseSession {
    public static final String TAG = "WaitingSession";
    private String mCancelProtocal = "";
    private WaitingContentView mWaitingContentView = null;

    private IWaitingContentViewListener mListener = new IWaitingContentViewListener() {

        @Override
        public void onCancel() {
            onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_CANCEL, mCancelProtocal);
        }
    };

    /**
     * @Author : Dancindream
     * @CreateDate : 2013-9-2
     * @param context
     * @param sessionManagerHandler
     * @param sessionViewContainer
     */
    public WaitingSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);

        mCancelProtocal = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_ON_CANCEL, "");
        mAnswer = JsonTool.getJsonValue(jsonProtocol, "ttsAnswer");
        addQuestionViewText(mQuestion);

        if (mWaitingContentView == null) {
            mWaitingContentView = new WaitingContentView(mContext);
        }
        mWaitingContentView.setTitle(mAnswer);
        mWaitingContentView.setLisener(mListener);

        addAnswerView(mWaitingContentView);
        addAnswerViewText(mAnswer);
        Logger.d(TAG, "--WaitingSession mAnswer : " + mAnswer + "--");
    }

    @Override
    public void release() {
        mWaitingContentView = null;
        super.release();
    }
}
