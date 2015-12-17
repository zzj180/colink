/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : AppExitSession.java
 * @ProjectName : uniCarGUI
 * @PakageName : com.unisound.unicar.gui.session
 * @version : 1.1
 * @Author : Xiaodong.He
 * @CreateDate : 2015-6-9
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.oem.RomControl;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * exit app
 * 
 */
public class AppExitSession extends BaseSession {
    public static final String TAG = "AppExitSession";
    public Context mContext = null;

    public AppExitSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        mContext = context;
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "jsonProtocol = " + jsonProtocol);

        JSONObject resultObject = JsonTool.getJSONObject(jsonProtocol, "data");
        JSONObject appObject = JsonTool.getJSONObject(jsonProtocol, "app");
        String packageName = JsonTool.getJsonValue(appObject, "package_name", "");
        String className = JsonTool.getJsonValue(appObject, "class_name", "");
        if (packageName != null && !"".equals(packageName) && className != null
                && !"".equals(className)) {
            addQuestionViewText(mQuestion);
            addAnswerViewText(mAnswer);
            Logger.d(TAG, "mQuestion:" + mQuestion + "mAnswer:" + mAnswer + "className:"
                    + className + "packageName:" + packageName);
            RomControl.enterControl(mContext, RomControl.ROM_APP_EXIT, packageName, className);

        }

        // XD 20151123 modify
        if (UserPerferenceUtil.VALUE_VERSION_MODE_EXP == UserPerferenceUtil
                .getVersionMode(mContext)) {
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_RELEASE_ONLY);
        } else{
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        }
    }
}
