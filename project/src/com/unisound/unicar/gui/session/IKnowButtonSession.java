package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.IKnowButtonContentView;
import com.unisound.unicar.gui.view.IKnowButtonContentView.IIKonwButtonContentViewListener;

/**
 * eg: “打电话”-->“打电话给谁？”-->"zhang san" --> No person --> I Know
 * eg: “打电话”-->“打电话给谁？”-->"zhang san" --> One person --> No Number --> I Know
 * 没有找到相关的联系人.
 * 我知道了  --> 点击退出
 * TTS播报完毕 退出
 * @author xiaodong
 * @date 20150626
 */
public class IKnowButtonSession extends CommBaseSession{
	public static final String TAG = IKnowButtonSession.class.getSimpleName();

	private IKnowButtonContentView mIKnowView = null;
	private IIKonwButtonContentViewListener mIKonwButtonListener = new IIKonwButtonContentViewListener(){
		@Override
		public void onOk() {
			Logger.d(TAG, "!--->----onOk()---");
			mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		}
	};
	
	IKnowButtonSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		Logger.d(TAG, "!--->IKnowButtonSession()-------");
	}
	
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		Logger.d(TAG, "!--->putProtocol()---jsonProtocol = "+jsonProtocol);
		Logger.d(TAG, "!--->mOriginType = "+ mOriginType + ";mType = " + mType);
		if(mIKnowView == null){
			mIKnowView = new IKnowButtonContentView(mContext);
			if(SessionPreference.VALUE_SCHEDULE_TYPE_NO_PERSON.equals(mType)){ 
				//can't find contacts
				mIKnowView.setShowText(R.string.call_find_no_person);
			} else if (SessionPreference.VALUE_SCHEDULE_TYPE_NO_NUMBER.equals(mType)){
				//find contacts but no number
				mIKnowView.setShowText(R.string.call_find_contact_no_number);
			} else if (SessionPreference.VALUE_SCHEDULE_TYPE_ONLINE_ERROR.equals(mType)) {
				//no network connect
				mIKnowView.setShowText(R.string.error_no_network_connect);
			} else if (SessionPreference.VALUE_SCHEDULE_TYPE_UNKNOWN_ERROR.equals(mType)) {
				mIKnowView.setShowText(R.string.error_unknown);
			} else if (SessionPreference.VALUE_SCHEDULE_TYPE_NO_APP.equals(mType)) {
				//can't find app
				mIKnowView.setShowText(R.string.appmgr_find_no_app);
			} else if(SessionPreference.VALUE_SCHEDULE_TYPE_LOCATION_NO_RESULT.equals(mType)){
				//no route find
				mIKnowView.setShowText(R.string.no_route_found);
			} else if(SessionPreference.VALUE_SCHEDULE_TYPE_LOCATION_RESULT_ERROR.equals(mType)){
				mIKnowView.setShowText(R.string.local_data_is_no_amap);
			} else if(SessionPreference.VALUE_SCHEDULE_TYPE_WEATHER_ERROR_SHOW.equals(mType)) {
				mIKnowView.setShowText(R.string.i_know_weather_error);

			} else if(SessionPreference.VALUE_TYPE_FM_NO_SHOW.equals(mType)){
				mIKnowView.setShowText(R.string.brodcast_data_is_no_amap);
			} else if(SessionPreference.VALUE_SCHEDULE_TYPE_STOCK_NO_RESULT.equals(mType)){
				mIKnowView.setShowText(R.string.stock_query_empty);
			} 
			mIKnowView.setListener(mIKonwButtonListener);
		}
		addSessionView(mIKnowView);
	}
	
	@Override
	public void onTTSEnd() {
		super.onTTSEnd();
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
