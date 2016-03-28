package com.zzj.coogo.screenoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class NaviGuidanceReceiver extends BroadcastReceiver implements
		NaviConstact, GuidanceInfo {

	public static final String PTT = "凯立德一键通";
	private String[] stringArray;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Bundle bundle = intent.getExtras();
		Log.e("NaviGuidanceReceiver", action);
		if (action.equals(ACTION)) {

			if (bundle != null) {

				int type = bundle.getInt(ACTION_TYPE);

				switch (type) {

				case MSG_ID_GUIDANCEINFO:

					stringArray = bundle.getStringArray(GUIDANCE_ARRAY_PARAM);
					Log.e("NaviGuidanceReceiver", stringArray[0]);
					if (ScrrenoffActivity.screen != null) {
						// ScrrenoffActivity.screen.currentRoad.setText(stringArray[6]);
						ScrrenoffActivity.screen.nextRoad.setText(stringArray[7]);
						long next_road_distance = Long.parseLong(stringArray[1]);
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
							ScrrenoffActivity.screen.maneuverImage
									.setBackgroundResource(R.drawable.ar_8);
							break;
						case 5:
							ScrrenoffActivity.screen.maneuverImage
									.setBackgroundResource(R.drawable.ar_2);
							break;
						case 6:
							ScrrenoffActivity.screen.maneuverImage
									.setBackgroundResource(R.drawable.ar_4);
							break;
						default:
							ScrrenoffActivity.screen.maneuverImage
									.setBackgroundResource(R.drawable.ar_1);
							break;
						}
						ScrrenoffActivity.screen.distanceText.setText(GaoDeBroadCast.getDidistance((int) next_road_distance));
						long total_remain_distance = Long.parseLong(stringArray[2]);
						long arrived_time = Long.parseLong(stringArray[4]);
						ScrrenoffActivity.screen.remainDistanceText.setText(GaoDeBroadCast.getRemainDidistance(
										(int) total_remain_distance,
										System.currentTimeMillis() + arrived_time));
						MainApplication.gaodeisnavi = true;
						MainApplication.getHander().removeMessages(2);
						MainApplication.getHander().sendEmptyMessageDelayed(2, 20000);

					}
					break;

				default:
					break;

				}

			}

		}

	}

}
