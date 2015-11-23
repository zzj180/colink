package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.oem.RomControl;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

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
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }
}
