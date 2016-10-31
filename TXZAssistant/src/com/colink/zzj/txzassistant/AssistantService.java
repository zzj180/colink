package com.colink.zzj.txzassistant;

import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.kwmusic.CommandPreference;
import com.android.kwmusic.KWMusicService;
import com.colink.zzj.txzassistant.node.CustomAsAsrNode;
import com.colink.zzj.txzassistant.oem.PreferenceHelper;
import com.colink.zzj.txzassistant.oem.RomSystemSetting;
import com.colink.zzj.txzassistant.util.APPUtil;
import com.colink.zzj.txzassistant.util.Constants;
import com.colink.zzj.txzassistant.util.Gps;
import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.PositionUtil;
import com.colink.zzj.txzassistant.util.SystemPropertiesProxy;
import com.colink.zzj.txzassistant.util.UserPerferenceUtil;
import com.colink.zzj.txzassistant.util.UserPreference;
import com.colink.zzj.txzassistant.vendor.BDDH.BDDHOperate;
import com.colink.zzj.txzassistant.vendor.GD.GDOperate;
import com.colink.zzj.txzassistant.vendor.KLD.KLDOperate;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZPowerManager;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZConfigManager.FloatToolType;

/**
 * @desc 语音助手服务
 * @auth zzj
 * @date 2016-03-19
 */
public class AssistantService extends Service implements Constants{

	private static final String TTS_SHOW = "tts_show";
	private static final String TXZ_SHOW = "com.txznet.txz.record.show";
	private static final String TXZ_DISMISS = "com.txznet.txz.record.dismiss";
	private static final String ACTION_NO_DISTURB = "com.inet.broadcast.no_disturb";
	private static final String ACTION_START_TALK = "cn.yunzhisheng.intent.action.START_TALK";
	private String MAP_INDEX = "MAP_INDEX";
	// private String ACC_STATE = "acc_state";
	public static final String ACTION_SET_WAKEUP = "com.unisound.unicar.gui.ACTION.SET_WAKEUP";
	public static final String ACTION_SET_TTSSPEED = "com.unisound.unicar.gui.ACTION.SET_TTSSPEED";

	public static final String ACTION_UPDATE_WAKEUP_WORD = "com.unisound.unicar.gui.ACTION.UPDATE_WAKEUP_WORD";
	public static final String EXTRA_KEY_WAKEUP_WORD = "WAKEUP_WORD";
	private static final String PLAY_TTS = "com.wanma.action.PLAY_TTS";
	private String ACC_STATE = "acc_state";

	public static final String KEY_PLATFORM = "ro.os.version";

	private static final String CAMERA_ACTIVITY = "com.android.camera.CameraActivity";
	private static final String CAMERA_LAUNCHER = "com.android.camera.CameraLauncher";
	private static final String GAODE_MAP_ACTIVITY = "com.autonavi.map.activity.NewMapActivity";
	private static final String BAIDU_NAVI_ACTIVITY = "com.baidu.navi.NaviActivity";
	private static final String GAODE_CAR_ACTIVITY = "com.autonavi.auto.MainMapActivity";
	
	public static final String BACK_CAR_STATE = "back_car_state";
	/*
	 * private static final String GOOGLE_MAP_APP =
	 * "com.google.android.apps.maps"; private static final String
	 * GOOGLE_MAP_ACTIVITY = "com.google.android.maps.MapsActivity";
	 */
	private static final int MIN_VOLUME = 0;

	private String curActivity;
	private boolean isShow;

	private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_NO_DISTURB.equals(action)) {
				try {
					TXZResourceManager.getInstance().dissmissRecordWin();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (TXZ_SHOW.equals(action)) {
				isShow = true;
				Settings.System.putInt(getContentResolver(), TTS_SHOW, 1);
		//		CustomAsAsrNode.getInstance().recover();
				context.sendBroadcast(new Intent("action.coogo.QUITE_SCREENOFF"));
				getCurActivity();
				boolean isCamera = isConsumeActivity();
				if (isCamera) {
					goHomePage();
				}
			} else if (TXZ_DISMISS.equals(action)) {
				isShow = false;
				Settings.System.putInt(getContentResolver(), TTS_SHOW, 0);
		//		CustomAsAsrNode.getInstance().useWakeupAsAsr();
				/*if (CAMERA_ACTIVITY.equals(curActivity)
						|| CAMERA_LAUNCHER.equals(curActivity)) {
					try {
						APPUtil.lanchApp(getApplicationContext(),APPUtil.CAMERA_PKG);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else */
				if(!isBackCar){
					if (BAIDU_NAVI_ACTIVITY.equals(curActivity)) {
						try {
							APPUtil.lanchApp(getApplicationContext(),APPUtil.BD_NAVI_PKG);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (GAODE_MAP_ACTIVITY.equals(curActivity)) {
						try {
							APPUtil.lanchApp(getApplicationContext(),APPUtil.GD_MAP_PKG);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if (GAODE_CAR_ACTIVITY.equals(curActivity)) {
						if (APPUtil.getInstance().isInstalled(APPUtil.GD_CARJ_PKG)) {
							try {
								APPUtil.lanchApp(getApplicationContext(), APPUtil.GD_CARJ_PKG);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else if (APPUtil.getInstance().isInstalled(APPUtil.GD_CAR_PKG)) {
							try {
								APPUtil.lanchApp(getApplicationContext(), APPUtil.GD_CAR_PKG);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			} else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
				goHomePage();
			}

		}
	};
	private UserPreference mUserPreference;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		mUserPreference = new UserPreference(getApplicationContext());
		AdapterApplication.mapType = getMapType();
		Settings.System.putInt(getContentResolver(), TTS_SHOW, 0);
		startKWMusicService();

		getContentResolver().registerContentObserver(
				Settings.System.getUriFor(MAP_INDEX), false,
				new ContentObserver(AdapterApplication.uiHandler) {
					@Override
					public void onChange(boolean selfChange) {
						AdapterApplication.mapType = getMapType();
						Logger.d("mapType = " + AdapterApplication.mapType);
						if(AdapterApplication.mapType==0){
							GDOperate.getInstance(getApplicationContext()).closeMap();
						}else if(AdapterApplication.mapType == 1){
							BDDHOperate.getInstance(getApplicationContext()).closeMap();
						}
					}
				});

		txzACC();
		getContentResolver().registerContentObserver(
				Settings.System.getUriFor(ACC_STATE), true,
				new ContentObserver(new Handler()) {
					@Override
					public void onChange(boolean selfChange) {
						txzACC();
					}
				});
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(TXZ_SHOW);
		filter.addAction(TXZ_DISMISS);
		filter.addAction(ACTION_NO_DISTURB);
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(mScreenOffReceiver, filter);
		
		isBackCar();
		getContentResolver().registerContentObserver(Settings.System.getUriFor(BACK_CAR_STATE), true,
				new ContentObserver(new Handler(getMainLooper())) {
					@Override
					public void onChange(boolean selfChange) {
						isBackCar();
					}

				});

	}
	boolean isBackCar;
	private void isBackCar() {
		isBackCar = Settings.System.getInt(getContentResolver(),BACK_CAR_STATE,0) == 1;
		Logger.d("后录="+isBackCar);
		if(isBackCar){
			TXZResourceManager.getInstance().dissmissRecordWin();
		}
	}
	
	private void startKWMusicService() {
		Intent intentMusic = new Intent(this, KWMusicService.class);
		this.startService(intentMusic);
	}

	private int getMapType() {
		return Settings.System.getInt(getContentResolver(), MAP_INDEX, 0);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null) {
			String action = intent.getAction();
			Logger.d(action + isShow);
			if (ACTION_START_TALK.equals(action)) {
				if (TXZConfigManager.getInstance().isInitedSuccess()) {
					TXZAsrManager.getInstance().triggerRecordButton();
					/*
					 * if(isShow){ TXZAsrManager.getInstance().cancel(); }else{
					 * TXZAsrManager.getInstance().start("有什么可以帮您"); }
					 */
				}
			} else if (BootReceiver.ACTION_START_NAVI.equals(action)) {
				CreateDialog(this);
			}
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mScreenOffReceiver);
	}

	private void txzACC() {
		AdapterApplication.mAcc = Settings.System.getInt(getContentResolver(),
				ACC_STATE, 1) == 1;
		UserPerferenceUtil.setWakeupEnable(AdapterApplication.getApp(),
				AdapterApplication.mAcc);
		Logger.d("acc=" + AdapterApplication.mAcc);
		if (AdapterApplication.mAcc) {
			int storageVolume = PreferenceHelper.getInstance().getVolume();
			if (storageVolume > MIN_VOLUME) {
				String platform = SystemPropertiesProxy.get(this, KEY_PLATFORM);
				if (TextUtils.isEmpty(platform)) {
					Logger.d("storageVolume = " + storageVolume);
					AudioManager am = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
					am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,storageVolume, 0);
					am.setStreamVolume(AudioManager.STREAM_ALARM,storageVolume, 0);
					am.setStreamVolume(AudioManager.STREAM_MUSIC,storageVolume * 2, 0);
				} else {
					/*
					 * AudioManager am = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
					 * am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,storageVolume, 0);
					 * am.setStreamVolume(AudioManager.STREAM_ALARM,storageVolume, 0);
					 * am.setStreamVolume(AudioManager.STREAM_MUSIC,storageVolume, 0);
					 */
				}
				PreferenceHelper.getInstance().setVolume(MIN_VOLUME);
			}
			if (mUserPreference.getFloat("lat", 0f) != 0f && mUserPreference.getFloat("lng", 0f) != 0f) {
				CreateDialog(this);
			}
			TXZPowerManager.getInstance().reinitTXZ(new Runnable() {
				@Override
				public void run() {
					TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);
					startService(new Intent(getApplicationContext(), PhoneBookService.class));
					// TXZPowerManager.getInstance().reinitTXZ();
				}
			});
		} else {
			if (PreferenceHelper.getInstance().getVolume() == MIN_VOLUME) {
				String platform = SystemPropertiesProxy.get(this, KEY_PLATFORM);
				if (TextUtils.isEmpty(platform)) {
					AudioManager am = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
					int curValue = am.getStreamVolume(AudioManager.STREAM_ALARM);
					am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,MIN_VOLUME, 0);
					am.setStreamVolume(AudioManager.STREAM_ALARM, MIN_VOLUME, 0);
					am.setStreamVolume(AudioManager.STREAM_MUSIC, MIN_VOLUME, 0);
					PreferenceHelper.getInstance().setVolume(curValue);
				}
			}
			Intent stop = new Intent(this,KWMusicService.class);
			stop.putExtra(CommandPreference.CMDNAME,CommandPreference.CMDSTOP);
			startService(stop);
			
		/*	 Set<String> words = AdapterApplication.getContext().getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE).getStringSet(KEY_PRE_WAKEUPWORDS, null);
			String[] strSet = new String[words.size()];
			words.toArray(strSet);
			TXZConfigManager.getInstance().setWakeupKeywordsNew(strSet);
			boolean micEnable = UserPerferenceUtil.getFloatMicEnable(getApplicationContext());
	//		TXZTtsManager.getInstance().setVoiceSpeed(UserPerferenceUtil.getTTSSpeed(getApplicationContext()));
			TXZConfigManager.getInstance().showFloatTool(micEnable ? FloatToolType.FLOAT_NORMAL : FloatToolType.FLOAT_NONE);
			TXZConfigManager.getInstance().setWakeupThreshhold(UserPerferenceUtil.getWakeupThreshold(getApplicationContext()));
			TXZConfigManager.getInstance().enableWakeup(UserPerferenceUtil.getAECEnable(getApplicationContext()));*/
			TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
			TXZPowerManager.getInstance().releaseTXZ();
			Intent intent = new Intent("AUTONAVI_STANDARD_BROADCAST_RECV");
			intent.putExtra("KEY_TYPE", 10018);
			sendBroadcast(intent);
			GDOperate.getInstance(getApplicationContext()).closeMap();
			AdapterApplication.runOnUiGround(new Runnable() {
				@Override
				public void run() {
					try {
						
						RomSystemSetting.forceStopPackage(APPUtil.IMUSIC_PKG);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 1000);

		}
	}

	AlertDialog mAlertDialog;
	Button button1, button2;

	/**
	 * by ZZJ
	 */
	@SuppressLint("InflateParams")
	public void CreateDialog(Context context) {
		if (mAlertDialog != null) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.prepare_navi, null);
		button1 = (Button) v.findViewById(R.id.button1);
		button2 = (Button) v.findViewById(R.id.button2);
		Builder builder = new AlertDialog.Builder(context);
		button1.setOnClickListener(l);
		button2.setOnClickListener(l);
		String address = mUserPreference.getString("address",getString(R.string.prepnavi_address));
		((TextView) v.findViewById(R.id.textView1)).setText(getString(R.string.prepnavi_endpoi, address));
		builder.setView(v);
		Intent intent = new Intent(PLAY_TTS);
		String content = getString(R.string.receive_prepnavi_command);

		if (getString(R.string.prepnavi_address).equals(address)) {
		} else {
			String customer = SystemPropertiesProxy.get(this,"ro.inet.consumer.code");
			if ("003".equals(customer)) {
				content = content + ",目的地是" + address;
			} else {
			}
		}

		intent.putExtra("content", content);
		context.sendBroadcast(intent);
		mAlertDialog = builder.create();
		mAlertDialog.setCanceledOnTouchOutside(false);
		mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);// 显示
		mAlertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		CountDown();
		mAlertDialog.show();

		mAlertDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				mAlertDialog = null;
				mUserPreference.putFloat("lat", 0f);
				mUserPreference.putFloat("lng", 0f);
				mUserPreference.putString("address", "");
				if (mCountDownTimer != null) {
					mCountDownTimer.cancel();
					mCountDownTimer = null;
				}
			}
		});
	}

	/**
	 * by ZZJ add
	 */
	OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				if (mAlertDialog != null) {
					startNavi();
				}
				break;
			default:
				if (mAlertDialog != null) {
					mUserPreference.putFloat("lat", 0f);
					mUserPreference.putFloat("lng", 0f);
					mUserPreference.putString("address", "");
					mAlertDialog.dismiss();
				}
				break;
			}

		}
	};

	/**
	 * by ZZJ
	 */
	CountDownTimer mCountDownTimer;

	private void CountDown() {
		if (mCountDownTimer != null) {
			return;
		}

		mCountDownTimer = new CountDownTimer(10000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				button1.setText(getString(R.string.cancel_prepnavi,
						millisUntilFinished / 1000));
			}

			@Override
			public void onFinish() {
				if (mAlertDialog != null) {
					startNavi();
				}
			}
		};
		mCountDownTimer.start();
	}

	/**
	 * by ZZJ
	 */
	private void startNavi() {
		Gps gcj02 = PositionUtil.bd09_To_Gcj02(
				mUserPreference.getFloat("lat", 0f),
				mUserPreference.getFloat("lng", 0f));
		startNavigation(gcj02.getWgLat(), gcj02.getWgLon(),
				mUserPreference.getString("address", ""));
		mUserPreference.putFloat("lat", 0f);
		mUserPreference.putFloat("lng", 0f);
		mUserPreference.putString("address", "");
		mAlertDialog.dismiss();
	}

	private void startNavigation(double lat, double lon, String name) {
		if (AdapterApplication.mapType == 1) {// 高德
			GDOperate.getInstance(this).startNavigation(lat, lon);
		} else if (AdapterApplication.mapType == 2) {
			KLDOperate.getInstance(this).startNavigation(lat, lon, name);
		} else if (AdapterApplication.mapType == 0) {// 百度导航开始导航
			Gps bd09 = PositionUtil.gcj02_To_Bd09(lat, lon);
			BDDHOperate.getInstance(this).startNavigation(bd09.getWgLat(),bd09.getWgLon());
		}
	}

	public boolean isConsumeActivity() {
		if (GAODE_MAP_ACTIVITY.equals(curActivity) || GAODE_CAR_ACTIVITY.equals(curActivity)
				|| BAIDU_NAVI_ACTIVITY.equals(curActivity)) {
			return true;
		}
		return false;
	}

	void goHomePage() {
		Intent mHomeIntent = new Intent(Intent.ACTION_MAIN, null);
		mHomeIntent.addCategory(Intent.CATEGORY_HOME);
		mHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(mHomeIntent);
	}

	@SuppressWarnings("deprecation")
	private void getCurActivity() {
		curActivity = null;
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		// 应用程序位于堆栈的顶层
		if (tasksInfo != null) {
			ComponentName name = tasksInfo.get(0).topActivity;
			if (name != null) {
				curActivity = name.getClassName();
				Logger.d(curActivity);
			}
		}
	}
}
