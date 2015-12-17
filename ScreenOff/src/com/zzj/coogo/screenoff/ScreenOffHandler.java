package com.zzj.coogo.screenoff;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ScreenOffHandler extends Handler{
	public ScreenOffHandler(Looper looper) {
		super(looper);
	}
	/**
	 * Handle incoming transaction requests. The incoming requests are
	 * initiated by the MMSC Server or by the MMS Client itself.
	 */
	@Override
	public void handleMessage(Message msg) {
		if (msg.what == 1) {
			if (ScrrenoffActivity.screen == null && MainApplication.time > 0 && !MainApplication.mScreenOff) {
				Intent intent2 = new Intent(MainApplication.app,ScrrenoffActivity.class);
				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				MainApplication.app.startActivity(intent2);
			} 
		}else if(msg.what==2){
			MainApplication.gaodeisnavi=false;
		}
	}
}
