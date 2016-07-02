package com.zzj.softwareservice.mx;

import com.zzj.softwareservice.ZZJApplication;
import com.zzj.softwareservice.database.DBConstant;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

public class MeiXingBroadCast extends BroadcastReceiver {
	private final static String TURN_ID = "turnID";
	private final static String DESTDISTANCE = "destdistance";
	private final static String ROADNAME = "roadname";
	private final static String NEXTROADNAME = "nextroadname";
	private final static String TURNDISTANCE = "turndistance";
	private final static String DESTTIME = "desttime";
	private final static String TURNING_INFO_ACTION = "com.mxnavi.mxnavi.TO_CTRL_TURNING_INFO";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (TURNING_INFO_ACTION.equals(action)) {
			((ZZJApplication)context.getApplicationContext()).getHander().removeMessages(0);
			((ZZJApplication)context.getApplicationContext()).getHander().sendEmptyMessageDelayed(0, 20000);
			
			int trunId = intent.getIntExtra(TURN_ID, 0);
			int total_remain_distance = intent.getIntExtra(DESTDISTANCE, 0);
			int next_road_distance = intent.getIntExtra(TURNDISTANCE, 0);
			String currentRoad = intent.getStringExtra(ROADNAME);
			String nextRoad = intent.getStringExtra(NEXTROADNAME);
			int arrived_time = intent.getIntExtra(DESTTIME, 0);
			String maneuver = null;

			switch (trunId) {
			case 0:
				maneuver = "ar_1";
				break;
			case 1:
				maneuver = "ar_2";
				break;
			case 2:
				maneuver = "ar_4";
				break;
			case 3:
				maneuver = "ar_6";
				break;
			case 4:
				maneuver = "ar_8";
				break;
			case 5:
				maneuver = "ar_3";
				break;
			case 6:
				maneuver = "ar_5";
				break;
			case 7:
				maneuver = "ar_7";
				break;
			case 8:
				maneuver = "ar_18";
				break;
			case 32:
				maneuver = "ar_16";
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

		}
	}
}
