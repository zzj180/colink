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

import com.unisound.unicar.gui.utils.Logger;

import com.coogo.inet.vui.assistant.car.R;
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
public class UnsupportEndSession extends BaseSession {
    public static final String TAG = UnsupportEndSession.class.getSimpleName();

    /**
     * @Author : Dancindream
     * @CreateDate : 2013-9-2
     * @param context
     * @param sessionManagerHandler
     * @param sessionViewContainer
     */
    public UnsupportEndSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        addAnswerViewText(mContext.getString(R.string.operation_cancel));
    }

    @Override
    public void onTTSEnd() {
        Logger.d(TAG, "!--->onTTSEnd");
        super.onTTSEnd();
        // mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_CANCEL);
    }

    @Override
    public void release() {
        super.release();
    }
}
