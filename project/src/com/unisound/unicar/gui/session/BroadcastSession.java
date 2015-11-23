/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : BroadcastSession.java
 * @ProjectName : CarPlay
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Brant
 * @CreateDate : 2014-9-18
 */
package com.unisound.unicar.gui.session;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.view.FmContentView;
import com.unisound.unicar.gui.view.FmContentView.IRouteWaitingContentViewListener;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-9-18
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-9-18
 * @Modified: 2014-9-18: 实现基本功能
 */
public class BroadcastSession extends BaseSession {
	private Context mContext;
	String freq;
	public BroadcastSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		this.mContext = context;
	}

	@Override
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		String type = null;
		freq = null;
		try {
			JSONObject data = jsonProtocol.getJSONObject("data");
			JSONArray channelList = data.getJSONArray("channelList");

			if (channelList != null && channelList.length() > 0) {
				JSONObject fredata = (JSONObject) channelList.get(0);

				JSONArray frequencyList = fredata.getJSONArray("frequencyList");
				if (frequencyList != null && frequencyList.length() > 0) {
					JSONObject datafre = (JSONObject) frequencyList.get(0);
					type = datafre.getString("type");
					freq = datafre.getString("frequency");
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		IRouteWaitingContentViewListener listener = new IRouteWaitingContentViewListener() {

			@Override
			public void onCancel() {
				mSessionManagerHandler
						.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_CANCEL);

			}
		};

		if (type.equals("FM")) {
			FmContentView view = new FmContentView(mContext, "调频："+freq);
			view.setLisener(listener);
			addAnswerView(view);
		} else {
			FmContentView view = new FmContentView(mContext, "调幅："+ freq);
			view.setLisener(listener);
			addAnswerView(view);
		}

	}
	

	@Override
	public void onTTSEnd() {
		super.onTTSEnd();
		try {
			Intent intent=new Intent("action.colink.startFM");
			intent.putExtra("fm_fq",  Float.parseFloat(freq));
			mContext.sendBroadcast(intent);
		} catch (Exception e) {
		}finally{
			mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		}
	}
}
