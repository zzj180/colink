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
import android.os.Handler;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.baidu.mapapi.utils.DistanceUtil;
import com.unisound.unicar.gui.location.operation.LocationModelProxy;
import com.unisound.unicar.gui.oem.RomDevice;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.route.operation.GaodeMap;
import com.unisound.unicar.gui.route.operation.GaodeUriApi;
import com.unisound.unicar.gui.route.operation.KLDUriApi;
import com.unisound.unicar.gui.utils.Gps;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.PositionUtil;
import com.unisound.unicar.gui.view.LocalSearchRouteConfirmView;
import com.unisound.unicar.gui.view.LocalSearchRouteConfirmView.IRouteWaitingContentViewListener;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2015-07-2
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2015-07-2
 * @Modified: 2015-07-2: 实现基本功能
 */
public class LocalSearchRouteConfirmShowSession extends BaseSession {
	public static final String TAG = "LocalSearchRouteConfirmShowSession";
	
	private Context mContext;
	
	private LocalSearchRouteConfirmView mRouteContentView = null;
	public static boolean isGaoDe;
	double toLat = 0.0;
	double toLng = 0.0;
	String toCity = "";
	String toPoi = "";
	
	private final int AMAP_INDEX = 1;
	private final int BAIDU_INDEX = 2;
	private final int KLD_INDEX = 3;
	private final int MAPBAR_INDEX = 4;
	private final int RITU_INDEX = 5;
	private static double mFromLat = 0.0;
	private static double mFromLng = 0.0;
	private static String mFromPoi = "";

	public LocalSearchRouteConfirmShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		this.mContext = context;
	}

	@SuppressWarnings("deprecation")
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		addQuestionViewText(mQuestion);
		Log.d(TAG, "putProtocol : " + jsonProtocol.toString());
		try {
			JSONObject data = jsonProtocol.getJSONObject(SessionPreference.KEY_DATA);
			toPoi = data.getString(SessionPreference.KEY_NAME);
			JSONObject poiinfo = new JSONObject(data.getString("location"));
			toLat = poiinfo.getDouble("lat");
			toLng = poiinfo.getDouble("lng");
			toCity = poiinfo.getString("city");
			toPoi = poiinfo.getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		addAnswerViewText(mAnswer);
		mRouteContentView = new LocalSearchRouteConfirmView(mContext);
		mRouteContentView.setListener(mRouteViewListener);
		mRouteContentView.setEndPOI(toPoi);
		addAnswerView(mRouteContentView);
	}
	
	
	public static void setFromLat(double fromLat){
		Logger.d(TAG, "RouteShowSession set fromLat : " + fromLat);
		mFromLat = fromLat;
	}
	public static void setFromLng(double fromLng){
		Logger.d(TAG, "RouteShowSession set fromLng : " + fromLng);
		mFromLng = fromLng;
	}
	public static void setFromPoi(String fromPoi){
		Logger.d(TAG, "RouteShowSession set fromPoi : " + fromPoi);
		mFromPoi = "出发地";
	}
	
	protected IRouteWaitingContentViewListener mRouteViewListener = new IRouteWaitingContentViewListener() {
		
		@Override
		public void onCancel() {
			Logger.d(TAG, "!--->mCallContentViewListener---onCancel()-----");
			onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_CANCEL, SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_CANCEL_CALL); 
			mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		}

		@Override
		public void onOk() {
			Logger.d(TAG, "!--->mCallContentViewListener---onOk()-----");
            onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_OK, SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_OK);
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
            showRoute();
		}

		@Override
		public void onTimeUp() {
			
			Logger.d(TAG, "!--->mCallContentViewListener---onTimeUp()-----");
			onUiProtocal(SessionPreference.EVENT_NAME_ON_CONFIRM_TIME_UP, SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_TIME_UP);
//            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
            showRoute();
		}
	};
	
	private void showRoute(){
		int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
		switch (mapIndex) {
		case AMAP_INDEX:
			if(isGaoDe){
				GaodeMap.showRoute(mContext, "driving", toLat, toLng,toCity, toPoi,0);
			}else{
				Gps gcj02 = PositionUtil.bd09_To_Gcj02(toLat, toLng);
				GaodeMap.showRoute(mContext, "driving",gcj02.getWgLat(),gcj02.getWgLon(),toCity, toPoi,0);
			}
			break;
		case BAIDU_INDEX:
			Logger.d(TAG, "mapIndex : " + mapIndex + " use baidu route ...");
			startNavi(mFromLat, mFromLng, toLat, toLng, mFromPoi, toPoi);
			break;
		case KLD_INDEX:
			KLDUriApi.startNavi(mContext, toLat, toLng, toPoi);
			break;
		case MAPBAR_INDEX:
			Logger.d(TAG, "mapIndex : " + mapIndex + " use mapbar route ...");
			JSONObject mJson = new JSONObject();
			try {
				mJson.put("toLatitude", toLat);
				mJson.put("toLongtitude", toLng);
				mJson.put("toPoi", toPoi);
				Log.d(TAG, "msg = "+mJson.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			Intent mIntent = new Intent();
			String action = "android.intent.action.SEND_POIINFO";
			mIntent.setAction(action);
			mIntent.putExtra("poi_info", mJson.toString());
			Log.d(TAG, "msg = "+action.toString());
			mContext.sendBroadcast(mIntent);
			break;
		case RITU_INDEX:
			Logger.d(TAG, "mapIndex : " + mapIndex + " use ritu route ...");
			Intent intent = new Intent("android.intent.action.ritu.keyword.name");
			
			double mlng = toLng;
			double mlat = toLat;
			
			if (toLng<1000) {
				mlng = mlng*3600*2560;
				mlat = mlat*3600*2560;
			}
			int lng = (int) mlng;
			int lat = (int) mlat;
			
			Log.d(TAG, "mapIndex : " + mapIndex + " use ritu route ...====lng:"+lng+"======lat:"+lat);
			intent.putExtra("navi_keyword_name", lng + "," + lat + "," + toPoi);
			mContext.sendBroadcast(intent);
			break;

		default:
			break;
		}
	}

	
	public void startNavi(double fromLat, double fromLng, double toLat, double toLng, String fromPoi,
			String toPoi) {
		if(isGaoDe){
			fromLat = LocationModelProxy.getInstance(mContext).getLastLocation().getLatitude();
			fromLng = LocationModelProxy.getInstance(mContext).getLastLocation().getLongitude();
			fromPoi = LocationModelProxy.getInstance(mContext).getLastLocation().getAddress();
			CoordinateConverter converter  = new CoordinateConverter();  
			converter.from(CoordType.COMMON);  
			// sourceLatLng待转换坐标  
			//先将高德坐标转换为百度坐标
			LatLng pt1 = new LatLng(fromLat, fromLng);
			LatLng pt2 = new LatLng(toLat, toLng);
			converter.coord(pt1);  
			LatLng desLatLng = converter.convert();
			fromLat = desLatLng.latitude;
			fromLng = desLatLng.longitude;
			CoordinateConverter toConverter  = new CoordinateConverter();  
			toConverter.from(CoordType.COMMON);
			toConverter.coord(pt2);
			LatLng to=toConverter.convert();
			toLat = to.latitude;
			toLng = to.longitude;
			Logger.d(TAG, "from lng : [ " + fromLat + " -- " + fromLng + " ]");
			Logger.d(TAG, "to lng : [ " + toLat + " -- " + toLng + " ]");
			Logger.d(TAG, "from poi : " + fromPoi + " to poi : " + toPoi);
			Logger.d(TAG, "to pt2 : " +  pt2.latitude + " to lng : " + pt2.longitude);
		}
			
		// 构建 导航参数
/*		NaviPara para = new NaviPara();
		para.startPoint = pt1;
		para.startName = fromPoi;
		para.endPoint = pt2;
		para.endName = toPoi;*/
		try {
		//	BaiduMapNavigation.openBaiduMapNavi(para, mContext);
			RomDevice.showRoute(mContext, GaodeMap.ROUTE_MODE_DRIVING, fromLat, fromLng, "", fromPoi, toLat, toLng, "", toPoi);
		} catch (BaiduMapAppNotSupportNaviException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onTTSEnd() {
		super.onTTSEnd();
		Logger.d(TAG, "!--->mCallContentViewListener---onTTSEnd()-----");
		//mRouteContentView.startCountDownTimer(GUIConfig.TIME_DELAY_AUTO_CONFIRM);
		showRoute();
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
	
	@Override
	public void release() {
		Logger.d(TAG, "!--->release");
		super.release();
		if (mRouteContentView != null) {
			mRouteContentView.cancelCountDownTimer();
		}
		mRouteContentView.setListener(null);
	}
}
