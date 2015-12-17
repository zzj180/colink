package com.colink.bluetoothe;

import com.colink.application.Application;
import com.colink.bluetoolthe.R;
import com.colink.service.TelphoneService;
import com.colink.service.TelphoneService.localBinder;
import com.colink.util.Constact;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingActivity extends Activity implements OnClickListener,
		Constact, OnCheckedChangeListener {

	public EditText blueName, pinValue;
	public TextView pairDevice;
	public ToggleButton callToggle, connToggle/* , a2dpToggle; */;
	private TelphoneService mService;

	private LinearLayout fouces;

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

			mService = null;

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			mService = ((localBinder) service).getService();
			mService.sendCommand(QUERY_AUTO_COMMAND);
		}

	};

	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(ACTION_DEVICE_ADDRESS.equals(action)){
				pairDevice.setText(Application.device_address);
			}else if(ACTION_DEVICE_NAME.equals(action)){
				blueName.setText(Application.blueTooth_name);
			}else if(ACTION_DEVICE_PIN.equals(action)){
				pinValue.setText(Application.pin_value);
			}else if(ACTION_DEVICE_STATE.equals(action)){
				if(Application.state==CONNECTED)
					pairDevice.setText(getString(R.string.connected));
				else
					pairDevice.setText(getString(R.string.unconnect));
			}else if(ACTION_DEVICE_AUTO.equals(action)){
				callToggle.setChecked(Application.auto_call);
				connToggle.setChecked(Application.auto_conn);
			}
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {

	/*	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

		// No Titlebar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

		initView();
		
		IntentFilter filter=new IntentFilter();
		filter.addAction(ACTION_DEVICE_ADDRESS);
		filter.addAction(ACTION_DEVICE_NAME);
		filter.addAction(ACTION_DEVICE_PIN);
		filter.addAction(ACTION_DEVICE_STATE);
		filter.addAction(ACTION_DEVICE_AUTO);
		registerReceiver(receiver, filter);

		Intent service = new Intent(this, TelphoneService.class);

		bindService(service, conn, BIND_AUTO_CREATE);
		
		getContentResolver().registerContentObserver(
				Settings.System.getUriFor(NIGHT_MODE),
				false, new ContentObserver(new Handler()) {
					@Override
					public void onChange(boolean selfChange) {
						initView();
					}
				});

	}

	private void initView() {
		int isDay = Settings.System.getInt(getContentResolver(),NIGHT_MODE,DAY_VALUE);
		if(isDay==DAY_VALUE){
			setContentView(R.layout.settting);
		}else{
			setContentView(R.layout.settting_night);
		}

		blueName = (EditText) findViewById(R.id.blueName);

		pinValue = (EditText) findViewById(R.id.pinValue);

		pairDevice = (TextView) findViewById(R.id.devicePair);

		fouces = (LinearLayout) findViewById(R.id.fouces);

		callToggle = (ToggleButton) findViewById(R.id.autoCall);

		connToggle = (ToggleButton) findViewById(R.id.autoConn);

		// a2dpToggle = (ToggleButton) findViewById(R.id.autoA2DP);

		connToggle.setOnCheckedChangeListener(this);

		callToggle.setOnCheckedChangeListener(this);

		// a2dpToggle.setOnCheckedChangeListener(this);
		callToggle.setChecked(Application.auto_call);

		connToggle.setChecked(Application.auto_conn);

		/*
		 * a2dpToggle.setChecked(getSharedPreferences(BLUETOOTH, MODE_PRIVATE)
		 * .getBoolean(A2DP_SWITCH, false));
		 */
		findViewById(R.id.duankai).setOnClickListener(this);
		findViewById(R.id.xiugai).setOnClickListener(this);
		findViewById(R.id.pin).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);

		findViewById(R.id.home).setOnClickListener(this);

		pairDevice.requestFocus();
		if (Application.device_address != null) {

			pairDevice.setText(Application.device_address);

		}

		blueName.setText(getSharedPreferences(BLUETOOTH, MODE_PRIVATE).getString(KEY_PRE, DEFAULT_NAME));

		if (Application.pin_value != null) {

			pinValue.setText(Application.pin_value);

		}

		fouces.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				fouces.setFocusable(true);

				fouces.setFocusableInTouchMode(true);

				fouces.requestFocus();

				return false;

			}

		});

		((TextView) findViewById(R.id.version)).setText(getSharedPreferences(BLUETOOTH, MODE_PRIVATE).getString(VERSION_KEY, null));

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		unbindService(conn);

		unregisterReceiver(receiver);
	//	overridePendingTransition(R.anim.fade, R.anim.hold);

	}

	@Override
	public void onClick(View v) {

		v.setFocusable(true);

		v.setFocusableInTouchMode(true);

		v.requestFocus();

		switch (v.getId()) {

		case R.id.duankai:

			if (mService != null)

				mService.sendCommand(DISCONNECT_COMMAND);

			break;

		case R.id.xiugai:

			if (mService != null) {

				mService.sendCommand("AT#MM" + blueName.getText().toString().trim() + "\r\n");

				mService.sendCommand(AT_MM, 500);

			}

			break;

		case R.id.pin:

			if (mService != null) {

				mService.sendCommand("AT#MN" + pinValue.getText().toString().trim() + "\r\n");

				mService.sendCommand(QUERY_PIN_COMMAND, 500);

			}

			break;

		case R.id.back:

			finish();

			break;

		case R.id.home:

			finish();

			break;

		default:

			break;

		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		buttonView.setFocusable(true);
		buttonView.setFocusableInTouchMode(true);
		buttonView.requestFocus();
		if (mService != null) {
			if (buttonView == callToggle) {
				
				mService.sendCommand(isChecked ? AUTO_CALL_COMMAND : UNAUTO_CALL_COMMAND);
				
				mService.sendCommand(QUERY_AUTO_COMMAND, 2000);
				
			} else if (buttonView == connToggle) {
				
				mService.sendCommand(isChecked ? AUTO_CONNECT_COMMAND : UNAUTO_CONNECT_COMMAND);
				
				mService.sendCommand(QUERY_AUTO_COMMAND, 2000);
				
			} 
			  /*
			  	else if (buttonView == a2dpToggle) {
			  	mService.sendCommand(isChecked ? A2DP_OPEN : A2DP_CLOSE); }
			  */
		}

	}
	
}
