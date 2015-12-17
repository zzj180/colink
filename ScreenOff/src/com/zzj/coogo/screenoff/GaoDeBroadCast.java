package com.zzj.coogo.screenoff;

import java.text.DecimalFormat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class GaoDeBroadCast extends BroadcastReceiver{
	
	private static final String PACKAGE = "com.zzj.coogo.screenoff";
	private static final String TOTAL_REMAIN_DISTANCE2 = "total_remain_distance";
	private static final String ROAD_ICON2 = "road_icon";
	private static final String CURRENT_ROAD_REMAIN_DISTANCE = "current_road_remain_distance";
	private static final String NEXT_ROAD_NAME = "next_road_name";
	private static final String CURRENT_ROAD_NAME = "current_road_name";
	private static final String gl = "公里";
	private static final String mi = "米";
	private static final String _00 = "#.00";
	private static final String DRAWABLE = "drawable";
	private static final String AR = "ar_";
	private static final String TYPE = "type";
	private final static String NAVI_GAODE="com.amap.navi";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(NAVI_GAODE.equals(action)){
			Bundle extras = intent.getExtras();
			MainApplication.gaodeisnavi=true;
			MainApplication.getHander().removeMessages(2);
			MainApplication.getHander().sendEmptyMessageDelayed(2, 20000);
			int type = extras.getInt(TYPE);
			switch (type) {
			/*case 100:
				String remainName = extras.getString("poi_name");
				int remainDistance = extras.getInt("total_remain_distance");
				int remainTime = extras.getInt("total_remain_time");
				break;*/
			case 101:
				String current_road = extras.getString(CURRENT_ROAD_NAME);
				String next_road = extras.getString(NEXT_ROAD_NAME);
				int next_road_distance = extras.getInt(CURRENT_ROAD_REMAIN_DISTANCE);
				int road_icon = extras.getInt(ROAD_ICON2);
		//		int total_remain_time = extras.getInt("total_remain_time");
				int total_remain_distance = extras.getInt(TOTAL_REMAIN_DISTANCE2);
				if (ScrrenoffActivity.screen != null) {
					ScrrenoffActivity.screen.currentRoad.setText(current_road);
					ScrrenoffActivity.screen.nextRoad.setText(next_road);
					if(next_road_distance < 500){
							int resID = context.getResources().getIdentifier(AR + road_icon, DRAWABLE,PACKAGE);
							ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(resID);
					}else{
							ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_9);
					}
					if (next_road_distance < 1000) {
						ScrrenoffActivity.screen.distanceText.setText(next_road_distance + mi);
					} else {
						DecimalFormat df = new DecimalFormat(_00);
						ScrrenoffActivity.screen.distanceText.setText(df.format(next_road_distance * 1.0 / 1000) + gl);

					}
					if (total_remain_distance < 1000) {
						ScrrenoffActivity.screen.remainDistanceText.setText(total_remain_distance + mi);
					} else {
						DecimalFormat df = new DecimalFormat(_00);
						ScrrenoffActivity.screen.remainDistanceText.setText(df.format(total_remain_distance * 1.0 / 1000) + gl);
					}
				}
				break;
			/*case 102:
				int[] camera_distance_array = extras.getIntArray("camera_distance");
				int[] camera_type_array = extras.getIntArray("camera_type");
				for (int i = 0; i < camera_distance_array.length; i++) {
				}
				for (int i = 0; i < camera_type_array.length; i++) {
				}
				break;*/
			default:
				break;
			}
		}
		
	}

}
