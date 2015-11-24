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
import android.util.Log;

import com.unisound.unicar.gui.utils.Logger;

import com.unisound.unicar.gui.preference.SessionPreference;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-2
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-2
 * @Modified: 2013-9-2: 实现基本功能
 */
public class UnsupportShowSession extends BaseSession {
    public static final String TAG = "UnsupportShowSession";

    /**
     * @Author : Dancindream
     * @CreateDate : 2013-9-2
     * @param context
     * @param sessionManagerHandler
     * @param sessionViewContainer
     */
    public UnsupportShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        Logger.d(TAG, "!--->----UnsupportShowSession()----------");
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        addAnswerViewText(mAnswer);
    }


    @Override
    public void onTTSEnd() {
        Logger.d(TAG, "onTTSEnd");
        super.onTTSEnd();
    }

    @Override
    public void release() {
        super.release();
    }
}
