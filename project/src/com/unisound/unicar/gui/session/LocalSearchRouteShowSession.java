/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : RouteShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
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
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.unisound.unicar.gui.location.operation.LocationModelProxy;
import com.unisound.unicar.gui.oem.RomDevice;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.route.operation.GaodeMap;
import com.unisound.unicar.gui.route.operation.KLDUriApi;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified: 2013-9-6: 实现基本功能
 */
public class LocalSearchRouteShowSession extends BaseSession {
	public static final String TAG = "LocalSearchRouteShowSession";
	
	private Context mContext;
	
	private final int AMAP_INDEX = 1;
	private final int BAIDU_INDEX = 2;
	private final int KLD_INDEX = 3;
	private final int MAPBAR_INDEX = 4;
	private final int RITU_INDEX = 5;
	
	private double toLat = 0.0;
	private double toLng = 0.0;
	private double fromLat = 0.0;
	private double fromLng = 0.0;
	private String fromPoi = "";
	private String toCity = "";
	private String toPoi = "";

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-6
	 * @param context
	 * @param sessionManagerHandler
	 */
	public LocalSearchRouteShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		this.mContext = context;
	}

	@SuppressWarnings("deprecation")
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		addQuestionViewText(mQuestion);

		//mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		Log.d(TAG, "jsonProtocol = " + jsonProtocol.toString());
		try {
			JSONObject data = jsonProtocol.getJSONObject("data");
			JSONObject poiinfo = new JSONObject(data.getString("location"));
			toLat = poiinfo.getDouble("lat");
			toLng = poiinfo.getDouble("lng");
			toCity = poiinfo.getString("city");
			toPoi = poiinfo.getString("name");
			fromPoi = "出发地";
//			showRoute();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		addAnswerViewText(mAnswer);

	}
	
	private void showRoute(){
		int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
		switch (mapIndex) {
		case AMAP_INDEX:
			Logger.d(TAG, "mapIndex : " + mapIndex + " use amap route ...");
			GaodeMap.showRoute(mContext,GaodeMap.ROUTE_MODE_DRIVING, toLat, toLng,toCity, toPoi,0);
			break;
		case BAIDU_INDEX:
			Logger.d(TAG, "mapIndex : " + mapIndex + " use baidu route ...");
			startNavi(toLat, toLng, fromPoi, toPoi);
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
			int lat = (int) toLat;
			int lng = (int) toLng;
			Logger.d(TAG, "navi_keyword_name ：【" + lng + "," + lat + "," + toPoi + "】");
			intent.putExtra("navi_keyword_name", lng + "," + lat + "," + toPoi);
			mContext.sendBroadcast(intent);
			break;
		default:
			break;
		}
	}

	public void startNavi(double toLat, double toLng, String fromPoi,
			String toPoi) {
		//先将高德坐标转换为百度坐标
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
		Logger.d(TAG, "from lng : [ " + fromLat + " -- " + fromLng + " ]");
		Logger.d(TAG, "to lng : [ " + toLat + " -- " + toLng + " ]");
		Logger.d(TAG, "from poi : " + fromPoi + " to poi : " + toPoi);
		Logger.d(TAG, "to pt2 : " +  pt2.latitude + " to lng : " + pt2.longitude);
		
		// 构建 导航参数
/*		NaviPara para = new NaviPara();
		para.startPoint = pt1;
		para.startName = fromPoi;
		para.endPoint = pt2;
		para.endName = toPoi;*/
		try {
		//	BaiduMapNavigation.openBaiduMapNavi(para, mContext);
			RomDevice.showRoute(mContext, GaodeMap.ROUTE_MODE_DRIVING, fromLat, fromLng, "", fromPoi, to.latitude, to.longitude, "", toPoi);
		} catch (BaiduMapAppNotSupportNaviException e) {
			e.printStackTrace();
		}
	}
	
	private void convertAmap2Baidu(){
		fromLat = LocationModelProxy.getInstance(mContext).getLastLocation().getLatitude();
		fromLng = LocationModelProxy.getInstance(mContext).getLastLocation().getLongitude();
		
		CoordinateConverter converter  = new CoordinateConverter();  
		converter.from(CoordType.COMMON);  
		// sourceLatLng待转换坐标  
		Logger.d(TAG, "Before convert : lat : " + fromLat + " lng : " + fromLng);
		LatLng tempLng = new LatLng(fromLat, fromLng);
		converter.coord(tempLng);  
		LatLng desLatLng = converter.convert();
		fromLat = desLatLng.latitude;
		fromLng = desLatLng.longitude;
		Logger.d(TAG, "After convert : lat : " + fromLat + " lng : " + fromLng);
	}

	@Override
	public void onTTSEnd() {
		super.onTTSEnd();
		Logger.d(TAG, "!---LocalSearchRouteConfireShowSession------------>onTTSEnd");
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		showRoute();
	}
}
