/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : MultipleShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-12
 */
package com.unisound.unicar.gui.session;

import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.view.MultiDomainView;
import com.unisound.unicar.gui.view.MultiDomainView.MultiDomainViewListener;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-12
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-12
 * @Modified: 2013-9-12: 实现基本功能
 */
public class MultipleShowSession extends BaseSession {
    public static final String TAG = "MultipleShowSession";
    private HashMap<String, String> mMultiDomainData = new HashMap<String, String>();

    private MultiDomainViewListener mMultiDomainViewListener = new MultiDomainViewListener() {

        @Override
        public void onChoose(String domain) {
            String jsonString = mMultiDomainData.get(domain);
            onUiProtocal(SessionPreference.EVENT_NAME_SELECT_ITEM, jsonString); // XD modify
                                                                                // 20150702
        }
    };

    /**
     * @Author : Dancindream
     * @CreateDate : 2013-9-12
     * @param context
     * @param sessionManagerHandler
     */
    public MultipleShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);

        addQuestionViewText(mQuestion);
        // addAnswerViewText(mAnswer);
        Logger.d(TAG, "--MultipleShowSession mAnswer : " + mAnswer + "--");
        // playTTS(mAnswer);

        JSONArray resultArray = JsonTool.getJsonArray(mDataObject, SessionPreference.KEY_RESULT);

        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject obj = JsonTool.getJSONObject(resultArray, i);
            String domain = JsonTool.getJsonValue(obj, "domain", "");
            String onClick = JsonTool.getJsonValue(obj, "onClick", "");
            mMultiDomainData.put(domain, onClick);
        }

        Set<String> set = mMultiDomainData.keySet();
        MultiDomainView view = new MultiDomainView(mContext);
        view.setMultiDomainData((String[]) set.toArray(new String[set.size()]),
                mMultiDomainViewListener);
        addAnswerView(view);
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }

}
