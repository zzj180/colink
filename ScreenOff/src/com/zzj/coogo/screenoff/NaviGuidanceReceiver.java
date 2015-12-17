package com.zzj.coogo.screenoff;

import java.text.DecimalFormat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class NaviGuidanceReceiver extends BroadcastReceiver implements
		NaviConstact,GuidanceInfo {

	private static final String gl = "公里";
	private static final String mi = "米";
	private static final String _00 = "#.00";
	public static final String PTT = "凯立德一键通";
	private String[] stringArray;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Bundle bundle = intent.getExtras();
		
		if (action.equals(ACTION)) {
			
			if(bundle!=null){
				
			int type = bundle.getInt(ACTION_TYPE);
			
				switch (type){
				
				case MSG_ID_GUIDANCEINFO:
					
					stringArray = bundle.getStringArray(GUIDANCE_ARRAY_PARAM);
					Log.e("NaviGuidanceReceiver", stringArray[0]);
					if (ScrrenoffActivity.screen != null) {
						ScrrenoffActivity.screen.currentRoad.setText(stringArray[6]);
						ScrrenoffActivity.screen.nextRoad.setText(stringArray[7]);
						long next_road_distance = Long.parseLong(stringArray[1]);
						if(next_road_distance  < 500){
								int road_icon = Integer.parseInt(stringArray[0]);
								switch (road_icon) {
								case 0:
									ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_1);
									break;
								case 1:
									ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_5);
									break;
								case 2:
									ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_3);
									break;
								case 4:
									ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_8);
									break;
								case 5:
									ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_2);
									break;
								case 6:
									ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_4);
									break;
								default:
									ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_1);
									break;
								}
						}else{
							ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_1);	
						}
						if (next_road_distance < 1000) {
							ScrrenoffActivity.screen.distanceText.setText(next_road_distance + mi);
						} else {
							DecimalFormat df = new DecimalFormat(_00);
							ScrrenoffActivity.screen.distanceText.setText(df.format(next_road_distance * 1.0 / 1000) + gl);

						}
						long total_remain_distance = Long.parseLong(stringArray[2]);
						if (total_remain_distance < 1000) {
							ScrrenoffActivity.screen.remainDistanceText.setText(total_remain_distance + mi);
						} else {
							DecimalFormat df = new DecimalFormat(_00);
							ScrrenoffActivity.screen.remainDistanceText.setText(df.format(total_remain_distance * 1.0 / 1000) + gl);
						}
						
					}
					break;
					
				 default: break; 
				 
				}
				
			}
			 
		}
		
	}


}
