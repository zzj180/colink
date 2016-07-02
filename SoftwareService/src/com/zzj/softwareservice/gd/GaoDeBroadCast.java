package com.zzj.softwareservice.gd;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.autonavi.external.model.EDataFactory;
import com.autonavi.external.model.ENaviInfo;
import com.zzj.softwareservice.ZZJApplication;
import com.zzj.softwareservice.database.DBConstant;

public class GaoDeBroadCast extends BroadcastReceiver {

	private static final String TOTAL_REMAIN_DISTANCE2 = "total_remain_distance";
	private static final String TOTAL_REMAIN_TIME = "total_remain_time";
	private static final String ROAD_ICON2 = "road_icon";
	private static final String CURRENT_ROAD_REMAIN_DISTANCE = "current_road_remain_distance";
	private static final String NEXT_ROAD_NAME = "next_road_name";
	private static final String CURRENT_ROAD_NAME = "current_road_name";
	private static final String AR = "ar_";
	private static final String TYPE = "type";
	private final static String NAVI_GAODE = "com.amap.navi";
	private final static String NAVI_GAODE_PW = "com.autonavi.minimap.carmode.send";
	private final static String SEND_NAVI_INFO = "NAVI_INFO";
	private final static String SEND_BUSINESS_ACTION = "send_business_action";
	private final static String SEND_BUSINESS_DATA = "send_business_data";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.e("info", action);
		if (NAVI_GAODE.equals(action)) {
			Bundle extras = intent.getExtras();
			int type = extras.getInt(TYPE);
			switch (type) {
			case 101:
				((ZZJApplication)context.getApplicationContext()).getHander().removeMessages(0);
				((ZZJApplication)context.getApplicationContext()).getHander().sendEmptyMessageDelayed(0, 20000);
				String current_road = extras.getString(CURRENT_ROAD_NAME);
				String next_road = extras.getString(NEXT_ROAD_NAME);
				int next_road_distance = extras.getInt(CURRENT_ROAD_REMAIN_DISTANCE);
				int road_icon = extras.getInt(ROAD_ICON2);
				int total_remain_time = extras.getInt(TOTAL_REMAIN_TIME);
				int total_remain_distance = extras.getInt(TOTAL_REMAIN_DISTANCE2);
				
				ContentValues con = new ContentValues();
				con.put(DBConstant.NaviTable.MANEUVER_IMAGE, AR + road_icon);
				con.put(DBConstant.NaviTable.NEXT_ROAD_DISTANCE, next_road_distance);
				con.put(DBConstant.NaviTable.CURRENT_ROADNAME, current_road);
				con.put(DBConstant.NaviTable.NEXT_ROAD, next_road);
				con.put(DBConstant.NaviTable.TOTAL_REMAIN_DISTANCE, total_remain_distance);
				con.put(DBConstant.NaviTable.TOTAL_REMAIN_TIME, total_remain_time);
				con.put(DBConstant.NaviTable.ISNAVING, 1);
				context.getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, con, null, null);
				break;
			default:
				break;
			}
		} else if (NAVI_GAODE_PW.equals(action)) {
			String businessAct;
			try {
				businessAct = intent.getStringExtra(SEND_BUSINESS_ACTION);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			if (SEND_NAVI_INFO.equals(businessAct)) {
				String sData = intent.getStringExtra(SEND_BUSINESS_DATA);
				if (!TextUtils.isEmpty(sData)) {
					ENaviInfo data;
					try {
						data = (ENaviInfo) EDataFactory.create(sData);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					((ZZJApplication)context.getApplicationContext()).getHander().removeMessages(0);
					((ZZJApplication)context.getApplicationContext()).getHander().sendEmptyMessageDelayed(0, 20000);
					if (data != null) {
						String current_road = data.getCurRoadName();
						String next_road =data.getNextRoadName();
						int total_remain_distance = data.getRouteRemainDis();
						int total_remain_time = data.getRouteRemainTime();
						int road_icon = data.getIcon();
						int next_road_distance = data.getSegRemainDis();
						
						ContentValues con = new ContentValues();
						con.put(DBConstant.NaviTable.MANEUVER_IMAGE, AR + road_icon);
						con.put(DBConstant.NaviTable.NEXT_ROAD_DISTANCE, next_road_distance);
						con.put(DBConstant.NaviTable.CURRENT_ROADNAME, current_road);
						con.put(DBConstant.NaviTable.NEXT_ROAD, next_road);
						con.put(DBConstant.NaviTable.TOTAL_REMAIN_DISTANCE, total_remain_distance);
						con.put(DBConstant.NaviTable.TOTAL_REMAIN_TIME, total_remain_time);
						con.put(DBConstant.NaviTable.ISNAVING, 1);
						context.getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, con, null, null);
						
					}
				}
			}

		}
	}
}
