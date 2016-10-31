package com.zzj.coogo.screenoff;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

public class MainApplication extends Application {

	public static boolean acc_state = true;
	public static boolean edog_state = true;
	public static boolean temp_alert = false;
	// public static boolean gaodeisnavi;
	public static Handler handler;
	private static Looper looper;
	public static MainApplication app;
	
	/*
	 * 客户编号
	 */
	public static String custom_code; 
	
	//自动免打扰时间
	public final static String NO_DIDTURB = "no_disturb";

	/*
	 * 自动进入免打扰
	 */
	public static boolean autoScreenOff;
	private static final String SCREEN_OFF_SWITCH = "screen_off_switch";
	
	
	/*
	 * 后拉
	 */
	public static boolean isBackCar;
	
	/**
	 * 是否关屏
	 */
	public static boolean mScreenOff = false;

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		looper = getMainLooper();
		custom_code = SystemPropertiesProxy.get(this, "ro.inet.consumer.code");
		acc_state = Settings.System.getInt(getContentResolver(), "acc_state", 1) == 1;
		String device = SystemPropertiesProxy.get(this, "ro.product.device");
		if(!device.contains("8665")){
			startService(new Intent(this, SwitchServeice.class));
		}
		startService(new Intent(this, ScreenOffService.class));
		
		goNoDiturb();
		CrashHandler crasHandler = CrashHandler.getInstance();
		crasHandler.init(this);
	}

	/**
	 * 计时免打扰
	 */
	public void goNoDiturb() {
		int time = Settings.System.getInt(getApplicationContext()
				.getContentResolver(), NO_DIDTURB, 60);
		autoScreenOff = Settings.System.getInt(getApplicationContext()
				.getContentResolver(), SCREEN_OFF_SWITCH, 0) == 0;
		Log.d("sleep", "time =" + time + ",screenOFF = " + autoScreenOff);
		getHander().removeMessages(1);
		getHander().removeMessages(3);
		if (autoScreenOff) {
			getHander().sendEmptyMessageDelayed(ScreenOffHandler.WHAT_NO_DISTURB, time * 1000);
		} else {
			getHander().sendEmptyMessageDelayed(ScreenOffHandler.WHAT_GO_SLEEP, time * 1000);
		}

	}
	
	/**
	 * 
	 * @return 栈顶Activity
	 */
	public String topActivity() {
		String className = null;
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		try {
			List<RunningTaskInfo> tasksInfo = activityManager
					.getRunningTasks(1);
			// 应用程序位于堆栈的顶层
			if (tasksInfo != null) {
				ComponentName name = tasksInfo.get(0).topActivity;
				if (name != null) {
					className = name.getClassName();
				}
			}
		} catch (Exception e) {
		}

		return className;
	}

	public static Handler getHander() {
		if (handler == null) {
			handler = new ScreenOffHandler(looper);
		}
		return handler;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	/**
	 * 休眠
	 */
	public void goToSleep() {
		Log.d("sleep", "sleep");
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (pm.isScreenOn()) {
			pm.goToSleep(SystemClock.uptimeMillis());
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

}
