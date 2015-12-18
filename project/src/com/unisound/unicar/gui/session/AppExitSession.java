package com.unisound.unicar.gui.session;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

public class AppExitSession extends BaseSession {
	public static final String TAG = "AppExitSession";

	public AppExitSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}
	
	@SuppressWarnings("deprecation")
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);

		Logger.d(TAG, "putProtocal : " + jsonProtocol);
		JSONObject resultObject = JsonTool.getJSONObject(mDataObject, "app");

		String packageName = JsonTool.getJsonValue(resultObject, "package_name", "");
		String className = JsonTool.getJsonValue(resultObject, "class_name", "");
		if("com.android.camera2".equals(packageName)){
			Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
			mHomeIntent.addCategory(Intent.CATEGORY_HOME);
			mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			mContext.startActivity(mHomeIntent);
		}else if("com.autonavi.minimap".equals(packageName)){
			mContext.sendBroadcast(new Intent("com.amap.stopnavi"));
			final Context context= mContext.getApplicationContext();
			mSessionManagerHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					try {
						SettingSession.forceStopPackage("com.autonavi.minimap", context);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 1000);
		}else if("com.baidu.navi".equals(packageName)){
			mContext.sendBroadcast(new Intent("com.baidu.navi.quitnavi"));
			final Context context= mContext.getApplicationContext();
			mSessionManagerHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						SettingSession.forceStopPackage("com.baidu.navi", context);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 1000);
			
		}else if("com.android.browser".equals(packageName)){
			try {
				SettingSession.forceStopPackage(packageName, mContext);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			mAnswer="不支持"+mQuestion;
		}

		String url = JsonTool.getJsonValue(resultObject, "url", "");
		if (packageName != null && !"".equals(packageName) && className != null && !"".equals(className)) {
			addQuestionViewText(mQuestion);
			addAnswerViewText(mAnswer);

		} else if (url != null && !"".equals(url)) {
			addAnswerViewText(mAnswer);
		}

		//打开应用，发送广播
	//	RomApp.closeApp(mContext, packageName);
	}

	@Override
	public void onTTSEnd() {
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		super.onTTSEnd();
	}
	
	
	
}
