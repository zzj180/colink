/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : TalkShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.NoPerSonContentView;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified: 2013-9-6: 实现基本功能
 */
public class TalkShowSession extends BaseSession {
    public static final String TAG = "TalkShowSession";

    /**
     * @Author : Dancindream
     * @CreateDate : 2013-9-6
     * @param context
     * @param sessionManagerHandler
     */
    public TalkShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
    }

    @SuppressWarnings("deprecation")
	public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        addQuestionViewText(mQuestion);
        addAnswerViewText(mAnswer);

        if (!TextUtils.isEmpty(mErrorCode)) {
            Logger.e(TAG, "ErrorCode:" + mErrorCode);
        }

        if (SessionPreference.DOMAIN_CALL.equals(mOriginType)
                || SessionPreference.DOMAIN_SMS.equals(mOriginType)
                || SessionPreference.DOMAIN_CONTACT.equals(mOriginType)) {
            if (mDataObject != null) {
                String extra_info = JsonTool.getJsonValue(mDataObject, "extra_info", "");
                String extra_name = JsonTool.getJsonValue(mDataObject, "extra_name", "");
                if ("NO_PERSON".equals(extra_info)) {
                    NoPerSonContentView view = new NoPerSonContentView(mContext);
                    view.setShowTitle(mContext.getString(R.string.nofind_contact) + extra_name);
                    addAnswerView(view, true);
                }
            }
        }
    }

    @Override
    public void onTTSEnd() {
        Logger.d(TAG, "onTTSEnd");
        super.onTTSEnd();
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }
}
