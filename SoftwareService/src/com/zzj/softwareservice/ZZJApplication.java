package com.zzj.softwareservice;

import com.zzj.softwareservice.bd.BNRService;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.provider.Settings;

public class ZZJApplication extends Application {

	private Handler handler;
	@Override
	public void onCreate() {
		super.onCreate();
		startService(new Intent(this, BNRService.class));
		Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
		CrashHandler.getInstance().init(this);
	}
	
	public  Handler getHander() {
		if (handler == null) {
			handler = new NaviStateHandler(getMainLooper(),this);
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
