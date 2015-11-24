package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import com.unisound.unicar.gui.location.operation.LocationModelProxy;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.ui.PositionActivity;
import com.unisound.unicar.gui.utils.Logger;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class PositionShowSession extends BaseSession {
	private static final String TAG = "PositionShowSession";

	public PositionShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		Logger.d(TAG, "PositionShowSession create");
	}
	
	@Override
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		Logger.d(TAG, "putProtocol : [" + jsonProtocol.toString() + "]");
		
		
		Intent intent = new Intent();
		intent.setClass(mContext, PositionActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
//		mPositionContentView = new PositionContentView(mContext);
//		mPositionContentView.updateMapView(mLocationInfo);
	}
	
	@SuppressWarnings("unused")
	private LocationInfo getLocationInfo(Context mContext){
		LocationInfo mLocationInfo = LocationModelProxy.getInstance(mContext).getLastLocation();
		if(mLocationInfo != null){
			return mLocationInfo;
		}else{
			return null;
		}
	}
	
	@Override
	public void onTTSEnd() {
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		super.onTTSEnd();
	}
	
	@Override
	public void release() {
//		mPositionContentView.release();
		super.release();
	}
}
