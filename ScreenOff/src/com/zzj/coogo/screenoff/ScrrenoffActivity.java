package com.zzj.coogo.screenoff;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ScrrenoffActivity extends Activity implements OnClickListener,BrightnessListening {

	private static final String ACTION_QUITE_SCREENOFF = "action.coogo.QUITE_SCREENOFF";
	public static final String CUSTIOM_002 = "002";
	public static final String CUSTIOM_001 = "001";
	public static final String CUSTIOM_007 = "007";
	public static final String CUSTIOM_003 = "003";

	private static final String COM_WANMA_ACTION_MAIN_INFO = "com.wanma.action.MAIN_INFO";

	TextView currentRoad, nextRoad, distanceText, remainDistanceText;

	ImageView maneuverImage;
	// ImageView naviButton;

	LEDView ledView;
	View layout;

	WakeLock wl;

	private BNRBroadCast mScreenOffReceiver;

	private static Boolean isExit = false;

	static ScrrenoffActivity screen;
	public static TextView edog_state;
	public static final String CMDOPEN_DESK_LYRIC = "open_desk_lyric";// 打开桌面歌词
	public static final String CMDCLOSE_DESK_LYRIC = "close_desk_lyric";// 关闭桌面歌词
	String MOFANG_PKG = "com.coogo.inet.vui.assistant.car";
	String WINDOW_SERVICE_CLASS = "com.android.kwmusic.KWMusicService";
	String content = "";

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (COM_WANMA_ACTION_MAIN_INFO.equals(action)) {
				content = intent.getStringExtra("content");
				edog_state.setText(content);
			}else if(ACTION_QUITE_SCREENOFF.equals(action)){
				try {
					ScrrenoffActivity.this.finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// No Titlebar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION  
                | View.SYSTEM_UI_FLAG_IMMERSIVE 
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new  OnSystemUiVisibilityChangeListener() {
			
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_HIDE_NAVIGATION  
		                  | View.SYSTEM_UI_FLAG_IMMERSIVE
		                  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
		                  | View.SYSTEM_UI_FLAG_FULLSCREEN);
				
			}
		});
		super.onCreate(savedInstanceState);
		if(CUSTIOM_003.equals(MainApplication.custom_code)){
			setContentView(R.layout.screen_off_003);
		}else{
			setContentView(R.layout.screen_off);
		}
		screen = this;
		initView();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Gank");
		if (BNRService.isnavi || MainApplication.gaodeisnavi) {
			ledView.setVisibility(View.GONE);
			layout.setVisibility(View.VISIBLE);
			// naviButton.setVisibility(View.VISIBLE);
		} else {
			ledView.setVisibility(View.VISIBLE);
			layout.setVisibility(View.GONE);
			// naviButton.setVisibility(View.GONE);
		}

		MainApplication.mScreenOff=false;
		registReceive();
	}

	private void initView() {
		findViewById(R.id.screen).setOnClickListener(this);
		layout = findViewById(R.id.toast_layout_root);
		edog_state = (TextView) findViewById(R.id.edog_state);
		final Typeface font = Typeface.createFromAsset(getAssets(),LEDView.FONT_DIGITAL_7);
		edog_state.setTypeface(font);// 设置字体
		edog_state.setMovementMethod(new ScrollingMovementMethod());
		/*
		 * naviButton = (ImageView) findViewById(R.id.imageView1);
		 * naviButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { if (ledView.getVisibility()
		 * == View.VISIBLE) { ledView.setVisibility(View.GONE);
		 * layout.setVisibility(View.VISIBLE); } else {
		 * ledView.setVisibility(View.VISIBLE); layout.setVisibility(View.GONE);
		 * }
		 * 
		 * } });
		 */
		ledView = (LEDView) findViewById(R.id.ledview);
		ledView.setOnDateListening(this);
		ledView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		//		if (BNRService.isnavi) {
					ledView.setVisibility(View.GONE);
					layout.setVisibility(View.VISIBLE);
		//		}
			}
		});

		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ledView.setVisibility(View.VISIBLE);
				layout.setVisibility(View.GONE);
			}
		});
		nextRoad = (TextView) findViewById(R.id.nextRoad);
		currentRoad = (TextView) findViewById(R.id.currentRoad);
		distanceText = (TextView) findViewById(R.id.distance);
		remainDistanceText = (TextView) findViewById(R.id.remainDistance);
		maneuverImage = (ImageView) findViewById(R.id.imageView);

	}

	@Override
	protected void onResume() {
		wl.acquire();
		ledView.start();
	//	if (CUSTIOM_002.equals(MainApplication.custom_code)) {
			ComponentName name = new ComponentName(MOFANG_PKG,WINDOW_SERVICE_CLASS);
			Intent intent = new Intent();
			intent.setComponent(name);
			intent.setAction(CMDOPEN_DESK_LYRIC);
			startService(intent);
	//	}else {
	/*		ComponentName name = new ComponentName(MOFANG_PKG,WINDOW_SERVICE_CLASS);
			Intent intent = new Intent();
			intent.setComponent(name);
			intent.setAction(CMDCLOSE_DESK_LYRIC);
			startService(intent);
		}*/
			super.onResume();
	}

	private void registReceive() {
		final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		mScreenOffReceiver = new BNRBroadCast();
		registerReceiver(mScreenOffReceiver, homeFilter);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_QUITE_SCREENOFF);
		if (CUSTIOM_002.equals(MainApplication.custom_code)){
			filter.addAction(COM_WANMA_ACTION_MAIN_INFO);
		}
		registerReceiver(receiver, filter);
		
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		wl.release();
		ledView.stop();
		ComponentName name = new ComponentName(MOFANG_PKG, WINDOW_SERVICE_CLASS);
		Intent intent = new Intent();
		intent.setComponent(name);
		intent.setAction(CMDCLOSE_DESK_LYRIC);
		startService(intent);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		screen = null;
		unregisterReceiver(mScreenOffReceiver);
		MainApplication.getHander().removeMessages(1);
		if (MainApplication.acc_state && MainApplication.time > 0 && !MainApplication.mScreenOff)
			MainApplication.getHander().sendEmptyMessageDelayed(1,MainApplication.time * 1000);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

		if (isExit) {
			finish();
		} else {
			isExit = true;
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME || keyCode == 66 || keyCode == 86) {
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
 
	@Override
	public void dateListening(int i) {
		WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
		localLayoutParams.screenBrightness = i / 255.0F;
		getWindow().setAttributes(localLayoutParams);
	}

	@SuppressLint("NewApi")
	private void text() {
		setImmersive(true);
	}
}
