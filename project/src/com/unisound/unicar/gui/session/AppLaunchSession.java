/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : AppLaunchSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.unisound.unicar.gui.oem.RomControl;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified:
 * 2013-9-6: 实现基本功能
 */
public class AppLaunchSession extends BaseSession {
	public static final String TAG = "AppLaunchSession";
	private static final String GLSX_AUTONAVI = "com.glsx.autonavi";
	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-6
	 * @param context
	 * @param sessionManagerHandler
	 */

	public AppLaunchSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	@SuppressWarnings("deprecation")
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);

		JSONObject resultObject = JsonTool.getJSONObject(mDataObject, "app");

		String packageName = JsonTool.getJsonValue(resultObject, "package_name", "");
		String className = JsonTool.getJsonValue(resultObject, "class_name", "");

		String url = JsonTool.getJsonValue(resultObject, "url", "");
		
		if (packageName != null && !"".equals(packageName) && className != null && !"".equals(className)) {
			addQuestionViewText(mQuestion);
			addAnswerViewText(mAnswer);
			Log.d(TAG, "--AppLaunchSession mAnswer : " + mAnswer + "--");
			if(GLSX_AUTONAVI.equals(packageName)){
				Intent intent = new Intent(SettingSession.COM_GLSX_BOOTUP_RECEIVE_AUTONAVI); 
				intent.putExtra("autonaviType", 1);  // autonaviType为1：表示直接发起导航请求， 	  // autonaviType为2：只进入导航主页面（不发起请求）;
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			}
			else
				RomControl.enterControl(mContext, RomControl.ROM_APP_LAUNCH, packageName, className);
		} else if (url != null && !"".equals(url)) {

			addAnswerViewText(mAnswer);
			RomControl.enterControl(mContext, RomControl.ROM_BROWSER_URL, url);
		}

		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
