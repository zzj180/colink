package com.zzj.coogo.screenoff;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

public class MainApplication extends Application {
	public static boolean acc_state = true;
	public static boolean edog_state = true;
	public static boolean temp_alert = false;
	public static boolean gaodeisnavi;
	public static TemperatureActivity tempActivity;
	public static Handler handler;
	private static Looper looper;
	public static Application app;
	public static int needSleep;
	private String CONSUMER_CODE = "ro.inet.consumer.code";
	public static String custom_code;
	public final static String NO_DIDTURB = "no_disturb";
	public static int time;
	public static long exitTime;
	public static boolean mScreenOff=false;

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		looper = getMainLooper();
		time = Settings.System.getInt(getContentResolver(), NO_DIDTURB, 300);
		custom_code = SystemPropertiesProxy.get(this, CONSUMER_CODE);
		if (BNRBroadCast.readAccFile()) {
			acc_state = true;
		} else {
			acc_state = false;
		}
		startService(new Intent(this, BNRService.class));
		getContentResolver().registerContentObserver(Settings.System.getUriFor(NO_DIDTURB), true,
				new ContentObserver(new Handler(getMainLooper())) {
					@Override
					public void onChange(boolean selfChange) {
						time = Settings.System.getInt(getContentResolver(),NO_DIDTURB, 300);
						getHander().removeMessages(1);
						if (time > 0) {
							getHander().sendEmptyMessageDelayed(1, time * 1000);
						}
					}
				});
		CrashHandler crasHandler = CrashHandler.getInstance();
		crasHandler.init(this);
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}
