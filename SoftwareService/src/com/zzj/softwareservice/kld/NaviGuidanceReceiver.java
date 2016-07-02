package com.zzj.softwareservice.kld;

import com.zzj.softwareservice.ZZJApplication;
import com.zzj.softwareservice.database.DBConstant;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class NaviGuidanceReceiver extends BroadcastReceiver implements
		NaviConstact, GuidanceInfo {

	private String[] stringArray;
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Bundle bundle = intent.getExtras();
		Log.e("NaviGuidanceReceiver", action);
		if (action.equals(ACTION)) {
			((ZZJApplication)context.getApplicationContext()).getHander().removeMessages(0);
			((ZZJApplication)context.getApplicationContext()).getHander().sendEmptyMessageDelayed(0, 20000);
			if (bundle != null) {
				int type = bundle.getInt(ACTION_TYPE);
				if (type == MSG_ID_GUIDANCEINFO) {
					stringArray = bundle.getStringArray(GUIDANCE_ARRAY_PARAM);
					try {
						int road_icon = Integer.parseInt(stringArray[0]);
						long next_road_distance = Long.parseLong(stringArray[1]);
						long total_remain_distance = Long.parseLong(stringArray[2]);
						long arrived_time = Long.parseLong(stringArray[4]);
						String currentRoad = stringArray[6];
						String nextRoad = stringArray[7];
						
						String maneuver=null;
						switch (road_icon) {
						case 0:
							maneuver = "ar_1";
							break;
						case 1:
							maneuver = "ar_5";
							break;
						case 2:
							maneuver = "ar_3";
							break;
						case 4:
							maneuver = "ar_8";
							break;
						case 5:
							maneuver = "ar_2";
							break;
						case 6:
							maneuver = "ar_4";
							break;
						default:
							maneuver = "ar_1";
							break;
						}
						
						ContentValues con = new ContentValues();
						con.put(DBConstant.NaviTable.MANEUVER_IMAGE, maneuver);
						con.put(DBConstant.NaviTable.NEXT_ROAD_DISTANCE, next_road_distance);
						con.put(DBConstant.NaviTable.CURRENT_ROADNAME, currentRoad);
						con.put(DBConstant.NaviTable.NEXT_ROAD, nextRoad);
						con.put(DBConstant.NaviTable.TOTAL_REMAIN_DISTANCE, total_remain_distance);
						con.put(DBConstant.NaviTable.TOTAL_REMAIN_TIME, arrived_time);
						con.put(DBConstant.NaviTable.ISNAVING, 1);
						context.getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, con, null, null);
						
					} catch (Exception e) {
						e.printStackTrace();
					}

				}


			}

		}

	}

}
