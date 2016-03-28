package com.zzj.coogo.screenoff;

import java.lang.reflect.Method;
import java.util.List;

import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
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
	public static boolean gaodeisnavi;
	public static TemperatureActivity tempActivity;
	public static Handler handler;
	private static Looper looper;
	public static MainApplication app;
	public static int needSleep;
	private String CONSUMER_CODE = "ro.inet.consumer.code";
	public static String custom_code;
	public final static String NO_DIDTURB = "no_disturb";
	private static final String SCREEN_OFF_SWITCH = "screen_off_switch";
	private String ACC_STATE = "acc_state";

	public static boolean autoScreenOff;
	public static long exitTime;
	public static boolean mScreenOff=false;

	
	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		looper = getMainLooper();
		custom_code = SystemPropertiesProxy.get(this, CONSUMER_CODE);
		acc_state = Settings.System.getInt(getContentResolver(),ACC_STATE, 1)==1;
		startService(new Intent(this, BNRService.class));
		startService(new Intent(this, SwitchServeice.class));
		goNoDiturb();
		getContentResolver().registerContentObserver(Settings.System.getUriFor(NO_DIDTURB), true,
				new ContentObserver(new Handler(getMainLooper())) {
					@Override
					public void onChange(boolean selfChange) {
						goNoDiturb();
					}

				});
		getContentResolver().registerContentObserver(Settings.System.getUriFor(ACC_STATE), true,
				new ContentObserver(getHander()) {
					@Override
					public void onChange(boolean selfChange) {
						acc_state = Settings.System.getInt(getContentResolver(),ACC_STATE, 1)==1;
						if(acc_state){
							startService(new Intent(getApplicationContext(), SwitchServeice.class));
							goNoDiturb();
							startService(new Intent(getApplicationContext(), BNRService.class));
						}else{
							getHander().removeMessages(1);
							if (ScrrenoffActivity.screen != null) {
								ScrrenoffActivity.screen.finish();
							}
							try {
								forceStopPackage(Constant.BAIDU_NAVI, getApplicationContext());
								forceStopPackage(Constant.GAODE_MAP, getApplicationContext());
								forceStopPackage(Constant.KUWO_MUSIC, getApplicationContext());
								forceStopPackage(Constant.BAIDU_MAP, getApplicationContext());
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								stopService(new Intent(getApplicationContext(), BNRService.class));
								stopService(new Intent(getApplicationContext(),SwitchServeice.class));
								android.os.Process.killProcess(android.os.Process.myPid());
							}
						}
					}
				});
		
//		CrashHandler crasHandler = CrashHandler.getInstance();
//		crasHandler.init(this);
	}

	public void goNoDiturb() {
		int time = Settings.System.getInt(getApplicationContext().getContentResolver(), NO_DIDTURB,300);
		autoScreenOff = Settings.System.getInt(getApplicationContext().getContentResolver(), SCREEN_OFF_SWITCH,0)==0;
		Log.d("sleep", "time =" + time + ",screenOFF = "+autoScreenOff);
		getHander().removeMessages(1);
		getHander().removeMessages(3);
		if(autoScreenOff){
			getHander().sendEmptyMessageDelayed(1, time * 1000);
		}else{
			getHander().sendEmptyMessageDelayed(3, time * 1000);
		}
	
	}
	
	public String topActivity() {
		String className = null;
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		// 应用程序位于堆栈的顶层
		if (tasksInfo != null) {
			ComponentName name = tasksInfo.get(0).topActivity;
			if(name!=null){
				className = name.getClassName();
			}
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
	
	public void goToSleep(){
		Log.d("sleep", "sleep");
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if(pm.isScreenOn()){
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
	
	private void forceStopPackage(String pkgName, Context context)
			throws Exception {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
		method.invoke(am, pkgName);
	}
}
