package com.unisound.unicar.gui.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 
 * @author xiaodong
 * @date 20150617
 */
public class WelcomeActivity extends Activity{

	private static final String TAG = WelcomeActivity.class.getSimpleName();

	public static final String ACTION_FINISH_WELCOMEACTIVITY = "com.unisound.unicar.gui.ACTION_FINISH_WELCOMEACTIVITY";
	
	private TextView tvVersion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		
		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText("V"+DeviceTool.getAppVersionName(this));
		
		IntentFilter filter = new IntentFilter(ACTION_FINISH_WELCOMEACTIVITY);
		registerReceiver(mFinishReceiver, filter);
        Logger.d(TAG, "!--->onCreate()-------registerReceiver mFinishReceiver");
        
        //startWindowService();  //xd delete 20150706
	}	

	/**
	 * 
	 */
	BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Logger.d(TAG, "!--->mFinishReceiver action = "+action);
			if(ACTION_FINISH_WELCOMEACTIVITY.equals(action)){
				//startMainActivity(); //xd delete 20150706
				WelcomeActivity.this.finish();
			}
		}
	};
	
	private boolean isNeedOTA(){
		boolean isNeedOTA = false;
		//TODO:
		Logger.d(TAG, "!--->---checkOTA()----------isNeedOTA = "+isNeedOTA);
		return isNeedOTA;
	}
	
	private void doOTA(){
		Logger.d(TAG, "!--->---doOTA()----------");
		//TODO:
		
		
	}
	
	@SuppressWarnings("unused")
	private void startMainActivity(){
		Logger.d(TAG, "!--->---startMainActivity()-----");
		Intent intent = new Intent(this, GUIMainActivity.class);
		intent.setAction(WindowService.ACTION_START_REQUEST_MAKE_FINISHED);
		startActivity(intent);
	}
	
	@SuppressWarnings("unused")
	private void startWindowService(){
		Logger.d(TAG, "!--->---startWindowService()-----");
		Intent i = new Intent(this, WindowService.class);
		i.setAction(WindowService.ACTION_START_REQUEST_MAKE_FINISHED);
		startService(i);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isNeedOTA()){
			doOTA();
		}
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mFinishReceiver);
	}
	
}
