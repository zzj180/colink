package com.zzj.coogo.screenoff;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;

public class ScreenOffHandler extends Handler {
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
					&& MainApplication.acc_state) {
				boolean keep_screen_on = Settings.System.getInt(MainApplication.app.getContentResolver(), "screen_keep_on", 0) == 0;
				String name = MainApplication.app.topActivity();
				if(MainApplication.gaodeisnavi && keep_screen_on && (Constant.BAIDU_NAVI_ACTIVITY.equals(name) || Constant.GAODE_MAP_ACTIVITY.equals(name))){
					return;
				}else{
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
			if (!MainApplication.autoScreenOff)
				((MainApplication) MainApplication.app).goToSleep();
		}
	}
}
