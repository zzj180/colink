/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : LocalSearchRouteConfirmShowSession.java
 * @ProjectName : uniCarSolution
 * @PakageName : com.unisound.unicar.gui.session
 * @Author : Alieen
 * @CreateDate : 2015-07-21
 */
package com.unisound.unicar.gui.session;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.unisound.unicar.gui.oem.RomDevice;
import com.unisound.unicar.gui.oem.RomSystemSetting;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2015-07-2
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2015-07-2
 * @Modified: 2015-07-2: 实现基本功能
 */
public class LocalSearchCallShowSession extends BaseSession {
	public static final String TAG = "LocalSearchCallShowSession";
	private Context mContext;
	public LocalSearchCallShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		this.mContext = context;
	}

	@SuppressWarnings("deprecation")
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		addQuestionViewText(mQuestion);
		Log.d(TAG, "putProtocol : " + jsonProtocol.toString());
		String phone = "";
		try {
			JSONObject data = jsonProtocol.getJSONObject("data");
			phone = data.getString("phoneNum");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(RomDevice.hasBluePhoneClient(mContext)){
			RomSystemSetting.RomCustomDialNumber(mContext, phone.toString());
		}else{
			Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone.toString()));
			callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(callIntent);
		}
	}
	
	
	
	@Override
	public void onTTSEnd() {
		super.onTTSEnd();
		Logger.d(TAG, "!--->mCallContentViewListener---onTTSEnd()-----");
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
	
	@Override
	public void release() {
		Logger.d(TAG, "!--->release");
		super.release();
	}
}
