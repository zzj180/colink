/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : WebShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.view.WebContentView;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-3
 * @Modified: 2013-9-3: 实现基本功能
 */
public class WebShowSession extends BaseSession {
    public static final String TAG = "WebShowSession";
    private String mUrl = "";

    private WebContentView webContentView = null;

    /**
     * @Author : Dancindream
     * @CreateDate : 2013-9-3
     * @param context
     * @param sessionManagerHandler
     */
    public WebShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);

        JSONObject resultObject = JsonTool.getJSONObject(mDataObject, "result");

        mUrl = JsonTool.getJsonValue(resultObject, SessionPreference.KEY_URL, "");

        addQuestionViewText(mQuestion);
        Logger.d(TAG, "--WebShowSession mAnswer : " + mAnswer + "--");
        addAnswerViewText(mAnswer);
        webContentView = new WebContentView(mContext);
        webContentView.setUrl(mUrl);
        addAnswerView(webContentView);
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }
}
