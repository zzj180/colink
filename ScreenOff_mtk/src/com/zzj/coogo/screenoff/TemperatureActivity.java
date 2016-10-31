package com.zzj.coogo.screenoff;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class TemperatureActivity extends Activity {
	private BNRBroadCast mScreenOffReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// No Titlebar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

/*		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/
		View main = LayoutInflater.from(this).inflate(R.layout.temp_alert,null);
		main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		super.onCreate(savedInstanceState);
		setContentView(main);
		// enterLightsOutMode(getWindow());
	//	MainApplication.tempActivity = this;

		WindowManager.LayoutParams localLayoutParams = getWindow()
				.getAttributes();
		localLayoutParams.screenBrightness = 15 / 255.0F;
		getWindow().setAttributes(localLayoutParams);

		final IntentFilter homeFilter = new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		mScreenOffReceiver = new BNRBroadCast();
		registerReceiver(mScreenOffReceiver, homeFilter);
		Intent intnet = new Intent("android.intent.action.CLOSE_WAKEUP");
		sendBroadcast(intnet);

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		Intent intnet = new Intent("android.intent.action.OPEN_WAKEUP");
		sendBroadcast(intnet);
		unregisterReceiver(mScreenOffReceiver);
//		MainApplication.tempActivity = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME || keyCode == 66
				|| keyCode == 86) {

			return true;

		}

		return super.onKeyDown(keyCode, event);
	}

	public static void enterLightsOutMode(Window window) {
		WindowManager.LayoutParams params = window.getAttributes();
		params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
		window.setAttributes(params);
	}

}
