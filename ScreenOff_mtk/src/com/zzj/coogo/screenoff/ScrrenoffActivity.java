package com.zzj.coogo.screenoff;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.spreada.utils.chinese.ZHConverter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ScrrenoffActivity extends Activity implements OnClickListener,
		BrightnessListening {

	private static final String ACTION_QUITE_SCREENOFF = "action.coogo.QUITE_SCREENOFF";
	public static final String CUSTIOM_002 = "002";
	public static final String CUSTIOM_001 = "001";
	public static final String CUSTIOM_007 = "007";
	public static final String CUSTIOM_003 = "003";
	public static final String CUSTIOM_006 = "006";
	private static final String GMT_8 = "GMT+8";
	private final static long MILLIS_IN_DAY = 86400000;
	private static final String COM_WANMA_ACTION_MAIN_INFO = "com.wanma.action.MAIN_INFO";

	private TextView nextRoad, distanceText, remainDistanceText;

	private ImageView maneuverImage;
	// ImageView naviButton;

	private LEDView ledView;
	private View layout;

	// WakeLock wl;

	private BNRBroadCast mScreenOffReceiver;

	private Boolean isExit = false;

	static ScrrenoffActivity screen;
	private TextView edog_state;
	private String CMDOPEN_DESK_LYRIC = "cn.kuwo.kwmusicauto.action.OPEN_DESKLYRIC";// 打开桌面歌词
	private String CMDCLOSE_DESK_LYRIC = "cn.kuwo.kwmusicauto.action.CLOSE_DESKLYRIC";// 关闭桌面歌词
//	private String MOFANG_PKG = "com.coogo.inet.vui.assistant.car";
//	private String WINDOW_SERVICE_CLASS = "com.android.kwmusic.KWMusicService";
//	String TXZ_PKG = "com.colink.zzj.txzassistant";

	private final String MANEUVER_IMAGE = "maneuver_Image";
	private final String NEXT_ROAD = "next_roadName";
	private final String NEXT_ROAD_DISTANCE = "next_road_distance";
	private final String TOTAL_REMAIN_TIME = "total_remain_time";
	private final String TOTAL_REMAIN_DISTANCE = "total_remain_distance";

	private  final String ISNAVING = "is_naving";
	
	private boolean isNaviing = false;
	private  final Uri uri_navi = Uri.parse("content://com.zzj.softwareservice.NaviProvider/navi");

	String content = "";
	private boolean isTW;
	boolean isCamera;

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (COM_WANMA_ACTION_MAIN_INFO.equals(action)) {
				content = intent.getStringExtra("content");
				edog_state.setText(content);
			} else if (ACTION_QUITE_SCREENOFF.equals(action)) {
				try {
					exit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	private Cursor query;

	private ContentObserver observernew = new ContentObserver(new Handler()) {

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			// TODO Auto-generated method stub
			super.onChange(selfChange, uri);
			getNaviInfo(uri);
		}

	};

	private void getNaviInfo(Uri uri) {
		try {
			query = getContentResolver().query(uri, null, null, null, null);
			if (query.moveToNext()) {
				isNaviing = query.getInt(query.getColumnIndex(ISNAVING)) == 1;
				if (isNaviing) {

					ledView.setVisibility(View.GONE);
					layout.setVisibility(View.VISIBLE);
					ledView.stop();
					String maneuver_Image = query.getString(query
							.getColumnIndex(MANEUVER_IMAGE));
					String next_road = query.getString(query
							.getColumnIndex(NEXT_ROAD));
					int next_road_distance = query.getInt(query
							.getColumnIndex(NEXT_ROAD_DISTANCE));
					int total_distance = query.getInt(query
							.getColumnIndex(TOTAL_REMAIN_DISTANCE));
					int remainTime = query.getInt(query
							.getColumnIndex(TOTAL_REMAIN_TIME));

					if (isTW) {
						nextRoad.setText(ZHConverter.convert(next_road,
								ZHConverter.TRADITIONAL));
					} else {
						nextRoad.setText(next_road);
					}
					remainDistanceText.setText(getRemainDidistance(total_distance,
									System.currentTimeMillis() + remainTime* 1000));

					int resID = getResources().getIdentifier(maneuver_Image,
							"drawable", getApplicationInfo().packageName);
					maneuverImage.setBackgroundResource(resID);
					distanceText.setText(getDidistance(next_road_distance));
				} else {
					ledView.setVisibility(View.VISIBLE);
					layout.setVisibility(View.GONE);
					ledView.start();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (query != null)
				query.close();
		}
	}

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
		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
				new OnSystemUiVisibilityChangeListener() {

					@Override
					public void onSystemUiVisibilityChange(int visibility) {
						if (visibility == View.VISIBLE) {
							getWindow()
									.getDecorView()
									.setSystemUiVisibility(
											View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
													| View.SYSTEM_UI_FLAG_IMMERSIVE
													| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
													| View.SYSTEM_UI_FLAG_FULLSCREEN);
						}

					}
				});
		super.onCreate(savedInstanceState);
		/*
		 * if(CUSTIOM_003.equals(MainApplication.custom_code)){
		 * setContentView(R.layout.screen_off_003); }else
		 * if(CUSTIOM_006.equals(MainApplication.custom_code)){
		 * setContentView(R.layout.screen_off_006); }else{
		 * setContentView(R.layout.screen_off); }
		 */
		setContentView(R.layout.screen_off);
		screen = this;
		Intent intent = getIntent();
		if (intent != null) {
			isCamera = intent.getBooleanExtra("isCamera", false);
		} else {
			isCamera = false;
		}
		/*
		 * Locale locale = getResources().getConfiguration().locale; if
		 * ("TW".endsWith(locale.getCountry())) { isTW = true; } else { isTW =
		 * false; }
		 */

		initView();
		/*
		 * PowerManager pm = (PowerManager)
		 * getSystemService(Context.POWER_SERVICE); =
		 * pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
		 * PowerManager.ACQUIRE_CAUSES_WAKEUP, "Gank");
		 */

		MainApplication.mScreenOff = false;
		registReceive();
	}

	private void initView() {
		findViewById(R.id.screen).setOnClickListener(this);
		layout = findViewById(R.id.toast_layout_root);
		edog_state = (TextView) findViewById(R.id.edog_state);
		// edog_state.setMovementMethod(new ScrollingMovementMethod());
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
		/*
		 * ledView.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // if (BNRService.isnavi) {
		 * ledView.setVisibility(View.GONE); layout.setVisibility(View.VISIBLE);
		 * ledView.stop(); // } } });
		 * 
		 * layout.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * ledView.setVisibility(View.VISIBLE); layout.setVisibility(View.GONE);
		 * ledView.start(); } });
		 */
		nextRoad = (TextView) findViewById(R.id.nextRoad);
		// currentRoad = (TextView) findViewById(R.id.currentRoad);
		distanceText = (TextView) findViewById(R.id.distance);
		remainDistanceText = (TextView) findViewById(R.id.remainDistance);
		maneuverImage = (ImageView) findViewById(R.id.imageView);
	}

	@Override
	protected void onResume() {

		// wl.acquire();
		//免打扰歌词开关
		int lrc_show = Settings.System.getInt(getApplicationContext().getContentResolver(), "screen_off_lrc_switch", 0);
		Log.d("screen", "lrc = " + lrc_show);
		if (lrc_show == 0) {
			/*ComponentName name = new ComponentName(MOFANG_PKG,
					WINDOW_SERVICE_CLASS);
			Intent intent = new Intent();
			intent.setComponent(name);
			intent.setAction(CMDOPEN_DESK_LYRIC);
			try {
				startService(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ComponentName txzname = new ComponentName(TXZ_PKG,WINDOW_SERVICE_CLASS);
			Intent txz = new Intent();
			txz.setComponent(txzname);
			txz.setAction(CMDOPEN_DESK_LYRIC);
			try {
				startService(txz);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			sendBroadcast(new Intent(CMDOPEN_DESK_LYRIC));

		}
		
		sendBroadcast(new Intent(Constant.NO_DISTURB_ACTION));
		getNaviInfo(uri_navi);
		super.onResume();
		getContentResolver().registerContentObserver(uri_navi, false,observernew);
	}

	private void registReceive() {
		final IntentFilter homeFilter = new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		mScreenOffReceiver = new BNRBroadCast();
		registerReceiver(mScreenOffReceiver, homeFilter);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_QUITE_SCREENOFF);
		if (CUSTIOM_002.equals(MainApplication.custom_code)) {
			filter.addAction(COM_WANMA_ACTION_MAIN_INFO);
		}
		registerReceiver(receiver, filter);
		/*
		 * guidanceReceiver =new NaviGuidanceReceiver(); IntentFilter filter2 =
		 * new IntentFilter("CLD.NAVI.MSG.GUIDANCEINFO");
		 * registerReceiver(guidanceReceiver, filter2);
		 */
	}

	// NaviGuidanceReceiver guidanceReceiver;
	@Override
	protected void onStop() {
		super.onStop();
		getContentResolver().unregisterContentObserver(observernew);
	}

	@Override
	protected void onPause() {
		// wl.release();
		ledView.stop();
		/*ComponentName name = new ComponentName(MOFANG_PKG, WINDOW_SERVICE_CLASS);
		Intent intent = new Intent();
		intent.setComponent(name);
		intent.setAction(CMDCLOSE_DESK_LYRIC);
		try {
			startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ComponentName txzname = new ComponentName(TXZ_PKG, WINDOW_SERVICE_CLASS);
		Intent txz = new Intent();
		txz.setComponent(txzname);
		txz.setAction(CMDCLOSE_DESK_LYRIC);
		try {
			startService(txz);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		sendBroadcast(new Intent(CMDCLOSE_DESK_LYRIC));
		sendBroadcast(new Intent("com.intent.action.LEAVE_NODISTURB"));
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		// unregisterReceiver(guidanceReceiver);
		screen = null;
		unregisterReceiver(mScreenOffReceiver);
		if (MainApplication.acc_state && !MainApplication.mScreenOff)
			MainApplication.app.goNoDiturb();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

		if (isExit) {
			exit();
		} else {
			isExit = true;
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 3000);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME || keyCode == 66
				|| keyCode == 86) {
			exit();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	void exit() {
		if (isCamera) {
			goHomePage();
		}
		finish();
	}

	@Override
	public void dateListening(int i) {
		WindowManager.LayoutParams localLayoutParams = getWindow()
				.getAttributes();
		localLayoutParams.screenBrightness = i / 255.0F;
		getWindow().setAttributes(localLayoutParams);
	}

	@SuppressLint("NewApi")
	private void text() {
		setImmersive(true);
	}

	void goHomePage() {
		Intent mHomeIntent = new Intent(Intent.ACTION_MAIN, null);
		mHomeIntent.addCategory(Intent.CATEGORY_HOME);
		mHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(mHomeIntent);
	}

	private String getDidistance(int s) {
		String dis;
		if (s < 1000) {
			dis = String.format(
					ScrrenoffActivity.screen.getString(R.string.mi), s);
		} else if (s < 100000) {
			dis = String.format(
					ScrrenoffActivity.screen.getString(R.string.gl),
					(float) s * 1.0 / 1000);
		} else {
			dis = String.format(
					ScrrenoffActivity.screen.getString(R.string.gl_far),
					s / 1000);
		}

		return dis;
	}

	private String getRemainDidistance(int s, long time) {
		long nowTime = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(GMT_8));
		calendar.setTimeInMillis(time);
		int l = (int) (toDay(time) - toDay(nowTime));
		String d;
		switch (l) {
		case 0:
			d = String.format(LEDView.DATE_FORMAT,
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE));
			break;
		case 1:
			d = "明天";
			break;
		case 2:
			d = "后天";
			break;
		default:
			d = l + "天后";
			break;
		}
		String dis;
		if (s < 1000) {
			dis = String.format(
					ScrrenoffActivity.screen.getString(R.string.arrive_mi), s,
					d);
		} else if (s < 1000000) {

			dis = String.format(
					ScrrenoffActivity.screen.getString(R.string.arrive_gl),
					(float) s * 1.0 / 1000, d);
		} else {
			dis = String.format(
					ScrrenoffActivity.screen.getString(R.string.arrive_gl_far),
					s / 1000, d);
		}

		return dis;
	}

	private long toDay(long millis) {
		return (millis + TimeZone.getDefault().getOffset(millis))
				/ MILLIS_IN_DAY;
	}

}
