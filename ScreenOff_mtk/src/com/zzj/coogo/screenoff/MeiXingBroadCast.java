package com.zzj.coogo.screenoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MeiXingBroadCast extends BroadcastReceiver{
	private final static String TURN_ID="turnID";
	private final static String DESTDISTANCE="destdistance";
	private final static String ROADNAME="roadname";
	private final static String NEXTROADNAME="nextroadname";
	private final static String TURNDISTANCE="turndistance";
	private final static String DESTTIME="desttime";
	private final static String TURNING_INFO_ACTION="com.mxnavi.mxnavi.TO_CTRL_TURNING_INFO";
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(TURNING_INFO_ACTION.equals(action )){
			int trunId = intent.getIntExtra(TURN_ID, 0);
			int total_remain_distance = intent.getIntExtra(DESTDISTANCE, 0);
			int next_road_distance = intent.getIntExtra(TURNDISTANCE, 0);
	//		String current_road = intent.getStringExtra(ROADNAME);
			String next_road = intent.getStringExtra(NEXTROADNAME);
			int dest_time = intent.getIntExtra(DESTTIME,0);
			MainApplication.gaodeisnavi = true;
			MainApplication.getHander().removeMessages(2);
			MainApplication.getHander().sendEmptyMessageDelayed(2, 20000);
			if (ScrrenoffActivity.screen != null) {
	//			ScrrenoffActivity.screen.currentRoad.setText(current_road);
				ScrrenoffActivity.screen.nextRoad.setText(next_road);
				if(next_road_distance < 1000){
					int resID ;
					switch (trunId) {
					case 0:
						resID = R.drawable.ar_1;
						break;
					case 1:
						resID = R.drawable.ar_2;
						break;
					case 2:
						resID = R.drawable.ar_4;
						break;
					case 3:
						resID = R.drawable.ar_6;
						break;
					case 4:
						resID = R.drawable.ar_8;
						break;
					case 5:
						resID = R.drawable.ar_3;
						break;
					case 6:
						resID = R.drawable.ar_5;
						break;
					case 7:
						resID = R.drawable.ar_7;
						break;
					case 8:
						resID = R.drawable.ar_18;
						break;
					case 32:
						resID = R.drawable.ar_16;
						break;
					default:
						resID = R.drawable.ar_1;
						break;
					}
					ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(resID);
				}else{
					ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_1);
				}
				ScrrenoffActivity.screen.distanceText.setText(GaoDeBroadCast.getDidistance(next_road_distance));
				ScrrenoffActivity.screen.remainDistanceText.setText(GaoDeBroadCast.getRemainDidistance(total_remain_distance,System.currentTimeMillis()+dest_time*1000));
			}
		}
	}
}
