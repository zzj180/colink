package com.zzj.coogo.screenoff;

import java.util.Calendar;
import java.util.TimeZone;

import com.autonavi.external.model.EDataFactory;
import com.autonavi.external.model.ENaviInfo;
import com.spreada.utils.chinese.ZHConverter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class GaoDeBroadCast extends BroadcastReceiver {

	private static final String GMT_8 = "GMT+8";
	private static final String PACKAGE = "com.zzj.coogo.screenoff";
	private static final String TOTAL_REMAIN_DISTANCE2 = "total_remain_distance";
	private static final String TOTAL_REMAIN_TIME = "total_remain_time";
	private static final String ROAD_ICON2 = "road_icon";
	private static final String CURRENT_ROAD_REMAIN_DISTANCE = "current_road_remain_distance";
	private static final String NEXT_ROAD_NAME = "next_road_name";
	private static final String CURRENT_ROAD_NAME = "current_road_name";
	private static final String DRAWABLE = "drawable";
	private static final String AR = "ar_";
	private static final String TYPE = "type";
	private final static String NAVI_GAODE = "com.amap.navi";
	private final static String NAVI_GAODE_PW = "com.autonavi.minimap.carmode.send";
	private final static String SEND_NAVI_INFO = "NAVI_INFO";
	private final static String SEND_BUSINESS_ACTION = "send_business_action";
	private final static String SEND_BUSINESS_DATA = "send_business_data";
	
	private final static long MILLIS_IN_DAY = 86400000;
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.e("info", action);
		if (NAVI_GAODE.equals(action)) {
			Bundle extras = intent.getExtras();
			MainApplication.gaodeisnavi = true;
			MainApplication.getHander().removeMessages(2);
			MainApplication.getHander().sendEmptyMessageDelayed(2, 20000);
			int type = extras.getInt(TYPE);
			switch (type) {
			case 101:
		//		String current_road = extras.getString(CURRENT_ROAD_NAME);
				String next_road = extras.getString(NEXT_ROAD_NAME);
				int next_road_distance = extras.getInt(CURRENT_ROAD_REMAIN_DISTANCE);
				int road_icon = extras.getInt(ROAD_ICON2);
				int total_remain_time = extras.getInt(TOTAL_REMAIN_TIME);
				int total_remain_distance = extras.getInt(TOTAL_REMAIN_DISTANCE2);
				if (ScrrenoffActivity.screen != null) {
					if(ScrrenoffActivity.screen.isTW){
			//			current_road = ZHConverter.convert(current_road, ZHConverter.TRADITIONAL);
						next_road = ZHConverter.convert(next_road, ZHConverter.TRADITIONAL);
					}
			//		ScrrenoffActivity.screen.currentRoad.setText(current_road);
					ScrrenoffActivity.screen.nextRoad.setText(next_road);
						int resID = context.getResources().getIdentifier(AR + road_icon, DRAWABLE, PACKAGE);
						ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(resID);
					ScrrenoffActivity.screen.distanceText.setText(getDidistance(next_road_distance));
					ScrrenoffActivity.screen.remainDistanceText.setText(getRemainDidistance(total_remain_distance,System.currentTimeMillis()+total_remain_time*1000));
				}
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
				MainApplication.gaodeisnavi = true;
				MainApplication.getHander().removeMessages(2);
				MainApplication.getHander().sendEmptyMessageDelayed(2, 20000);
				String sData = intent.getStringExtra(SEND_BUSINESS_DATA);
				if (!TextUtils.isEmpty(sData)) {
					ENaviInfo data;
					try {
						data = (ENaviInfo) EDataFactory.create(sData);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				
					if (data != null) {
					//	String current_road = data.getCurRoadName();
						String next_road =data.getNextRoadName();
						int total_remain_distance = data.getRouteRemainDis();
						int total_remain_time = data.getRouteRemainTime();
						int road_icon = data.getIcon();
						// int total_remain_time =
						// extras.getInt("total_remain_time");
						int next_road_distance = data.getSegRemainDis();
						Log.d("info", "road_icon="+road_icon);
						if (ScrrenoffActivity.screen != null) {
							if(ScrrenoffActivity.screen.isTW){
						//		current_road = ZHConverter.convert(current_road, ZHConverter.TRADITIONAL);
								next_road = ZHConverter.convert(next_road, ZHConverter.TRADITIONAL);
							}
					//		ScrrenoffActivity.screen.currentRoad.setText(current_road);
							ScrrenoffActivity.screen.nextRoad.setText(next_road);
								int resID = context.getResources().getIdentifier(AR + road_icon,DRAWABLE, PACKAGE);
								ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(resID);
							ScrrenoffActivity.screen.distanceText.setText(getDidistance(next_road_distance));
							ScrrenoffActivity.screen.remainDistanceText.setText(getRemainDidistance(total_remain_distance,System.currentTimeMillis()+total_remain_time*1000));
						}
					}
				}
			}

		}
	}

	public static String getDidistance(int s) {
		String dis;
		if (s < 1000) {
			dis = String.format(ScrrenoffActivity.screen.getString(R.string.mi),s);
		} else if(s<100000){
			dis = String.format(ScrrenoffActivity.screen.getString(R.string.gl),(float)s*1.0/1000);
		} else {
			dis = String.format(ScrrenoffActivity.screen.getString(R.string.gl_far),s/1000);
		}
		
		return dis;
	}
	
	public static String getRemainDidistance(int s,long time) {
		long nowTime = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(GMT_8));
		calendar.setTimeInMillis(time);
		int l = (int)(toDay(time) - toDay(nowTime));
		String d;
		switch (l) {
		case 0:
			d = String.format(LEDView.DATE_FORMAT,calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
			break;
		case 1:
			d = "明天";
			break;
		case 2:
			d = "后天";
			break;
		default:
			d = l+"天后";
			break;
		}
		String dis;
		if (s < 1000) {
			dis = String.format(ScrrenoffActivity.screen.getString(R.string.arrive_mi),s,d);
		} else if(s<1000000){
			
			dis = String.format(ScrrenoffActivity.screen.getString(R.string.arrive_gl),(float)s*1.0/1000,d);
		}else {
			dis = String.format(ScrrenoffActivity.screen.getString(R.string.arrive_gl_far),s/1000,d);
		}
		
		return dis;
	}
	
    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }
}
