package cn.coogo.hardware.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent ;
import android.text.TextUtils;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	private static final String TAG = "HardwareService";
	public static final String POWER_ON_START = "powerOnStart";
	public static final String FIRST_TIME = "first_time";
	@Override
	public void onReceive(Context context, Intent intent) {
	    String action = intent.getAction();
	    Log.i(TAG, "Receive action : " + action);
		if (TextUtils.equals(action, ACTION)){
			Log.i(TAG, "Receive BOOT_COMPLETED!");
			startHardwareService(context);
			if(!HardwareService.getSharePrs(context)){
			    HardwareService.setSharePrs(context, true);
			    HardwareService.initEdog(context);
			}
		}
	}
	/**
	 * lzl
	 */
    private void startHardwareService(Context context) {
        Intent intent = new Intent(context, HardwareService.class);
        context.startService(intent);
    }

}
