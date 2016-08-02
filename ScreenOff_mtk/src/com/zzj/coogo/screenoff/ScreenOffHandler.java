package com.zzj.coogo.screenoff;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;

public class ScreenOffHandler extends Handler {
	private static final String TTS_SHOW = "tts_show";
	private static final String SCREEN_KEEP_ON = "screen_keep_on";

	public ScreenOffHandler(Looper looper) {
		super(looper);
	}

	/**
	 * Handle incoming transaction requests. The incoming requests are initiated
	 * by the MMSC Server or by the MMS Client itself.
	 */
	@Override
	public void handleMessage(Message msg) {
		if (msg.what == 1) {
			if (ScrrenoffActivity.screen == null && !MainApplication.mScreenOff
					&& MainApplication.autoScreenOff
					&& MainApplication.acc_state && !MainApplication.isBackCar) {
				boolean keep_screen_on = Settings.System.getInt(MainApplication.app.getContentResolver(), SCREEN_KEEP_ON, 1) == 1;
				boolean enable = Settings.System.getInt(MainApplication.app.getContentResolver(), TTS_SHOW, 0) == 1;
				String name = MainApplication.app.topActivity();
				if(MainApplication.gaodeisnavi && keep_screen_on && enable && (Constant.BAIDU_NAVI_ACTIVITY.equals(name) 
						|| Constant.GAODE_MAP_ACTIVITY.equals(name) || Constant.GAODE_CAR_ACTIVITY.equals(name))){
					return;
				}else{
					MainApplication.app.sendBroadcast(new Intent(Constant.NO_DISTURB_ACTION));
					Intent intent2 = new Intent(MainApplication.app,ScrrenoffActivity.class);
					intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					boolean isCamera = Constant.CAMERA_ACTIVITY.equals(name);
					intent2.putExtra("isCamera", isCamera);
					MainApplication.app.startActivity(intent2);
				}
			}
		} else if (msg.what == 2) {
			MainApplication.gaodeisnavi = false;
			if (ScrrenoffActivity.screen != null) {
				ScrrenoffActivity.screen.ledView.setVisibility(View.VISIBLE);
				ScrrenoffActivity.screen.layout.setVisibility(View.GONE);
			}
		} else if (msg.what == 3) {
			if (!MainApplication.autoScreenOff && !MainApplication.isBackCar){
				boolean keep_screen_on = Settings.System.getInt(MainApplication.app.getContentResolver(), SCREEN_KEEP_ON, 1) == 1;
				boolean enable = Settings.System.getInt(MainApplication.app.getContentResolver(), TTS_SHOW, 0) == 1;
				String name = MainApplication.app.topActivity();
				if(MainApplication.gaodeisnavi && keep_screen_on && enable && (Constant.BAIDU_NAVI_ACTIVITY.equals(name)
						|| Constant.GAODE_MAP_ACTIVITY.equals(name) || Constant.GAODE_CAR_ACTIVITY.equals(name))){
					return;
				}else{
					((MainApplication) MainApplication.app).goToSleep();
				}
			}
		}
	}
}
