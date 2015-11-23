/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MultipleLocationSession.java
 * @ProjectName : uniCarSolution
 * @PakageName : com.unisound.unicar.gui.session
 * @Author : Alieen
 * @CreateDate : 2015-07-08
 */
package com.unisound.unicar.gui.session;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.baidu.mapapi.utils.DistanceUtil;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.PickLocationView;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2015-07-08
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2015-07-08
 * @Modified:
 * 2015-07-08: 实现基本功能
 */
public class MultipleLocationSession extends SelectCommonSession {
	public static final String TAG = "MultipleLocationSession";

	private PickLocationView mPickLocationView;
	private ArrayList<LocationInfo> mLocationInfos = new ArrayList<LocationInfo>();
	private String ttsAnswer = "";
	
	public MultipleLocationSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		Logger.d("---- jsonProtocol----",jsonProtocol.toString());
		JSONArray dataArray = JsonTool.getJsonArray(mDataObject, SessionPreference.KEY_LOCATION);
		if (dataArray != null) {
			mAnswer = mContext.getString(R.string.say_number_choose);
			addSessionAnswerText(mAnswer);
			
			mTtsText = ttsAnswer;
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject item = JsonTool.getJSONObject(dataArray, i);
				LocationInfo info = new LocationInfo();
				info.setName(JsonTool.getJsonValue(item, "name"));
				info.setAddress(JsonTool.getJsonValue(item, "address"));
				info.setType(JsonTool.getJsonValue(item, "type", -1));
				info.setCity(JsonTool.getJsonValue(item, "city"));
				info.setProvider(JsonTool.getJsonValue(item, "provider"));
				info.setLatitude(JsonTool.getJsonValue(item, "lat", 0d));
				info.setLongitude(JsonTool.getJsonValue(item, "lng", 0d));
				mLocationInfos.add(info);
				mDataItemProtocalList.add(JsonTool.getJsonValue(item, SessionPreference.KEY_TO_SELECT));
			}
			Logger.d(TAG, "!--->mDataItemProtocalList size = "+mDataItemProtocalList.size());
			if (mPickLocationView == null) {
				mPickLocationView = new PickLocationView(mContext);
				mPickLocationView.initView(mLocationInfos);
				mPickLocationView.setPickListener(mPickViewListener);
			}
			addSessionView(mPickLocationView, false);
		}
	}

	@Override
	public void release() {
		super.release();
		Logger.d(TAG, "!--->release----");
		if (mPickLocationView != null) {
			mPickLocationView.setPickListener(null);
			mPickLocationView = null;
		}
		mLocationInfos.clear();
		mLocationInfos = null;
	}
}
