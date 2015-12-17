/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : AppLaunchSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.oem.RomControl;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified: 2013-9-6: 实现基本功能
 */
public class AppLaunchSession extends BaseSession {
    public static final String TAG = "AppLaunchSession";

    /**
     * @Author : Dancindream
     * @CreateDate : 2013-9-6
     * @param context
     * @param sessionManagerHandler
     */

    public AppLaunchSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);

        JSONObject resultObject = JsonTool.getJSONObject(mDataObject, "app");

        String packageName = JsonTool.getJsonValue(resultObject, "package_name", "");
        String className = JsonTool.getJsonValue(resultObject, "class_name", "");

        String url = JsonTool.getJsonValue(resultObject, "url", "");

        if (packageName != null && !"".equals(packageName) && className != null
                && !"".equals(className)) {
            addQuestionViewText(mQuestion);
            addAnswerViewText(mAnswer);
            Logger.d(TAG, "--AppLaunchSession mAnswer : " + mAnswer + "--");

            RomControl.enterControl(mContext, RomControl.ROM_APP_LAUNCH, packageName, className);
        } else if (url != null && !"".equals(url)) {

            addAnswerViewText(mAnswer);
            RomControl.enterControl(mContext, RomControl.ROM_BROWSER_URL, url);
        }

        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }

}
