package com.colink.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

public class BootReceiver extends BroadcastReceiver {
	public static final String TELPHONE_SERVICE = "cn.colink.service.Telphone_Service";
	private static final String ACTION_ACC_ON = "android.intent.action.ACC_ON_KEYEVENT";
	private static final String TEMP_HIGH_KEYEVENT="android.intent.action.TEMP_HIGH_KEYEVENT";
	private static final String TEMP_NORMAL_KEYEVENT="android.intent.action.TEMP_NORMAL_KEYEVENT";
	@Override
	public void onReceive(Context context, Intent intent) {
		String action=intent.getAction();
		if(Intent.ACTION_BOOT_COMPLETED.equals(action)){
			context.startService(new Intent(TELPHONE_SERVICE));
		}else if(ACTION_ACC_ON.equals(action)){
			context.startService(new Intent(TELPHONE_SERVICE));
		}else if(TEMP_HIGH_KEYEVENT.equals(action)){
			context.stopService(new Intent(TELPHONE_SERVICE));
			Process.killProcess(android.os.Process.myPid());
		}else if(TEMP_NORMAL_KEYEVENT.equals(action)){
			context.startService(new Intent(TELPHONE_SERVICE));
		}
		/*else if(PLAY_TTS.equals(action)){
			String playText=intent.getStringExtra("content");
			TTSController.getInstance(context).playText(playText);
		}*/
	}
	
}
