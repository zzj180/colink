package com.zzj.coogo.screenoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BNRBroadCast extends BroadcastReceiver {

	private static final String EDOG_WINDOW = "window";
	private static final String EDOG_FLOATING_WINDOW = "com.szcx.edog.floating.window";
	private static final String EDOG_RETURN_FLOATING_STATUS = "com.szcx.edog.return.floating.status";
	private static final String EDOG_GET_FLOATING_STATUS = "com.szcx.edog.get.floating.status";
	private static final String TEMP_HIGH_KEYEVENT = "android.intent.action.TEMP_HIGH_KEYEVENT";
//	private static final String TEMP_NORMAL_KEYEVENT = "android.intent.action.TEMP_NORMAL_KEYEVENT";
//	public static final String ACTION_ACC_ON = "android.intent.action.ACC_ON_KEYEVENT";
//	public static final String ACTION_ACC_OFF = "android.intent.action.ACC_OFF_KEYEVENT";
	private static final String ACTION_TOUCHEVENT = "android.intent.action.TOUCHEVENT";
	private static final String ACTION_BACK_CAR = "android.intent.action.BACK_CAR_ON_KEYEVENT";
	private static final String TAG = "BNRBroadCast";

	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, action+","+MainApplication.acc_state);
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			Intent service = new Intent(context, ScreenOffService.class);
			service.putExtra("bootreceive", true);
			context.startService(service);
			String device = SystemPropertiesProxy.get(context, "ro.product.device");
			if(!device.contains("8665")){
				context.startService(new Intent(context, SwitchServeice.class));
			}
			
		} else if (Intent.ACTION_SCREEN_ON.equals(action)) {
			//免打扰重新计时
			MainApplication.mScreenOff=false;
			if (MainApplication.acc_state ) {
				MainApplication.app.goNoDiturb();
			}
			if (ScrrenoffActivity.screen == null) {
			/*	Intent edog = new Intent();
				edog.setAction(EDOG_FLOATING_WINDOW);
				edog.putExtra(EDOG_WINDOW, MainApplication.edog_state);*/
			}else {
				ScrrenoffActivity.screen.finish();
			}
		} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
			if (MainApplication.acc_state) {
				MainApplication.mScreenOff=true;
				if (ScrrenoffActivity.screen != null) {
					ScrrenoffActivity.screen.finish();
				}
			}
			Intent edog = new Intent();
			edog.setAction(EDOG_GET_FLOATING_STATUS);
			context.sendBroadcast(edog);
		} else if (action.equals(TEMP_HIGH_KEYEVENT)) {
			if (ScrrenoffActivity.screen == null) {
				Intent intent2 = new Intent(context,ScrrenoffActivity.class);
				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				boolean isCamera = Constant.CAMERA_ACTIVITY.equals(MainApplication.app.topActivity());
				intent2.putExtra("isCamera", isCamera);
				context.startActivity(intent2);
				MainApplication.getHander().removeMessages(1);
				MainApplication.getHander().removeMessages(3);
			}
			/*MainApplication.temp_alert = true;
			MainApplication.getHander().removeMessages(1);
			if (ScrrenoffActivity.screen != null) {
				ScrrenoffActivity.screen.exit();
			}
			if (MainApplication.tempActivity == null) {
				Intent intent2 = new Intent(context, TemperatureActivity.class);
				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent2);
			}

			try {
				forceStopPackage(BAIDU_NAVI, context);
				forceStopPackage(GAODE_MAP, context);
				forceStopPackage(BAIDU_MAP, context);
				forceStopPackage(KUWO_MUSIC, context);
			} catch (Exception e) {
				e.printStackTrace();
			}*/

		} else if (ACTION_TOUCHEVENT.equals(action)) {
			if (MainApplication.acc_state)
				MainApplication.app.goNoDiturb();
		} else if (EDOG_RETURN_FLOATING_STATUS.equals(action)) {
			MainApplication.edog_state = intent.getBooleanExtra(EDOG_WINDOW,MainApplication.edog_state);
			Intent edog = new Intent();
			edog.setAction(EDOG_FLOATING_WINDOW);
			edog.putExtra(EDOG_WINDOW, false);
			context.sendBroadcast(edog);
		} else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
			
			// 短按Home键
			if (ScrrenoffActivity.screen != null) {
				ScrrenoffActivity.screen.exit();
			}
		} else if(ACTION_BACK_CAR.equals(action)){
			if (ScrrenoffActivity.screen != null) {
				ScrrenoffActivity.screen.exit();
			}
		}
	}

}
