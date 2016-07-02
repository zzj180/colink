/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : WindowService.java
 * @ProjectName : uniCarGUI
 * @PakageName : com.unisound.unicar.gui.ti
 * @version : 1.0
 * @Author : zzj
 * @CreateDate : 2015-6-9
 */
package cn.yunzhisheng.vui.assistant;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog.Builder;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.ContentObserver;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.common.PinyinConverter;

import com.android.kwmusic.KWMusicService;
import com.coogo.inet.vui.assistant.car.R;
import com.spreada.utils.chinese.ZHConverter;
import com.unisound.unicar.framework.service.IMessageRouterCallback;
import com.unisound.unicar.framework.service.IMessageRouterService;
import com.unisound.unicar.gui.data.interfaces.IBaseListener;
import com.unisound.unicar.gui.data.operation.AppsDataModel;
import com.unisound.unicar.gui.data.operation.ContactDataModel;
import com.unisound.unicar.gui.data.operation.MediaDataModel;
import com.unisound.unicar.gui.database.WakeUpDB;
import com.unisound.unicar.gui.database.WakeUpSwitch;
import com.unisound.unicar.gui.fm.UniDriveFmGuiService;
import com.unisound.unicar.gui.fm.XmFmGuiService;
import com.unisound.unicar.gui.location.interfaces.ILocationListener;
import com.unisound.unicar.gui.location.operation.LocationModelProxy;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.msg.ISystemCallTransitionListener;
import com.unisound.unicar.gui.msg.SystemCallTransition;
import com.unisound.unicar.gui.oem.RomCustomerProcessing;
import com.unisound.unicar.gui.preference.CommandPreference;
import com.unisound.unicar.gui.preference.Constant;
import com.unisound.unicar.gui.preference.PrivatePreference;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.preference.UserPreference;
import com.unisound.unicar.gui.route.operation.GaodeUriApi;
import com.unisound.unicar.gui.route.operation.GoogleUriApi;
import com.unisound.unicar.gui.route.operation.KLDUriApi;
import com.unisound.unicar.gui.session.GUISessionManager;
import com.unisound.unicar.gui.session.SettingSession;
import com.unisound.unicar.gui.ui.GUIMainActivity;
import com.unisound.unicar.gui.ui.MessageReceiver;
import com.unisound.unicar.gui.ui.MessageSender;
import com.unisound.unicar.gui.ui.SettingMapViewPagerActivity;
import com.unisound.unicar.gui.utils.AudioFocusHelper;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.ErrorUtil;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Gps;
import com.unisound.unicar.gui.utils.GuiProtocolUtil;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.PackageUtil;
import com.unisound.unicar.gui.utils.PositionUtil;
import com.unisound.unicar.gui.utils.StringUtil;
import com.unisound.unicar.gui.utils.TTSController;
import com.unisound.unicar.gui.view.EditLocationPopWindow;
import com.unisound.unicar.gui.view.EditLocationPopWindow.OnEditLocationPopListener;
import com.unisound.unicar.gui.view.EditWakeupWordPopWindow;
import com.unisound.unicar.gui.view.EditWakeupWordPopWindow.OnEditWakeupwordPopListener;
import com.unisound.unicar.gui.view.FloatMicView;
import com.unisound.unicar.gui.view.MicrophoneControl;
import com.unisound.unicar.gui.view.MicrophoneControl.MicrophoneViewClickListener;
import com.unisound.unicar.gui.view.MicrophoneControlDoresoView;
import com.unisound.unicar.gui.view.SessionContainer;
import com.unisound.unicar.gui.view.SessionLinearLayout;
import com.unisound.unicar.gui.view.SessionLinearLayout.DispatchKeyEventListener;
import com.unisound.unicar.gui.view.SessionLinearLayout.OnTouchEventListener;

/**
 * @Module :
 * @Comments :
 * @Author : zzj
 * @CreateDate : 2013-5-27
 * @ModifiedBy : zzj
 * @ModifiedDate: 2015-6-9
 * @Modified: 2015-6-9: GUI 3.0 update
 */
public class WindowService extends Service {
	private static final String TAG = "WindowService";
	private static final String CAMERA_ACTIVITY = "com.android.camera.CameraActivity";
	public static final String ACTION_SET_WAKEUP = "com.unisound.unicar.gui.ACTION.SET_WAKEUP";
	public static final String ACTION_SET_TTSSPEED = "com.unisound.unicar.gui.ACTION.SET_TTSSPEED";
	public static final String ACTION_SET_ONESHOT = "com.unisound.unicar.gui.ACTION.SET_ONESHOT";
	public static final String ACTION_SET_VERSION_LEVEL = "com.unisound.unicar.gui.ACTION.SET_VERSION_LEVEL";
	public static final String ACTION_SET_AEC = "com.unisound.unicar.gui.ACTION.SET_AEC";
	public static final String KEY_SET_TTSSPEED = "SET_TTSSPEED";
	public static final String ACTION_START_REQUEST_MAKE_FINISHED = "com.unisound.unicar.gui.ACTION.REQUEST_MAKE_FINISHED"; // zzj
																															// added

	public static final String ACTION_CUSTOMFROMBT = "com.unisound.unicar.customFromBt";

	/* zzj added 20151028 */
	public static final String ACTION_UPDATE_WAKEUP_WORD = "com.unisound.unicar.gui.ACTION.UPDATE_WAKEUP_WORD";
	public static final String EXTRA_KEY_WAKEUP_WORD = "WAKEUP_WORD";

	public static final String ACTION_UPDATE_LOCATION = "com.unisound.unicar.gui.ACTION.UPDATE_LOCATION";
	public static final String EXTRA_KEY_LOCATION = "LOCATION";

	public static final String EXTRA_KEY_DISMISS_FLOAT_WINDOW = "DISMISS_FLOAT_WINDOW";
	public static final int MSG_SET_STATE = 2001;
	public static final int MSG_ON_RECORDING_START = 2002;
	public static final int MSG_ON_RECORDING_STOP = 2003;
	public static final int MSG_ON_SESSION_PROTOCAL = 2004;
	public static final int MSG_IS_ASR_COMPILE_DONE = 2005;
	public static final int MSG_ON_CONTROL_WAKEUP_SUCCESS = 2006;
	public static final int MSG_ON_TTS_PLAY_END = 2007;
	public static final int MSG_ON_RECORDING_PREPARED = 2008;
	public static final int MSG_ON_RECORDING_RESULT = 2009;
	public static final int MSG_ON_RECORDING_EXCEPTION = 2010; // zzj 20150807
																// added
	public static final int MSG_ON_RECOGNIZER_TIMEOUT = 2011;
	public static final int MSG_ON_CTT_CANCEL = 2012;
	public static final int MSG_ON_ONESHOT_RECOGNIZER_TIMEOUT = 2013;
	public static final int MSG_ON_START_RECORDING_FAKE_ANIMATION = 2014; // zzj
																			// added
																			// 20151015
	public static final int MSG_ON_GET_WAKEUP_WORDS = 2015; // zzj added 20151019
	public static final int MSG_ON_CLICK_MAIN_ACTION_BUTTON = 2016;// add tyz
																	// 20151020
	public static final int MSG_ON_UPDATE_WAKEUP_WORDS_STATUS = 2017;
	public static final int MSG_ON_RECORDING_IDLE = 2018;
	public static final String ACTION_QUITE_SCREENOFF = "action.coogo.QUITE_SCREENOFF";
	/* < zzj 20150813 added Begin */
	public static final int MSG_GUI_CANCEL_SESSION = 3100;
	public static final int MSG_GUI_CANCEL_WAITTING_SESSION = 3101;
	/* zzj 20150813 added End > */

	private Point mWindowSize = new Point();
	private WindowManager mWindowManager;
	private SessionLinearLayout mViewRoot;
	private SessionContainer mSessionContainer;
	private MicrophoneControl mMicrophoneControl;
	private MicrophoneControlDoresoView mMicrophoneDoresoControl; // zzj added
																	// 20150828

	private GUISessionManager mSessionManager = null;
	private WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
	private Context mContext;
	private IMessageRouterService mMessageRouterService;
	private SystemCallTransition messageTransCenter = null;
	// private boolean isNeedRequestIsASRCompileFinished = false;

	/* < zzj 20150805 added for Mic float View Begin */
	private List<String> mLauncherPackage;
	private FloatMicView mScreeFloatView;
	private boolean mPendingStartMicChecker;
	private boolean mEnableFloatMic = false;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler()/* {s

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (mViewRoot != null && mViewRoot.isShown()) {
					RomSystemSetting.setMute(mContext);
				}
				break;

			default:
				break;
			}
		};

	}*/;

	/** is Need Hide MicFloatView for some special case */
	private boolean isNeedHideMicFloatView = false;
	/* zzj 20150805 added for Mic float View End > */

	/* < zzj 20150828 added for Music Doreso Begin */
	/**
	 * wake up success type
	 * SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS
	 * SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO
	 */
	private int mWakeupSuccessType = SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS;

	private static final String GANK = "bright";
	private static final String COM_GLSX_BOOT_ACCON = "com.glsx.boot.ACCON";
	private static final String COM_GLSX_BOOT_ACCOFF = "com.glsx.boot.ACCOFF";
	public static final String ACTION_ACC_ON = "android.intent.action.ACC_ON_KEYEVENT";
	public static final String ACTION_ACC_OFF = "android.intent.action.ACC_OFF_KEYEVENT";
	public static final String ACTION_CLOSE_WAKEUP = "android.intent.action.CLOSE_WAKEUP";
	public static final String ACTION_OPEN_WAKEUP = "android.intent.action.OPEN_WAKEUP";
	public static final String ACTION_START_TALK = "cn.yunzhisheng.intent.action.START_TALK";
	private static final String PLAY_TTS = "com.wanma.action.PLAY_TTS";
	private static final String GL_PLAY_TTS = "com.glsx.tts.speaktext";
	private static final String ACTION_REMOTE_VOICE_CONTROL = "com.inet.remote.VOICE_CONTROL";
	private static final String ACTION_CHANGE_VOLUME = "android.intent.action.CHANGE_VOLUME_STATE";
	private static final String KEY_VOLUME_VALUE = "volume_value";
	private String ACC_STATE = "acc_state";
	int curValue;
	private int id;
	public static boolean accSwitch = true;
	private boolean playTTs = true;
	public static final int MSG_ON_ACC_ON = 1988;
	public static final int MSG_ON_ACC_OFF = 1989;

	/* < zzj 20150916 added ZZJ */
	public static final String ACTION_CONTACT_DONE = "com.colink.zzj.contact.donedial";
	public static final String ACTION_CONTACT_START = "com.colink.zzj.contact.start";
	private static final String DOWNVOER = "通讯录导入结束";

	private boolean ismute;
	private MediaPlayer mPlayer;

	private AudioManager mAudioManager;
	AlertDialog mAlertDialog;
	Button button1, button2;
	UserPreference mUserPreference;
	private boolean startDownPhone;
	String playText = null; // add by ZZJ 20150909

	public static String language;

	public int getWakeupSuccessType() {
		return mWakeupSuccessType;
	}

	/* zzj 20150828 added for Music Doreso End > */

	// zzj added 20150707
	private static ArrayList<String> mOnlineSupportList = null;
	private static ArrayList<String> mOfflineSupportList = null;
	private String mSystemCallJson = "";

	private ContactDataModel contactDataModel = null;

	private MediaDataModel mediaDataModel = null;

	private AppsDataModel appsDataModel = null;

	private boolean isMainMark = false;

	private AudioFocusHelper mAudioFocusHelper = null;

	// Foreground Service
	private boolean mReflectFlg = false;
	private static final int NOTIFICATION_ID = 1; // 设置为前台service
	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
			int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	private DispatchKeyEventListener mDispatchKeyEventListener = new DispatchKeyEventListener() {

		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			Logger.d(TAG,
					"!--->dispatchKeyEvent()---keyCode = " + event.getKeyCode());
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
					&& event.getAction() == KeyEvent.ACTION_UP) {
				mSessionManager.cancelTalk();
				return true;
			} else {
				if (isMainMark) {
					return mSessionContainer.dispatchKeyEvent(event);
				} else {
					return mMicrophoneControl.dispatchKeyEvent(event);
				}

			}

		}
	};

	private OnTouchEventListener mOnTouchEventListener = new OnTouchEventListener() {

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			Logger.d(TAG, "!--->onTouchEvent()---Action = " + event.getAction());
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			if ((event.getAction() == MotionEvent.ACTION_DOWN)
					&& ((x < 0) || (x >= mViewRoot.getWidth()) || (y < 0) || (y >= mViewRoot
							.getHeight()))) {
				mSessionManager.cancelTalk();
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
				mSessionManager.cancelTalk();
				return true;
			}
			return false;
		}

	};

	private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			if (ismute) {
				updateVolumeValue(curValue);
				getAudioManager().setStreamVolume(AudioManager.STREAM_ALARM, 0,
						0);
			}
			if (mPlayer != null) {
				mPlayer.release();
			}
		}
	};

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		Logger.d(TAG, "!--->onCreate");
		super.onCreate();
		mContext = this;

		// add by zzj 20151223 for edog
		/*
		 * String str =
		 * ((TelephonyManager)getSystemService("phone")).getDeviceId(); String
		 * id= WmEncrypter.encryptByMD5(str + "PinWang"); WmEncrypter.write(new
		 * File("/mnt/sdcard/WM_Edog/pefa.bin"), id);
		 */

		initServiceForeground();
		PrivatePreference.init(mContext);
		// 初始化拼音模块
		initPinyinConverter();
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Constant.setFirstStart(this, true);// zzj 20150820 added
		UserPerferenceUtil.VALUE_VERSION_MODE_DEFAULT = com.unisound.unicar.gui.utils.SystemPropertiesProxy
				.getInt(this, "ro.coogo.voice.mode",UserPerferenceUtil.VALUE_VERSION_MODE_EXP);
		mUserPreference = new UserPreference(this);
		TTSController.getInstance(this).init();

		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		mPlayer.setOnCompletionListener(mOnCompletionListener);
		/* < zzj added 20150706 begin */
		// if (PackageUtil.isRunningForeground(WindowService.this)) {
		// mSessionManager.showWelcomeView(true);
		// }
		// startWelcomeActivity();// zzj added 20150706
		/* zzj added 20150706 end > */

		bindMessageRouterService();
		updateMapConfig();
		startKWMusicService();

		boolean isXmFmInstalled = DeviceTool.checkApkExist(mContext,
				GUIConfig.PACKAGE_NAME_XMLY_FM);

		if (isXmFmInstalled) {
			startXmFmGuiService();
		} else {
			startUniDriveFmService();
		}
		accSwitch = Settings.System.getInt(getContentResolver(),ACC_STATE, 1)==1;
		if(accSwitch){
			if (!mUserPreference.getBoolean("acc", true)) {
				int storageVolume = mUserPreference.getInt("volumn", 12);
				setVolumn(storageVolume);
			}
			mUserPreference.putBoolean("acc", true);
		}else{
			setVolumn(0);
			mUserPreference.putBoolean("acc", false);
			accSwitch = false;
		}

		mViewRoot = (SessionLinearLayout) View.inflate(this,
				R.layout.window_service_main, null);
		/*
		 * if (DeviceTool.getDeviceSDKVersion() > 13) {
		 * mViewRoot.setSystemUiVisibility
		 * (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);// zzj added } else {
		 * Logger.d(TAG, "!--->DeviceSDKVersion <= 13"); }
		 */
		findViews();
		// init framework & show prepare view
		mSessionManager = new GUISessionManager(this, mSessionContainer,
				mMicrophoneControl);

		Locale locale = getResources().getConfiguration().locale;
		language = locale.getCountry();

		/* < zzj 20150805 added for Mic float View Begin */
		mScreeFloatView = new FloatMicView(this);
		mLauncherPackage = PackageUtil.getLauncherPackages(this);
		/* zzj 20150805 added for Mic float View End > */
		initWindowParams();
		registerListener();
		/* < zzj 20150805 added for Mic float View Begin */
		registerReceiver();
		mPendingStartMicChecker = true;
		updateEnableFloatMic();
		startFloatMicChecker(0);
		/* zzj 20150805 added for Mic float View End > */
		/* < zzj delete 20150706 Begin */
		// if (PackageUtil.isRunningForeground(WindowService.this)) {
		// mSessionManager.showInitView(true);
		// }
		/* zzj delete 20150706 End > */
		// Intent killNotificationIntent = new Intent(this,
		// KillNotificationService.class);
		// startService(killNotificationIntent);
		messageTransCenter = new SystemCallTransition();
		messageTransCenter.setMessageTransListener(mMessageTransListener);

		/* < zzj 20150825 added for 4.4 default Sms App begin */
		/*
		 * if (GUIConfig.isAllowGUIRequestAsDefaultSmsApp) {
		 * DeviceTool.changeGUIToDefaultSmsApp(mContext); }
		 */
		/* zzj 20150825 added for 4.4 default Sms App End > */

		/*
		 * IntentFilter filter = new
		 * IntentFilter(GUIConfig.ACTION_SMS_SEND_SUCCESS);
		 * filter.addAction(GUIConfig.ACTION_SMS_SEND_FAIL);
		 * registerReceiver(mSmsSendStatusReceiver, filter);
		 */

		// setDefaultLocation();
//		getGeneralGPS(); // zzj added 20151021
		getContentResolver().registerContentObserver(
						Settings.System.getUriFor(SettingMapViewPagerActivity.MAP_INDEX),
						false, new ContentObserver(mUIHandler) {
							@Override
							public void onChange(boolean selfChange) {
								updateMapConfig();
							}
						});
		
		getContentResolver().registerContentObserver(
				Settings.System.getUriFor(ACC_STATE),
				false, new ContentObserver(mUIHandler) {
					@Override
					public void onChange(boolean selfChange) {
						accSwitch = Settings.System.getInt(getContentResolver(),ACC_STATE, 1)==1;
						if(accSwitch){
							int storageVolume = mUserPreference.getInt("volumn", 12);
							setVolumn(storageVolume);
							mUserPreference.putBoolean("acc", true);
							mUIHandler.sendEmptyMessageDelayed(MSG_ON_ACC_ON, 7000);
	
							if (mUserPreference.getFloat("lat", 0f) != 0f
									&& mUserPreference.getFloat("lng", 0f) != 0f) {
								CreateDialog(getApplication());
							}
	
							Intent ecar = new Intent("com.android.ecar.recv");
							ecar.putExtra("ecarSendKey", "ACC_ON");
							ecar.putExtra("cmdType", "standCMD");
							ecar.putExtra("keySet", "");
							getApplicationContext().sendBroadcast(ecar);
	
							// 广联一键通
							getApplicationContext().sendBroadcast(new Intent(COM_GLSX_BOOT_ACCON));
						}else{
							getApplicationContext().sendBroadcast(new Intent(COM_GLSX_BOOT_ACCOFF));
							curValue = getAudioManager().getStreamVolume(
									AudioManager.STREAM_ALARM);
							mUserPreference.putInt("volumn", curValue);
							setVolumn(0);
							mUserPreference.putBoolean("acc", false);
							if (mViewRoot.isShown()) {
								mSessionManager.cancelTalk();
							}

							mUIHandler.sendEmptyMessageDelayed(MSG_ON_ACC_OFF, 2000);
							MessageSender messageSender = new MessageSender(getApplicationContext());
							Intent stop = new Intent(CommandPreference.SERVICECMD);
							stop.putExtra(CommandPreference.CMDNAME,CommandPreference.CMDSTOP);
							messageSender.sendOrderedMessage(stop, null);

							Intent ecar = new Intent("com.android.ecar.recv");
							ecar.putExtra("ecarSendKey", "ACC_OFF");
							ecar.putExtra("cmdType", "standCMD");
							ecar.putExtra("keySet", "");
							getApplicationContext().sendBroadcast(ecar);
						}
					}
				});
		initAudioFoucus();

	}

	@SuppressLint("NewApi")
	private void findViews() {
		mSessionContainer = (SessionContainer) mViewRoot
				.findViewById(R.id.sessionContainer);
		mMicrophoneControl = (MicrophoneControl) mViewRoot
				.findViewById(R.id.microphoneControl);
		mMicrophoneControl
				.setMicrophoneViewClickListener(mMicrophoneViewClickListener);// add

		// zzj added 20150828
		mMicrophoneDoresoControl = (MicrophoneControlDoresoView) mViewRoot
				.findViewById(R.id.microphoneControl_doreso);
	}

	/**
	 * when WindowService onCreate should show wake up status Toast zzj 20150819
	 * added
	 */
	private void showWakeupStatusToast() {
		if (!Constant.isFirstStart(mContext)) {
			return;
		}
		Logger.d(TAG, "!--->showWakeupStatusToast----");
		if (UserPerferenceUtil.isWakeupEnable(mContext)) {
			String wakeupWord = UserPerferenceUtil.getWakeupWord(mContext);
			Toast.makeText(mContext,
					getString(R.string.toast_wakeup_open, wakeupWord),
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(mContext, R.string.toast_wakeup_closed,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * start WelcomeActivity zzj added 20150706
	 */
	/*
	 * private void startWelcomeActivity() { Logger.d(TAG,
	 * "!--->---startWelcomeActivity()-----"); Intent intent = new Intent(this,
	 * WelcomeActivity.class); intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 * // intent.setAction(WindowService.ACTION_START_REQUEST_MAKE_FINISHED);
	 * startActivity(intent); }
	 */
	private void registerListener() {
		mViewRoot.setDispatchKeyEventListener(mDispatchKeyEventListener);
		mViewRoot.setOnTouchEventListener(mOnTouchEventListener);
		/* < zzj 20150805 added for float mic view Begin */
		mScreeFloatView.setOnClickListener(mOnClickListener);
		UserPerferenceUtil.registerOnSharedPreferenceChangeListener(this,
				mPreferenceChangeListener);
		/* zzj 20150805 added for float mic view End > */
	}

	private void unregisterListener() {
		mViewRoot.setDispatchKeyEventListener(null);
		/* < zzj 20150805 added for float mic view Begin */
		mScreeFloatView.setOnClickListener(null);
		UserPerferenceUtil.unregisterOnSharedPreferenceChangeListener(this,
				mPreferenceChangeListener);
		/* zzj 20150805 added for float mic view End > */
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void resetWindowParamsFlags() {
		Logger.d(TAG, "!--->------resetWindowParamsFlags()------");
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
	}

	private void initWindowParams() {
		Logger.d(TAG, "!--->initWindowParams()------");
		mWindowParams.type =2009;
		mWindowParams.format = PixelFormat.RGBA_8888;
		resetWindowParamsFlags();
		mWindowParams.gravity = Gravity.CENTER;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.d(TAG, "!--->onStartCommand: intent " + intent);

		boolean isUnicarServiceInstalled = DeviceTool
				.isUnicarServiceInstalled(getApplicationContext());
		if (!isUnicarServiceInstalled) {
			Logger.e(TAG,
					"!--->onStartCommand()---UnicarService not installed!");
			mSessionManager
					.showInitFailedView(getString(R.string.error_init_msg_vui_service_not_install));
			// Toast.makeText(getApplicationContext(),
			// "UnicarService not installed!", Toast.LENGTH_LONG).show();
		}

		if (intent != null) {
			String action = intent.getAction();
			Logger.d(TAG, "!--->onStartCommand: action " + action);
			if (MessageReceiver.ACTION_START_TALK.equals(action)) {
				if (mViewRoot.isShown()) {
					if (mSessionManager != null) {
						mSessionManager.cancelTalk();
					}
				} else {
					boolean isCamera = isCameraActivity();
					if(isCamera){
						goHomePage();
					}
					startTalk();

				}
			} else if (ACTION_START_TALK.equals(action)) {
				if (mViewRoot.isShown()) {
					if (mSessionManager != null) {
						mSessionManager.cancelTalk();
					}
				} else {
					boolean isCamera = isCameraActivity();
					if(isCamera){
						goHomePage();
					}
					startTalk();

				}
			} else if (ACTION_START_REQUEST_MAKE_FINISHED.equals(action)) {
				Logger.d(TAG, "!--->----ACTION_START_REQUEST_MAKE_FINISHED");
				requestIsASRCompileFinished();
			} else if (MessageReceiver.ACTION_START_CALL_OUT.equals(action)) {
				Logger.d(TAG, "!--->onStartCommand--ACTION_START_CALL_OUT----");
				requestCallOut();
			} else if (MessageReceiver.ACTION_START_NAVIGATION.equals(action)) {
				Logger.d(TAG,
						"!--->onStartCommand--ACTION_START_NAVIGATION----");
				requestNavication();
			} else if (MessageReceiver.ACTION_START_MUSIC.equals(action)) {
				Logger.d(TAG, "!--->onStartCommand--ACTION_START_MUSIC----");
				requestMusic();
			} else if (MessageReceiver.ACTION_START_LOCAL_SEARCH.equals(action)) {
				Logger.d(TAG,
						"!--->onStartCommand--ACTION_START_LOCAL_SEARCH----");
				requestLocalSearch();
			} else if (ACTION_SET_WAKEUP.equals(action)) {
				if (UserPerferenceUtil.isWakeupEnable(mContext)) {
					sendProtocolEvent(
							SessionPreference.EVENT_NAME_SWITCH_WAKEUP,
							SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_START);
					ContentValues con = new ContentValues();
					con.put(WakeUpDB.SWITCH, 1);
					getContentResolver().update(WakeUpSwitch.CONTENT_URI, con,
							null, null);
				} else {
					sendProtocolEvent(
							SessionPreference.EVENT_NAME_SWITCH_WAKEUP,
							SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_STOP);
					ContentValues con = new ContentValues();
					con.put(WakeUpDB.SWITCH, 0);
					getContentResolver().update(WakeUpSwitch.CONTENT_URI, con,
							null, null);
				}
			} else if (ACTION_UPDATE_WAKEUP_WORD.equals(action)) {
				// zzj added 20151029
				String newText = intent.getStringExtra(EXTRA_KEY_WAKEUP_WORD);
				String oldWakeupWord = UserPerferenceUtil
						.getWakeupWord(mContext);
				sendUpdateWakeupWordEvent(oldWakeupWord, newText);
			} else if (ACTION_UPDATE_LOCATION.equals(action)) {
				// zzj added 20151118
				String newLocation = intent.getStringExtra(EXTRA_KEY_LOCATION);
				sendkeywordEvent(
						SessionPreference.EVENT_NAME_UPDATE_POI_KEYWORD,
						newLocation);
			} else if (ACTION_SET_TTSSPEED.equals(action)) {
				int speed = UserPerferenceUtil.getTTSSpeed(mContext);
				if (UserPerferenceUtil.VALUE_TTS_SPEED_SLOWLY == speed) {
					sendProtocolEvent(
							SessionPreference.EVENT_NAME_SWITCH_TTS_SPEED,
							SessionPreference.EVENT_PROTOCAL_SWITCH_TTS_SPEED_SLOWLY);
				} else if (UserPerferenceUtil.VALUE_TTS_SPEED_STANDARD == speed) {
					sendProtocolEvent(
							SessionPreference.EVENT_NAME_SWITCH_TTS_SPEED,
							SessionPreference.EVENT_PROTOCAL_SWITCH_TTS_SPEED_STANDARD);
				} else if (UserPerferenceUtil.VALUE_TTS_SPEED_FAST == speed) {
					sendProtocolEvent(
							SessionPreference.EVENT_NAME_SWITCH_TTS_SPEED,
							SessionPreference.EVENT_PROTOCAL_SWITCH_TTS_SPEED_FAST);
				}
			} else if (MessageReceiver.ACTION_PLAY_TTS.equals(action)) {
				// zzj added 20150914
				String tts = intent
						.getStringExtra(MessageReceiver.KEY_EXTRA_TTS_TEXT);
				Logger.d(TAG, "!--->onStartCommand--ACTION_PLAY_TTS----tts ="
						+ tts);
				String protol = GuiProtocolUtil.getPlayTTSEventParam(tts,
						GuiProtocolUtil.EVENT_PARAM_KEY_TTS_END_WAKEUP);
				sendProtocolEvent(SessionPreference.EVENT_NAME_PLAY_TTS, protol);
			} else if (ACTION_SET_ONESHOT.equals(action)) {
				if (GUIConfig.isSupportOneShotSetting) {
					if (UserPerferenceUtil.getOneShotEnable(mContext)) {
						sendProtocolEvent(
								SessionPreference.EVENT_NAME_SWITCH_ONESHOT,
								SessionPreference.EVENT_PROTOCAL_SWITCH_ONESHOT_START);
					} else {
						sendProtocolEvent(
								SessionPreference.EVENT_NAME_SWITCH_ONESHOT,
								SessionPreference.EVENT_PROTOCAL_SWITCH_ONESHOT_STOP);
					}
				}
			} else if (ACTION_SET_VERSION_LEVEL.equals(action)) {
				// zzj added 20151123
				sendVersionLevel();
			} else if (ACTION_SET_AEC.equals(action)) {
				if (GUIConfig.isSupportAECSetting) {
					if (UserPerferenceUtil.getAECEnable(mContext)) {
						sendProtocolEvent(
								SessionPreference.EVENT_NAME_SWITCH_AEC,
								SessionPreference.EVENT_PROTOCAL_SWITCH_AEC_START);
					} else {
						sendProtocolEvent(
								SessionPreference.EVENT_NAME_SWITCH_AEC,
								SessionPreference.EVENT_PROTOCAL_SWITCH_AEC_STOP);
					}
				}
			} else if (MessageReceiver.ACTION_START_NAVI.equals(action)) {
				CreateDialog(this);
			}
		}
		return START_STICKY;
	}

	/**
	 * sendVersionLevel
	 * 
	 * @author zzj
	 * @date 2015-11-23
	 */
	private void sendVersionLevel() {
		if (!GUIConfig.isSupporVersionLevelSetting) {
			return;
		}
		int level = UserPerferenceUtil.getVersionMode(mContext);
		String levelName = SessionPreference.PARAM_VERSION_LEVEL_STANDRARD;
		switch (level) {
		case UserPerferenceUtil.VALUE_VERSION_MODE_EXP:
			levelName = SessionPreference.PARAM_VERSION_LEVEL_EXP;
			break;
		case UserPerferenceUtil.VALUE_VERSION_MODE_STANDARD:
			levelName = SessionPreference.PARAM_VERSION_LEVEL_STANDRARD;
			break;
		case UserPerferenceUtil.VALUE_VERSION_MODE_HIGH:
			levelName = SessionPreference.PARAM_VERSION_LEVEL_HIGH;
			break;
		default:
			break;
		}
		Logger.d(TAG, "sendVersionLevel--level = " + level + "; levelName = "
				+ levelName);
		sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_VERSION_LEVEL,
				GuiProtocolUtil.getSetVersionLevelProtocol(levelName));
	}

	/**
	 * sync Configure to VUI
	 */
	private void syncConfigure() {
		if (accSwitch) {
			ContentValues con = new ContentValues();
			con.put(WakeUpDB.SWITCH, 1);
			getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null,
					null);
		} else {
			sendAccCloseEvent();
			ContentValues con = new ContentValues();
			con.put(WakeUpDB.SWITCH, 0);
			getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null,
					null);
		}
		// send switch WakeUp
		/*
		 * if (UserPerferenceUtil.isWakeupEnable(mContext)) {
		 * sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,
		 * SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_START); } else {
		 * sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,
		 * SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_STOP); }
		 */
		// send switch TTSSpeed
		int speed = UserPerferenceUtil.getTTSSpeed(mContext);
		if (UserPerferenceUtil.VALUE_TTS_SPEED_SLOWLY == speed) {
			sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_TTS_SPEED,
					SessionPreference.EVENT_PROTOCAL_SWITCH_TTS_SPEED_SLOWLY);
		} else if (UserPerferenceUtil.VALUE_TTS_SPEED_STANDARD == speed) {
			sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_TTS_SPEED,
					SessionPreference.EVENT_PROTOCAL_SWITCH_TTS_SPEED_STANDARD);
		} else if (UserPerferenceUtil.VALUE_TTS_SPEED_FAST == speed) {
			sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_TTS_SPEED,
					SessionPreference.EVENT_PROTOCAL_SWITCH_TTS_SPEED_FAST);
		}

		if (GUIConfig.isSupportOneShotSetting) {
			// send switch OneShot
			if (UserPerferenceUtil.getOneShotEnable(mContext)) {
				sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_ONESHOT,
						SessionPreference.EVENT_PROTOCAL_SWITCH_ONESHOT_START);
			} else {
				sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_ONESHOT,
						SessionPreference.EVENT_PROTOCAL_SWITCH_ONESHOT_STOP);
			}
		}

		if (GUIConfig.isSupportAECSetting) {
			// send switch AEC
			if (UserPerferenceUtil.getAECEnable(mContext)) {
				sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_AEC,
						SessionPreference.EVENT_PROTOCAL_SWITCH_AEC_START);
			} else {
				sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_AEC,
						SessionPreference.EVENT_PROTOCAL_SWITCH_AEC_STOP);
			}
		}

		sendVersionLevel(); // zzj added 20151123
	}

	private void startTalk() {
		mSessionManager.startTalk();

		// zzj 20150827 added
		mWakeupSuccessType = SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS;
		updateMicControlView(SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS);
	}

	public void onReset() {
		Logger.d(TAG, "onReset");
		dismiss();
	}

	@SuppressLint("InlinedApi")
	private void show(View view) {
		if (view == null || view.isShown()) {
			return;
		}
		// 弹出框强制横屏
		/*
		 * if (mWindowParams.screenOrientation !=
		 * ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
		 * mWindowParams.screenOrientation =
		 * ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE; }
		 */

		mWindowManager.addView(view, mWindowParams);
	}

	public void addPrepareView(View view) {
		if (view == null) {
			Logger.w(TAG, "addSessionView: view null,return!");
			return;
		}
		Logger.d(TAG, "!--->addPrepareView------");
		dismiss();
		WindowManager.LayoutParams WindowParams = new WindowManager.LayoutParams(
				2009,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		WindowParams.gravity = Gravity.CENTER;
		WindowParams.format = PixelFormat.RGBA_8888;
		if (view.isShown()) {
			mWindowManager.removeViewImmediate(view);
		}
		mWindowManager.addView(view, WindowParams);
	}

	@SuppressWarnings("deprecation")
	public void show() {
		if (mViewRoot.isShown()) {
			return;
		}
		// mHandler.sendEmptyMessageDelayed(0, 500);
	//	RomSystemSetting.setMute(mContext);
		mContext.sendBroadcast(new Intent(ACTION_QUITE_SCREENOFF));
		// requestAudioFocus();
		Logger.d(TAG, "show");
		acquireWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, GANK);
		/* < zzj 20150805 added for Float Mic View Begin */
		stopFloatMicChecker();
		playTTs = false;
		hideFloatView();
		mPendingStartMicChecker = true;
		/* zzj 20150805 added for Float Mic View End > */

		show(mViewRoot);

	}

	public void dismiss() {
		releaseWakeLock();
		playTTs = true;
		if (playText != null) {
			TTSController.getInstance(mContext).playText(playText, id);
			playText = null;
		}
		isMainMark = false;
		if (!mViewRoot.isShown()) {
			return;
		}
		abandonAudioFocus();
		Logger.d(TAG, "dismiss");
		mWindowManager.removeViewImmediate(mViewRoot);

		/* < zzj 20150805 added for Float Mic View Begin */
		if (mPendingStartMicChecker) {
			mPendingStartMicChecker = false;
			updateEnableFloatMic();
			startFloatMicChecker(0);
		}

		if (accSwitch) {
			sendAccOpenEvent();
		}

		// RomSystemSetting.setUnMute(mContext);
		mUIHandler.postDelayed(mRecoverRunnable, 1000);
		/* zzj 20150805 added for Float Mic View End > */
	}

	public void dimissView(View view) {
		if (!view.isShown()) {
			return;
		}
		Logger.d(TAG, "dimissView---prepare view dismiss");
		mWindowManager.removeViewImmediate(view);

		/* < zzj 20150805 added for Float Mic View Begin */
		if (mPendingStartMicChecker) {
			mPendingStartMicChecker = false;
			updateEnableFloatMic();
			startFloatMicChecker(0);
		}
		/* zzj 20150805 added for Float Mic View End > */
	}

	public Point getWindowSize() {
		return mWindowSize;
	}

	public void addView(View view, int windowWidth, int windowHeight) {
		Logger.d(TAG, "!--->addView:windowWidth " + windowWidth
				+ ",windowHeight " + windowHeight);
		mWindowParams.width = windowWidth;
		mWindowParams.height = windowHeight;
		addView(view);
	}

	public void addView(View view) {
		Logger.d(TAG, "addView");
		dismiss();
		mWindowParams.gravity = Gravity.CENTER;
		mWindowParams.x = 0;
		mWindowParams.y = 0;
		resetWindowParamsFlags();

		if ((mWindowParams.softInputMode & WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION) == 0) {
			mWindowParams.softInputMode |= WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
		}

		show(view);
	}

	static boolean canTextInput(View v) {
		if (v.onCheckIsTextEditor()) {
			return true;
		}

		if (!(v instanceof ViewGroup)) {
			return false;
		}

		ViewGroup vg = (ViewGroup) v;
		int i = vg.getChildCount();
		while (i > 0) {
			i--;
			v = vg.getChildAt(i);
			if (canTextInput(v)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Logger.d(TAG, "onLowMemory");
	}

	@Override
	public void onDestroy() {
		Logger.d(TAG, "onDestroy");
		super.onDestroy();
		dismiss();
		unregisterListener();
		releaseWakeLock();
		unBindService(mContext); // zzj added 20150706
		unregisterReceiver(mSmsSendStatusReceiver);// zzj added 20150709

		/* < zzj 20150805 added for Float Mic View Begin */
		unregisterReceiver();
		stopKWMusicService();
		stopFloatMicChecker();
		mLauncherPackage.clear();
		mLauncherPackage = null;
		/* zzj 20150805 added for Float Mic View End > */
		mLocateTime = 0;
		mSessionManager.onDestroy();
		// PhoneStateReceiver.release();
		mSessionManager = null;
		mWindowParams = null;
		mWindowSize = null;
		if (contactDataModel != null) {
			contactDataModel.release();
		}

		if (mediaDataModel != null) {
			mediaDataModel.release();
		}

		if (mPlayer != null) {
			mPlayer.release();
		}

		if (appsDataModel != null) {
			appsDataModel.release();
		}

		stopForegroundCompat(NOTIFICATION_ID);
	}

	public static Intent getExplicitIntent(Context context,
			Intent implicitIntent) {
		// Retrieve all services that can match the given intent
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent,
				0);
		// Make sure only one match was found
		if (resolveInfo == null || resolveInfo.size() != 1) {
			return null;
		}
		// Get component info and create ComponentName
		ResolveInfo serviceInfo = resolveInfo.get(0);
		String packageName = serviceInfo.serviceInfo.packageName;
		String className = serviceInfo.serviceInfo.name;

		Logger.d(TAG, "getExplicitIntent packagename = " + packageName
				+ ";  classname=" + className);

		ComponentName component = new ComponentName(packageName, className);
		// Create a new intent. Use the old one for extras and such reuse
		Intent explicitIntent = new Intent(implicitIntent);
		// Set the component to be explicit
		explicitIntent.setComponent(component);
		return explicitIntent;
	}

	public void bindMessageRouterService() {
		try {
			Logger.d(TAG, "!--->bindMessageRouterService");
			Intent intent = new Intent(
					"com.unisound.unicar.messagerouter.start");
			// for android 5.0 and above
			if (getExplicitIntent(mContext, intent) != null) {
				intent = getExplicitIntent(mContext, intent);
			}
			// intent.setPackage(getPackageName());
			mContext.startService(intent);
			mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void rebindMessageRouterService() {
		if (mMessageRouterService == null) {
			Logger.d(TAG, "rebindMessageRouterService");
			bindMessageRouterService();
		}
	}

	private void startKWMusicService() {
		Intent intentMusic = new Intent(mContext, KWMusicService.class);
		mContext.startService(intentMusic);
	}

	private void stopKWMusicService() {
		Intent intentMusic = new Intent(mContext, KWMusicService.class);
		mContext.stopService(intentMusic);
	}

	/**
	 * startUniDriveFmService zzj 20150923 added
	 */
	private void startUniDriveFmService() {
		Logger.d(TAG, "startUniDriveFmService from window service");
		Intent intent = new Intent(mContext, UniDriveFmGuiService.class);
		intent.setAction(MessageReceiver.ACTION_START_UNIDRIVE_FM);
		mContext.startService(intent);
	}

	/**
	 * startXmFmGuiService zzj 20150923 added
	 */
	private void startXmFmGuiService() {
		Logger.d(TAG, "XmFmGuiService from window service");
		Intent intent = new Intent(mContext, XmFmGuiService.class);
		intent.setAction(MessageReceiver.ACTION_START_XM_FM);
		mContext.startService(intent);
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mMessageRouterService = IMessageRouterService.Stub
					.asInterface(service);
			try {
				Logger.d(TAG, "!--->onServiceConnected()-----------");
				mMessageRouterService.registerCallback(mCallback);
				registSystemCall();
				// if(isNeedRequestIsASRCompileFinished){
				requestIsASRCompileFinished();
				sendRequestWakeupWordsEvent();
				syncConfigure();
				// bind MessageRouterService成功后开始定位
//				getGeneralGPS();
				// }
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Logger.d(TAG, "onServiceDisconnected");
			dismiss();
			mMessageRouterService = null;
			rebindMessageRouterService();
		}
	};

	private IMessageRouterCallback mCallback = new IMessageRouterCallback.Stub() {

		@Override
		public void onCallBack(String callBackJson) throws RemoteException {
			Logger.d(TAG, language + "!--->onCallBack : " + callBackJson);
			if ("TW".endsWith(language)) {
				callBackJson = ZHConverter.convert(callBackJson,
						ZHConverter.TRADITIONAL);
			}
			onCallBackFunction(callBackJson);
			mSystemCallJson = callBackJson;
			checkIfNeedSendRespImmediately(callBackJson);
		}
	};

	private void onCallBackFunction(String callBackJson) {
		messageTransCenter.handlerSystemCall(callBackJson);
	}

	/**
	 * check If Need Send Response Immediately
	 * 
	 * @param callBackJson
	 */
	private void checkIfNeedSendRespImmediately(String callBackJson) {
		Logger.d(TAG, "!--->checkIfNeedSendRespImmediately----");
		JSONObject obj = JsonTool.parseToJSONObject(callBackJson);
		if (obj != null) {
			JSONObject jobj = JsonTool.parseToJSONObject(callBackJson);
			JSONObject data = JsonTool.getJSONObject(jobj,
					SessionPreference.KEY_DATA);
			String functionName = JsonTool.getJsonValue(data,
					SessionPreference.KEY_FUNCTION_NAME, "");
			JSONObject dataParams = JsonTool.getJSONObject(data,
					SessionPreference.KEY_PARAMS);

			if (SessionPreference.VALUE_FUNCTION_NAME_ON_TALK_PROTOCOL
					.equals(functionName)) {
				JSONObject typeObj = JsonTool.getJSONObject(dataParams,
						SessionPreference.KEY_TYPE);
				String type = JsonTool.getJsonValue(typeObj,
						SessionPreference.KEY_TYPE);
				String originType = JsonTool.getJsonValue(typeObj,
						SessionPreference.KEY_ORIGIN_TYPE);
				if (dataParams != null
						&& SessionPreference.VALUE_TYPE_WAITING.equals(type)
						&& (SessionPreference.DOMAIN_ROUTE.equals(originType) || SessionPreference.DOMAIN_NEARBY_SEARCH
								.equals(originType))) {
					return;
				} else {
					sendResponse("");
				}
			} else {
				sendResponse("");
			}
		}
	}

	/**
	 * TODO:if type in "data" is "CALL" need response to VUI zzj modify 20150807
	 * 
	 * @param callBackJson
	 */

	public void sendResponse(String params) {
		sendResponse(params, "SUCCESS");
	}

	/**
	 * 
	 * @param params
	 * @param state
	 */
	public void sendResponse(String params, String state) {
		Logger.d(TAG, "sendResponse params : " + params + "; state : " + state);
		Logger.d(TAG, "sendResponse mSystemCallJson : " + mSystemCallJson);
		JSONObject jobj = JsonTool.parseToJSONObject(mSystemCallJson);
		JSONObject data = JsonTool.getJSONObject(jobj,
				SessionPreference.KEY_DATA);
		String callName = JsonTool.getJsonValue(data,
				SessionPreference.KEY_FUNCTION_NAME);
		String callID = JsonTool.getJsonValue(jobj,
				SessionPreference.KEY_CALL_ID);
		String param = "";
		String domain = "";
		if (params != null && params.length() > 0) {
			param = params;
			// add tyz 0714
			JSONObject params_ = JsonTool.getJSONObject(data, "params");
			JSONObject type = JsonTool.getJSONObject(params_, "type");
			domain = JsonTool.getJsonValue(type, "origin_type");
			Logger.d(TAG, "sendResponse domain : " + domain);
			// add tyz 0714
		} else {
			JSONObject dataParams = JsonTool.getJSONObject(data,
					SessionPreference.KEY_PARAMS);
			if (dataParams != null) {
				param = dataParams.toString();
			}
		}

		messageTransCenter.sendResponse(callID, callName, state, domain, param);
	}

	/*
	 * public void sendResponse() { Logger.d(TAG,
	 * "sendResponse mSystemCallJson : " + mSystemCallJson); JSONObject jobj =
	 * JsonTool.parseToJSONObject(mSystemCallJson); JSONObject data =
	 * JsonTool.getJSONObject(jobj, SessionPreference.KEY_DATA); String callID =
	 * JsonTool.getJsonValue(jobj, SessionPreference.KEY_CALL_ID); String param
	 * = "{\"functionID\":\"6\"}"; messageTransCenter.sendResponse(callID,
	 * "onTTSPlayEnd", "SUCCESS", "", param); }
	 */

	public void unBindService(Context context) {
		try {
			Logger.d(TAG, "!--->unBindService");
			context.unbindService(mConnection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * SystemCall add in here
	 * 
	 * @return
	 */
	private JSONArray setDataForSystemCall() {
		ArrayList<String> namelist = new ArrayList<String>();
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_SET_STATE);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_IS_ASR_COMPILE_DONE);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_RECORDING_START);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_RECORDING_STOP);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_TALK_PROTOCOL);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_UPDATE_VOLUME);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_TTS_PLAY_END);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_RECORDING_PREPARED);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_RECORDING_EXCEPTION); // zzj
																					// added
																					// 20150807
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_TALK_RESULT);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_FETCH_UPDATE_CONTACT_DONE);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_FETCH_UPDATE_MEDIA_DONE);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_FETCH_UPDATE_APP_DONE);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_RECOGNIZER_TIMEOUT);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_CTT_CANCEL);// add
																			// tyz
																			// ctt
																			// cancel
																			// systemcall
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_MAIN_ACTION);// do
																		// main
																		// action

		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_ONESHOT_RECOGNIZER_TIMEOUT);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_START_FAKE_RECORDING_ANIMATION);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_REQUEST_WAKEUP_WORDS);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_CONTROL_WAKEUP_SUCCESS);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_UPDATE_WAKEUP_WORD_STATUS);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_RECODING_IDLE);

		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < namelist.size(); i++) {
			JSONObject name = new JSONObject();
			try {
				name.put("callName", namelist.get(i).toString());
				jsonArray.put(name);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonArray;
	}

	/**
	 * register System Call to VUI service 0 setState 1 onRecordingStart 2
	 * onRecordingStop 3 isASRCompileDone
	 */
	private void registSystemCall() {

		JSONObject main = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			main.put("type", "REG");
			main.put("data", data);

			data.put("version", PackageUtil.getAppVersionName(mContext));
			data.put("moduleName", "GUI");
			data.put("callNameList", setDataForSystemCall());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// String systemCall =
		// "{\"type\":\"REG\",\"data\":{\"version\":\"v3.0\",\"moduleName\":\"GUI\",\"callNameList\":[{\"callName\":\"setState\"},{\"callName\":\"isASRCompileDone\"},{\"callName\":\"onRecordingStart\"},{\"callName\":\"onRecordingStop\"}]}}";

		sendMsg(main.toString());

	}

	/**
	 * send message to framework service
	 * 
	 * @param msgJson
	 */
	private void sendMsg(final String msgJson) {
		rebindMessageRouterService();
		if (mMessageRouterService != null) {
			try {
				Logger.d(TAG, "!--->sendMsg()----msgJson = " + msgJson);
				mMessageRouterService.sendMessage(msgJson);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * when Click mic icon, send "PTT" event to start record when GUI is run in
	 * background
	 */
	public void sendPTT() {
		Logger.d(TAG, "sendPTT");
		sendEvent(SessionPreference.EVENT_NAME_PTT);
	}

	/* < zzj 20150828 added for Music Doreso Begin */
	/**
	 * sendPTTDoreso: "PTT_DORESO"
	 */
	public void sendPTTDoreso() {
		Logger.d(TAG, "EVENT_NAME_PTT_DORESO");
		sendEvent(SessionPreference.EVENT_NAME_PTT_DORESO);
	}

	/**
	 * send PTT By WakeupSuccess Type
	 */
	public void sendPTTByWakeupSuccessType() {
		Logger.d(TAG, "!--->sendPTTByWakeupSuccessType--mWakeupSuccessType = "
				+ mWakeupSuccessType);
		if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS == mWakeupSuccessType) {
			sendPTT();
		} else if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO == mWakeupSuccessType) {
			sendPTTDoreso();
		}
	}

	/* zzj 20150828 added for Music Doreso End > */

	public void sendCancelEvent() {
		Logger.d(TAG, "sendCancelEvent");
		sendEvent(SessionPreference.EVENT_NAME_CTT);
	}

	public void sendStartLocalSearchNaviEvent() {
		Logger.d(TAG, "sendStartLocalSearchNaviEvent");
		sendEvent(SessionPreference.EVENT_NAME_LOCALSERACH_NAVI);
	}

	public void sendStartLocalSearchCallEvent() {
		Logger.d(TAG, "sendStartLocalSearchCallEvent");
		sendEvent(SessionPreference.EVENT_NAME_LOCALSERACH_CALL);
	}

	/**
	 * called on mUIHandler MSG_GUI_CANCEL_WAITTING_SESSION zzj 20150813 modify
	 */
	private void sendWaittingCancelEvent() {
		Logger.d(TAG, "sendWaittingCancelEvent");
		sendEvent(SessionPreference.EVENT_NAME_WAITTING_CANCEL);
	}

	public void sendResetWakeupWordEvent() {
		sendEvent(SessionPreference.EVENT_NAME_RESET_WAKEUP_WORD);
	}

	public void sendStopWakeupWordEvent() {
		sendEvent(SessionPreference.EVENT_NAME_STOP_WAKEUP);
	}

	public void sendRequestWakeupWordsEvent() {
		sendEvent(SessionPreference.EVENT_NAME_REQUEST_WAKEUP_WORDS);
	}

	/**
	 * 
	 * @param oldWakeupWord
	 * @param newWakeupWord
	 */
	public void sendUpdateWakeupWordEvent(String oldWakeupWord,
			String newWakeupWord) {
		sendProtocolEvent(SessionPreference.EVENT_NAME_UPDATE_WAKEUP_WORD,
				"{\"oldWakeupWord\":" + "\"" + oldWakeupWord
						+ "\" , \"newWakeupWord\":" + "\"" + newWakeupWord
						+ "\" }");
	}

	private void sendEvent(String eventName) {
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":"
				+ eventName + "}}";
		Logger.d(TAG, "-sendEvent- eventMsg : " + eventMsg);
		sendMsg(eventMsg);
	}

	/**
	 * TYZ addded 20151013 zzj modify 20151023
	 * 
	 * @param eventName
	 * @param keyWords
	 */
	private void sendkeywordEvent(String eventName, String keyWord) {
		// String eventMsg =
		// "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":"
		// + eventName
		// + ",\"keyword\":" + keyWord + "}}";
		// Logger.d(TAG, "-sendEvent- eventMsg : " + eventMsg);
		// sendMsg(eventMsg);
		sendProtocolEvent(eventName,
				GuiProtocolUtil.getChangeLocationParamProtocol(keyWord));
	}

	/**
	 * send wakeup stop when acc close
	 */
	public void sendAccCloseEvent() {
		Logger.d(TAG, "sendAccCloseEvent");
		sendEvent(SessionPreference.EVENT_NAME_ACC_CLOSE);
	}

	/**
	 * send wakeup open when acc open
	 */
	public void sendAccOpenEvent() {
		Logger.d(TAG, "sendAccOpenEvent");
		sendEvent(SessionPreference.EVENT_NAME_ACC_OPEN);
	}

	/**
	 * 
	 * zzj added 20150702
	 * 
	 * @param eventName
	 *            : ON_CONFIRM_OK, ON_CONFIRM_CANCEL, SELECT_ITEM
	 * @param protol
	 */
	public void sendProtocolEvent(String eventName, String protol) {
		Logger.d(TAG, "sendProtocolEvent()----eventName =" + eventName
				+ "; protol = " + protol);
		if (SessionPreference.EVENT_NAME_ON_CONFIRM_TIME_UP.equals(eventName)) {
			protol = SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_TIME_UP;
		}
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\""
				+ eventName + "\"},\"param\":" + protol + "}";
		// {"type":"EVENT","data":{"moduleName":"GUI","eventName":"ON_CONFIRM_OK"},"param":{"service":"cn.yunzhisheng.setting","semantic":{"intent":{"confirm":"OK"}},"code":"SETTING_EXEC"}}
		// {"type":"EVENT","data":{"moduleName":"GUI","eventName":"ON_CONFIRM_CANCEL"},"param":{"service":"cn.yunzhisheng.setting","semantic":{"intent":{"confirm":"CANCEL"}},"code":"SETTING_EXEC"}}
		// {"type":"EVENT","data":{"moduleName":"GUI","eventName":"ON_CONFIRM_TIME_UP"},"param":{"service":"cn.yunzhisheng.setting","semantic":{"intent":{"confirm":"TIME_UP"}},"code":"SETTING_EXEC"}}
		// {"type":"EVENT","data":{"moduleName":"GUI","eventName":"SMS_CONTENT_RETYPE"},"param":{"service":"cn.yunzhisheng.setting","semantic":{"intent":{"confirm":"SMS_CONTENT_RETYPE"}},"code":"SETTING_EXEC"}}
		Logger.d(TAG, "!--->sendProtol()----eventMsg = " + eventMsg);
		sendMsg(eventMsg);
	}

	/**
	 * IS_ASR_COMPILE_FINISH
	 */
	public void requestIsASRCompileFinished() {
		Logger.d(TAG, "!--->sendIsASRCompileFinished---Begin---");
		if (mMessageRouterService == null) {
			// isNeedRequestIsASRCompileFinished = true;
			rebindMessageRouterService();
			return;
		}
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\"IS_ASR_COMPILE_FINISH\"}}";
		sendMsg(eventMsg);
		Logger.d(TAG, "!--->sendIsASRCompileFinished---Finished---");
	}

	/**
	 * requestSupportDomainList zzj added 20150702
	 */
	public void requestSupportDomainList() {
		Logger.d(TAG, "!--->requestSupportDomainList()----");
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\"GET_SUPPORT_DOMAIN_LIST\"}}";
		sendMsg(eventMsg);
	}

	/**
	 * 
	 */
	public void requestCallOut() {
		Logger.d(TAG, "!--->requestCallOut()----");
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\"MAIN_ACTION\"},\"param\":{\"type\":\"call\"}}";
		sendMsg(eventMsg);
	}

	public void requestNavication() {
		Logger.d(TAG, "!--->requestNavication()----");
		isMainMark = true;
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\"MAIN_ACTION\"},\"param\":{\"type\":\"navi\"}}";
		sendMsg(eventMsg);
	}

	public void requestMusic() {
		Logger.d(TAG, "!--->requestMusic()----");
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\"MAIN_ACTION\"},\"param\":{\"type\":\"music\"}}";
		sendMsg(eventMsg);
	}

	/**
	 * request Local Search zzj added 20150713
	 */
	public void requestLocalSearch() {
		Logger.d(TAG, "!--->requestLocalSearch()----");
		isMainMark = true;
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\"MAIN_ACTION\"},\"param\":{\"type\":\"localsearch\"}}";
		sendMsg(eventMsg);
	}

	/**
	 * write Contacts Info
	 */
	private void writeContactsInfo() {
		Logger.d(TAG, "!--->writeContactsInfo()-----");
		/* < zzj added 20150706 begin */
		if (PackageUtil.isRunningForeground(WindowService.this)) {
			mSessionManager.showInitView(true);
		}
		if (!DeviceTool.isSdCardExist()) {
			Logger.e(TAG, "!--->No SDCard mounted!");
			mSessionManager.showInitFailedView(mContext
					.getString(R.string.error_init_msg_no_sdcard));
			// Toast.makeText(mContext,
			// mContext.getString(R.string.error_init_msg_no_sdcard),
			// Toast.LENGTH_LONG).show();
			return;
		}
		/* zzj added 20150706 end > */
		contactDataModel = new ContactDataModel(mContext);
		contactDataModel.setDataModelListener(mDataModelListener);
		contactDataModel.init();
	}

	/**
	 * write Medias Info
	 */
	private void writeMediasInfo() {
		Logger.d(TAG, "!--->writeMediasInfo()-----");
		mediaDataModel = new MediaDataModel(mContext);
		mediaDataModel.setDataModelListener(mDataModelListener);
		mediaDataModel.init();
	}

	// add by tyz 0709 write apps info
	protected void writeAppsInfo() {
		// TODO Auto-generated method stub
		Logger.d(TAG, "!--->writeAppsInfo()-----");
		appsDataModel = new AppsDataModel(mContext);
		appsDataModel.setDataModelListener(mDataModelListener);
		appsDataModel.init();
	}

	IBaseListener mDataModelListener = new IBaseListener() {
		@Override
		public void onDataDone(int type) {
			switch (type) {
			case SessionPreference.SAVE_CONTACT_DATA_DONE:
				if (startDownPhone) {
					/*TTSController.getInstance(WindowService.this).playText(
							DOWNVOER);*/
					sendBroadcast(new Intent(ACTION_CONTACT_DONE));
					startDownPhone = false;
				}
				sendDataDoneEvent(SessionPreference.EVENT_NAME_SAVE_CONTACTS_DONE);
				break;
			case SessionPreference.SAVE_MEDIA_DATA_DONE:
				sendDataDoneEvent(SessionPreference.EVENT_NAME_SAVE_MEDIAS_DONE);
				break;
			case SessionPreference.SAVE_APPS_DATA_DONE:
				sendDataDoneEvent(SessionPreference.EVENT_NAME_SAVE_APPS_DONE);
				break;
			case SessionPreference.SAVE_UPDATE_CONTACT_DATA_DONE:
				if (startDownPhone) {
					TTSController.getInstance(WindowService.this).playText(
							DOWNVOER);
					sendBroadcast(new Intent(ACTION_CONTACT_DONE));
					startDownPhone = false;
				}
				sendDataDoneEvent(SessionPreference.EVENT_NAME_SAVE_UPDATE_CONTACTS_DONE);
				break;
			case SessionPreference.SAVE_UPDATE_MEDIA_DATA_DONE:
				sendDataDoneEvent(SessionPreference.EVENT_NAME_SAVE_UPDATE_MEDIAS_DONE);
				break;
			case SessionPreference.SAVE_UPDATE_APPS_DATA_DONE:
				sendDataDoneEvent(SessionPreference.EVENT_NAME_SAVE_UPDATE_APPS_DONE);
				break;
			}
		}
	};

	private void sendDataDoneEvent(String eventName) {
		JSONObject objcData = new JSONObject();
		try {
			objcData.put("type", "EVENT");
			JSONObject data = new JSONObject();
			data.put("moduleName", "GUI");
			data.put("eventName", eventName);
			objcData.put("data", data);
			sendMsg(objcData.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	ISystemCallTransitionListener mMessageTransListener = new ISystemCallTransitionListener() {

		@Override
		public void setState(int type) {
			Logger.d("mMessageTransListener", "!--->setState()----type = "
					+ type);
			Message msg = new Message();
			msg.what = MSG_SET_STATE;
			msg.obj = type;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void onTalkRecordingPrepared() {
			playTTs = false;
			Logger.d("mMessageTransListener",
					"!--->---onTalkRecordingPrepared---");
			Message msg = new Message();
			msg.what = MSG_ON_RECORDING_PREPARED;
			mUIHandler.sendMessage(msg);
			// mSessionManager.onTalkRecordingStart();
		}

		@Override
		public void onTalkRecordingException() {
			Logger.d("mMessageTransListener",
					"!--->---onTalkRecordingException---");
			mUIHandler.sendEmptyMessage(MSG_ON_RECORDING_EXCEPTION);
		}

		@Override
		public void onTalkRecordingStart() {
			requestAudioFocus();
			playTTs = false;
			Logger.d("mMessageTransListener", "!--->---onTalkRecordingStart---");
			Message msg = new Message();
			msg.what = MSG_ON_RECORDING_START;
			mUIHandler.sendMessage(msg);
			// mSessionManager.onTalkRecordingStart();
		}

		@Override
		public void onTalkRecordingStop() {
			abandonAudioFocus();
			playTTs = true;
			if (playText != null) {
				TTSController.getInstance(mContext).playText(playText, id);
				playText = null;
			}
			Logger.d("mMessageTransListener", "!--->---onTalkRecordingStop---");
			Message msg = new Message();
			msg.what = MSG_ON_RECORDING_STOP;
			mUIHandler.sendMessage(msg);
			// mSessionManager.onTalkRecordingStop();
		}

		@Override
		public void onTalkResult(String result) {
			Logger.d("mMessageTransListener", "!--->onTalkResult---result = "
					+ result);
			Message msg = new Message();
			msg.what = MSG_ON_RECORDING_RESULT;
			msg.obj = result;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void onSessionProtocal(String protocol) {
			Logger.d("mMessageTransListener",
					"!--->onSessionProtocal---protocol = " + protocol);
			Message msg = new Message();
			msg.what = MSG_ON_SESSION_PROTOCAL;
			msg.obj = protocol;
			mUIHandler.sendMessage(msg);
			// mSessionManager.onSessionProtocal(protocol);
		}

		@Override
		public void onSendMsg(String msg) {
			Logger.d("mMessageTransListener", "!--->onSendMsg---msg = " + msg);
			sendMsg(msg);
		}

		@Override
		public void onPlayEnd() {
			// abandonAudioFocus();
			Logger.d("mMessageTransListener", "!--->onPlayEnd ");
			/*
			 * playTTs = true; if (playText != null) {
			 * TTSController.getInstance(mContext).playText(playText); playText
			 * = null; }
			 */
			Message msg = new Message();
			msg.what = MSG_ON_TTS_PLAY_END;
			mUIHandler.sendMessage(msg);
			// mSessionManager.onPlayEnd();
		}

		@Override
		public void onUpdateVolume(int volume) {
			Logger.d("mMessageTransListener", "!--->onUpdateVolume---volume = "
					+ volume);
			// zzj added 20150714
			// Logger.d(TAG, "!--->onUpdateVolume---volume = "+volume);
			if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS == mWakeupSuccessType) {
				mMicrophoneControl.setVoiceLevel(volume);
			} else if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO == mWakeupSuccessType) {
				mMicrophoneDoresoControl.setVoiceLevel(volume);
			}
		}

		@Override
		public void onRecognizerTimeout() {
			Logger.d("mMessageTransListener", "!--->onRecognizerTimeout ");
			Message msg = new Message();
			msg.what = MSG_ON_RECOGNIZER_TIMEOUT;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void onCTTCancel() {
			abandonAudioFocus();
			playTTs = true;
			if (playText != null) {
				TTSController.getInstance(mContext).playText(playText, id);
				playText = null;
			}
			Logger.d("mMessageTransListener", "!--->onCTTCancel");
			Message msg = new Message();
			msg.what = MSG_ON_CTT_CANCEL;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void onOneShotRecognizerTimeout() {
			Logger.d("mMessageTransListener",
					"!--->---onOneShotRecognizerTimeout---");
			Message msg = new Message();
			msg.what = MSG_ON_ONESHOT_RECOGNIZER_TIMEOUT;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void onStartFakeAnimation() {
			Logger.d("mMessageTransListener", "!--->onStartFakeAnimation---");
			Message msg = new Message();
			msg.what = MSG_ON_START_RECORDING_FAKE_ANIMATION;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void onGetWakeupWords(String wakeupWords) {
			Logger.d("mMessageTransListener", "!--->onGetWakeupWords---");
			Message msg = new Message();
			msg.what = MSG_ON_GET_WAKEUP_WORDS;
			msg.obj = wakeupWords;
			mUIHandler.sendMessage(msg);
		}

		public void onClickMainActionButton(int style) {// add tyz 20151020 do
														// mian action branch
			// TODO Auto-generated method stub
			Logger.d("mMessageTransListener",
					"!--->onClickMainActionButton()----style = " + style);
			Message msg = new Message();
			msg.what = MSG_ON_CLICK_MAIN_ACTION_BUTTON;
			msg.obj = style;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void onControlWakeupSuccess(String wakeupWord) {
			// 唤醒成功
			Logger.d("mMessageTransListener",
					"!--->onControlWakeupSuccess wakeupWord : " + wakeupWord);
			Message msg = new Message();
			msg.what = MSG_ON_CONTROL_WAKEUP_SUCCESS;
			msg.obj = wakeupWord;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void onUpdateWakeupWordsStatus(String status) {
			Logger.d("mMessageTransListener",
					"!--->onUpdateWakeupWordsStatus status : " + status);
			Message msg = new Message();
			msg.what = MSG_ON_UPDATE_WAKEUP_WORDS_STATUS;
			msg.obj = status;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void onTalkRecodingIdle() {
			Logger.d("mMessageTransListener", "!--->onTalkRecodingIdle");
			Message msg = new Message();
			msg.what = MSG_ON_RECORDING_IDLE;
			mUIHandler.sendMessage(msg);
		}
	};

	/**
	 * zzj 20150827 added
	 * 
	 * @param type
	 */
	private void updateMicControlView(int type) {
		Logger.d(TAG, "!--->updateMicControlView---type = " + type);
		if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS == type) {
			mMicrophoneControl.setVisibility(View.VISIBLE);
			mMicrophoneDoresoControl.setVisibility(View.GONE);
			mSessionManager.setMicrophoneControl(mMicrophoneControl); // zzj
																		// 20150827
																		// added
		} else if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO == type) {
			mMicrophoneControl.setVisibility(View.GONE);
			mMicrophoneDoresoControl.setVisibility(View.VISIBLE);
			mSessionManager.setMicrophoneControl(mMicrophoneDoresoControl); // zzj
																			// 20150827
																			// added
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SET_STATE:
				int type = (Integer) msg.obj;
				switch (type) {
				case SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_INIT_DONE:
					Logger.d(TAG, "!--->VALUE_SET_STATE_TYPE_WAKEUP_INIT_DONE");
					// 唤醒已经打开 已经初始化完成，可以说命令词了
					mSessionManager.onWakeUpInitDone();
					break;
				case SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS:
					Logger.d(TAG, "!--->VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS");
					boolean isCamera = isCameraActivity();
					if(isCamera){
						goHomePage();
					}
					mWakeupSuccessType = SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS;
					updateMicControlView(mWakeupSuccessType);
					mSessionManager.onNormalWakeUpSuccess();
					break;
				case SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO:
					// 我想哼歌 唤醒成功
					Logger.d(TAG,
							"!--->VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO");
					mWakeupSuccessType = SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO;
					updateMicControlView(mWakeupSuccessType);
					mSessionManager.onWakeUpSuccessDoreso();
					break;
				case SessionPreference.VALUE_SET_STATE_TYPE_ASR_COMPILE_FINISH:
					Logger.d(TAG,
							"!--->VALUE_SET_STATE_TYPE_ASR_COMPILE_FINISH");
					// 语法数据编译完成
					mSessionManager.onTalkDataDone();
					showWakeupStatusToast(); // zzj 20150819 added
					Constant.setFirstStart(mContext, false); // first start has
																// finished.
					requestSupportDomainList(); // zzj added 20150702
					// 在第一次数据编译完成后，再监听数据变化 //LP added 20151104

					if (appsDataModel != null) {
						appsDataModel.registerReceiver();
					}
					break;
				case SessionPreference.VALUE_SET_STATE_TYPE_WRITE_CONTACT_INFO:
					Logger.d(TAG,"!--->VALUE_SET_STATE_TYPE_WRITE_CONTACT_INFO----");
					writeContactsInfo();
					break;
				case SessionPreference.VALUE_SET_STATE_TYPE_WRITE_MEDIA_INFO:
					writeMediasInfo();
					break;
				case SessionPreference.VALUE_SET_STATE_TYPE_WRITE_APPS_INFO:
					writeAppsInfo();
					break;
				case SessionPreference.VALUE_SET_STATE_TYPE_GET_SUPPORT_DOMAIN_LIST:
					Logger.d(TAG,"!--->VALUE_SET_STATE_TYPE_GET_SUPPORT_DOMAIN_LIST----");
					// TODO:
					String supportListProtocol = "";
					onGetSupportListProtocol(supportListProtocol);
					break;

				default:
					break;
				}
				break;
			case MSG_ON_CONTROL_WAKEUP_SUCCESS:
				String wakeupWord = (String) msg.obj;
				mSessionManager.onControlWakeUpSuccess(wakeupWord);
				break;
			case MSG_ON_RECORDING_PREPARED:
				Logger.d(TAG, "!--->MSG_ON_RECORDING_PREPARED----");
	//			getGeneralGPS();
				mSessionManager.onTalkRecordingPrepared();
				break;
			case MSG_ON_RECORDING_EXCEPTION:
				Logger.d(TAG, "!--->MSG_ON_RECORDING_EXCEPTION-----");
				mSessionManager.onTalkRecordingException();
				break;
			case MSG_ON_RECORDING_START:
				Logger.d(TAG, "!--->MSG_RECORDING_START----");
				mSessionManager.onTalkRecordingStart();
				break;
			case MSG_ON_RECORDING_STOP:
				Logger.d(TAG, "!--->MSG_ON_RECORDING_STOP----");
				// 正在识别
				mSessionManager.onTalkRecordingStop();
				break;
			case MSG_ON_RECORDING_RESULT:
				String result = (String) msg.obj;
				/* < zzj 20150906 modify for ASR partial result Begin */
				Logger.d(TAG, "!--->MSG_ON_RECORDING_RESULT----result = "
						+ result);
				// {"result":"打电话","resultType":"full"}
				JSONObject resultObj = JsonTool.parseToJSONObject(result);
				String resultType = JsonTool.getJsonValue(resultObj,
						"resultType", "full"); // "partial"
				String text = JsonTool.getJsonValue(resultObj, "result");
				boolean isPartial = false;
				if ("partial".equals(resultType)) {
					isPartial = true;
				}
				mSessionManager.onTalkRecordingResult(text, isPartial);
				/* zzj 20150906 modify for ASR partial result End > */
				break;
			case MSG_ON_SESSION_PROTOCAL:
				Logger.d(TAG, "!--->MSG_ON_SESSION_PROTOCAL----");
				String sessionProtocol = (String) msg.obj;
				mSessionManager.onSessionProtocal(sessionProtocol);
				break;
			case MSG_ON_TTS_PLAY_END:
				Logger.d(TAG, "!--->MSG_ON_TTS_PLAY_END----");
				mSessionManager.onTTSPlayEnd();
				break;
			case MSG_ON_RECOGNIZER_TIMEOUT:
				Logger.d(TAG, "!--->MSG_ON_RECOGNIZER_TIMEOUT----");
				mSessionManager.onRecognizerTimeout();
				break;
			case MSG_GUI_CANCEL_SESSION:
				Logger.d(TAG, "!--->MSG_GUI_CANCEL_SESSION--cancelTalk--");
				sendCancelEvent();
				// mSessionManager.dismissWindowService(); //DELET do this on
				// onCTTCancel()
				// system call
				break;
			case MSG_GUI_CANCEL_WAITTING_SESSION:
				Logger.d(TAG,
						"!--->MSG_GUI_CANCEL_WAITTING_SESSION--sendWaittingCancelEvent--");
				sendWaittingCancelEvent();
				break;
			case MSG_ON_CTT_CANCEL:
				Logger.d(TAG, "!--->MSG_ON_CTT_CANCEL----");
				mSessionManager.onCTTCancel();
				break;
			case MSG_ON_ONESHOT_RECOGNIZER_TIMEOUT:
				Logger.d(TAG, "!--->MSG_ON_ONESHOT_RECOGNIZER_TIMEOUT----");
				mSessionManager.onOneShotRecognizerTimeOut();
				break;
			case MSG_ON_START_RECORDING_FAKE_ANIMATION:
				Logger.d(TAG, "!--->MSG_ON_START_RECORDING_FAKE_ANIMATION----");
				mSessionManager.onStartRecordingFakeAnimation();
				break;
			case MSG_ON_GET_WAKEUP_WORDS:
				String wakeupWords = (String) msg.obj;
				Logger.d(TAG, "!--->MSG_ON_GET_WAKEUP_WORDS----wakeupWords = "
						+ wakeupWords);
				UserPerferenceUtil.setWakeupWords(mContext, wakeupWords);
				break;
			case MSG_ON_CLICK_MAIN_ACTION_BUTTON:
				int style = (Integer) msg.obj;
				Logger.d(TAG, "!--->MSG_ON_CLICK_MAIN_ACTION_BUTTON--- style-"
						+ style);
				switch (style) {
				case 23:// call

					break;
				case 24:// navi

					break;
				case 25:// music

					break;
				case 26:// localsearch

					break;

				default:
					break;
				}
				break;
			case MSG_ON_UPDATE_WAKEUP_WORDS_STATUS:
				String status = (String) msg.obj;
				Logger.d(TAG,
						"!--->MSG_ON_UPDATE_WAKEUP_WORDS_STATUS status : "
								+ status);
				mSessionManager.onCTTCancel();
				break;
			case MSG_ON_RECORDING_IDLE:
				Logger.d(TAG, "!--->MSG_ON_RECORDING_IDLE");
				mSessionManager.onTalkRecordingIdle();
				break;
			case MSG_ON_ACC_ON:
				if (accSwitch) {
					if (UserPerferenceUtil.isWakeupEnable(mContext)) {
						sendAccOpenEvent();
						ContentValues con = new ContentValues();
						con.put(WakeUpDB.SWITCH, 1);
						getContentResolver().update(WakeUpSwitch.CONTENT_URI,
								con, null, null);
					} else {
						// sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_STOP);
						ContentValues con = new ContentValues();
						con.put(WakeUpDB.SWITCH, 0);
						getContentResolver().update(WakeUpSwitch.CONTENT_URI,
								con, null, null);
					}
				}
				break;
			case MSG_ON_ACC_OFF:
				if (UserPerferenceUtil.isWakeupEnable(mContext)) {
					sendAccCloseEvent();
					ContentValues con = new ContentValues();
					con.put(WakeUpDB.SWITCH, 0);
					getContentResolver().update(WakeUpSwitch.CONTENT_URI, con,
							null, null);
				}

				break;
			default:
				break;
			}
		};
	};

	/**
	 * Sms Send Status Receiver zzj added 20150709
	 */
	private BroadcastReceiver mSmsSendStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.d(TAG,
					"!--->mSmsSendStatusReceiver Action:" + intent.getAction());
			if (GUIConfig.ACTION_SMS_SEND_SUCCESS.equals(intent.getAction())) {
				mSessionManager.showSmsSendStatusView(GUIConfig.SMS_STATUS_SEND_SUCCESS);
			} else if (GUIConfig.ACTION_SMS_SEND_FAIL
					.equals(intent.getAction())) {
				mSessionManager
						.showSmsSendStatusView(GUIConfig.SMS_STATUS_SEND_FAIL);
			}
		}

	};

	/**
	 * TODO: zzj added 20150702
	 * 
	 * @param supportListProtocol
	 */
	private void onGetSupportListProtocol(String supportListProtocol) {
		Logger.d(TAG, "!--->onGetSupportListProtocol()--supportListProtocol = "
				+ supportListProtocol);
		// mOnlineSupportList = null;
		// mOfflineSupportList = null;
	}

	/**
	 * zzj added 20150702
	 * 
	 * @param hasNetWork
	 * @return
	 */
	public static ArrayList<String> getSupportList(boolean hasNetWork) {
		Logger.d(TAG, "!--->getSupportList----hasNetWork=" + hasNetWork);
		if (null == mOnlineSupportList && null == mOfflineSupportList) {
			testAddSupportList();
		}

		if (hasNetWork) {
			return mOnlineSupportList;
		} else {
			return mOfflineSupportList;
		}
	}

	/**
	 * test function for Support List
	 */
	private static void testAddSupportList() {
		mOnlineSupportList = new ArrayList<String>();
		mOnlineSupportList.add(SessionPreference.DOMAIN_CALL);
		if (GUIConfig.isShowSMSFunctionHelp) {
			mOnlineSupportList.add(SessionPreference.DOMAIN_SMS);
		}
		mOnlineSupportList.add(SessionPreference.DOMAIN_MUSIC);
		mOnlineSupportList.add(SessionPreference.DOMAIN_AUDIO);
		mOnlineSupportList.add(SessionPreference.DOMAIN_ROUTE);
		mOnlineSupportList.add(SessionPreference.DOMAIN_WEATHER);
		mOnlineSupportList.add(SessionPreference.DOMAIN_SETTING);
		mOnlineSupportList.add(SessionPreference.DOMAIN_STOCK);
		mOnlineSupportList.add(SessionPreference.DOMAIN_LOCAL);
		mOnlineSupportList.add(SessionPreference.DOMAIN_TRAFFIC);
		mOnlineSupportList.add(SessionPreference.DOMAIN_LIMIT);
		mOnlineSupportList.add(SessionPreference.DOMAIN_BROADCAST);

		mOfflineSupportList = new ArrayList<String>();
		mOfflineSupportList.add(SessionPreference.DOMAIN_CALL);
		mOfflineSupportList.add(SessionPreference.DOMAIN_MUSIC);
		mOfflineSupportList.add(SessionPreference.DOMAIN_BROADCAST);
		mOfflineSupportList.add(SessionPreference.DOMAIN_SETTING);
	}

	private long mLocateTime = 0;
	public static LocationInfo mLocationInfo = null;
	private long getGPSCacheTime = 20000;

	private void getGeneralGPS() {
		Logger.d(TAG, "getGeneralGPS start mLocateTime : " + mLocateTime);
		final long now = System.currentTimeMillis();
		if (now - mLocateTime >= getGPSCacheTime || mLocateTime == 0) {
			Logger.d(TAG, "getGeneralGPS in!!!");
			LocationModelProxy locationModel = LocationModelProxy
					.getInstance(mContext);
			locationModel.setLocationListener(new ILocationListener() {
				@Override
				public void onLocationResult(List<LocationInfo> infos,
						ErrorUtil code) {
				}

				@Override
				public void onLocationChanged(LocationInfo locationInfo,
						ErrorUtil errorUtil) {
					if (locationInfo != null) {
						Logger.d(TAG, "getGeneralGPS end Latitude : "
								+ locationInfo.getLatitude() + ";Longitude : "
								+ locationInfo.getLongitude() + ";city : "
								+ locationInfo.getCity());
						mLocateTime = now;
						mLocationInfo = locationInfo;
						if (mLocationInfo != null) {
							// sendProtocolEvent(SessionPreference.EVENT_NAME_GENERAL_GPS,
							// "{\"gpsInfo\":" + "\"" +
							// mLocationInfo.getLatitude() + ","
							// + mLocationInfo.getLongitude() + "\"}");

							sendProtocolEvent(
									SessionPreference.EVENT_NAME_GENERAL_GPS,
									"{\"gpsInfo\":" + "\""
											+ mLocationInfo.getLatitude() + ","
											+ mLocationInfo.getLongitude()
											+ "\",\"city\":"
											+ mLocationInfo.getCity() + "}");
						}
					}
				}
			});
			locationModel.startLocate();
		}

		if (mLocationInfo != null) {
			sendProtocolEvent(SessionPreference.EVENT_NAME_GENERAL_GPS,
					"{\"gpsInfo\":" + "\"" + mLocationInfo.getLatitude() + ","
							+ mLocationInfo.getLongitude() + "\"}");
		}
	}

	/* < zzj 20150805 added for Mic float View Begin */
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == mScreeFloatView.getFloatMicInstance()) {
				Logger.d(TAG, "!--->onClick mScreeFloatView");
				startTalk();
			}
		}
	};

	private OnSharedPreferenceChangeListener mPreferenceChangeListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {

			Logger.d(TAG, "!--->onSharedPreferenceChanged: key " + key);

			if (UserPerferenceUtil.KEY_ENABLE_FLOAT_MIC.equals(key)) {
				updateEnableFloatMic();
				if (mEnableFloatMic) {
					startFloatMicChecker(0);
				}
			}
		}
	};

	/**
	 * mScreenReceiver
	 */
	private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			Logger.d(TAG, "!--->mScreenReceiver--onReceive:intent " + intent);
			String action = intent.getAction();

			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				updateEnableFloatMic();
				startFloatMicChecker(0);
				mSessionManager.onResume();

			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				stopFloatMicChecker();
				mSessionManager.onPause();

			} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
				stopFloatMicChecker();
				mSessionManager.onResume();

			} else if (ACTION_CUSTOMFROMBT.equals(action)) {
				sendEventObject(SessionPreference.EVENT_NAME_SAVE_CONTACTS_DONE);

			} else if (ACTION_ACC_OFF.equals(action)) {

				if (!accSwitch) {

					context.sendBroadcast(new Intent(COM_GLSX_BOOT_ACCOFF));
					accSwitch = false;
					curValue = getAudioManager().getStreamVolume(
							AudioManager.STREAM_ALARM);
					mUserPreference.putInt("volumn", curValue);
					setVolumn(0);
					mUserPreference.putBoolean("acc", false);
					if (mViewRoot.isShown()) {
						mSessionManager.cancelTalk();
					}

					mUIHandler.sendEmptyMessageDelayed(MSG_ON_ACC_OFF, 2000);
					MessageSender messageSender = new MessageSender(context);
					Intent stop = new Intent(CommandPreference.SERVICECMD);
					stop.putExtra(CommandPreference.CMDNAME,
							CommandPreference.CMDSTOP);
					messageSender.sendOrderedMessage(stop, null);

					Intent ecar = new Intent("com.android.ecar.recv");
					ecar.putExtra("ecarSendKey", "ACC_OFF");
					ecar.putExtra("cmdType", "standCMD");
					ecar.putExtra("keySet", "");
					context.sendBroadcast(ecar);

				}

			} else if (ACTION_ACC_ON.equals(action)) {

				accSwitch = true;
				int storageVolume = mUserPreference.getInt("volumn", 12);
				setVolumn(storageVolume);
				mUserPreference.putBoolean("acc", true);
				mUIHandler.sendEmptyMessageDelayed(MSG_ON_ACC_ON, 7000);

				if (mUserPreference.getFloat("lat", 0f) != 0f
						&& mUserPreference.getFloat("lng", 0f) != 0f) {
					CreateDialog(context);
				}

				Intent ecar = new Intent("com.android.ecar.recv");
				ecar.putExtra("ecarSendKey", "ACC_ON");
				ecar.putExtra("cmdType", "standCMD");
				ecar.putExtra("keySet", "");
				context.sendBroadcast(ecar);

				// 广联一键通
				context.sendBroadcast(new Intent(COM_GLSX_BOOT_ACCON));

			} else if (ACTION_CLOSE_WAKEUP.equals(action)) {

				mSessionManager.cancelSession();
				mSessionManager.onPause();
				if (UserPerferenceUtil.isWakeupEnable(mContext)) {
					sendAccCloseEvent();
					ContentValues con = new ContentValues();
					con.put(WakeUpDB.SWITCH, 0);
					getContentResolver().update(WakeUpSwitch.CONTENT_URI, con,
							null, null);
				}

			} else if (ACTION_OPEN_WAKEUP.equals(action)) {

				if (UserPerferenceUtil.isWakeupEnable(mContext)) {
					sendAccOpenEvent();
					ContentValues con = new ContentValues();
					con.put(WakeUpDB.SWITCH, 1);
					getContentResolver().update(WakeUpSwitch.CONTENT_URI, con,
							null, null);

				} else {

					ContentValues con = new ContentValues();
					con.put(WakeUpDB.SWITCH, 0);
					getContentResolver().update(WakeUpSwitch.CONTENT_URI, con,
							null, null);
				}

			} else if (PLAY_TTS.equals(action)) {

				playText = intent.getStringExtra("content");
				id = intent.getIntExtra("id", -1);
				Logger.d(TAG, "!--->playText= " + playText + "--->playTTs="
						+ playTTs);

				if (playTTs) {

					TTSController.getInstance(context).playText(playText, id);
					playText = null;

				}
			} else if (GL_PLAY_TTS.equals(action)) {

				playText = intent.getStringExtra("ttsText");
				if (playTTs) {
					TTSController.getInstance(context).playText(playText);
					playText = null;
				}

			}else if (ACTION_CONTACT_START.equals(action)) {

				startDownPhone = true;

			} else if (ACTION_CUSTOMFROMBT.equals(action)) {

				sendEventObject(SessionPreference.EVENT_NAME_SAVE_CONTACTS_DONE);

			} else if (ACTION_CHANGE_VOLUME.equals(action)) {
				if (mPlayer == null) {
					mPlayer = new MediaPlayer();
				}
				boolean isPlaying = false;
				try {
					isPlaying = mPlayer.isPlaying();
				} catch (IllegalStateException e) {
					mPlayer = null;
					mPlayer = new MediaPlayer();
				}
				if (mPlayer != null && !isPlaying && accSwitch) {

					curValue = getAudioManager().getStreamVolume(
							AudioManager.STREAM_ALARM);

					Logger.d(TAG, "curValue : " + curValue);

					if (curValue > 0) {
						ismute = true;
						mPlayer.reset();
						AssetFileDescriptor afd = context.getResources()
								.openRawResourceFd(R.raw.mute);
						try {
							mPlayer.setDataSource(afd.getFileDescriptor(),
									afd.getStartOffset(), afd.getLength());
							mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
							mPlayer.prepare();
							mPlayer.setOnCompletionListener(mOnCompletionListener);
							mPlayer.start();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

					} else {
						int storageVolume = Settings.System.getInt(
								mContext.getContentResolver(),
								KEY_VOLUME_VALUE, 7);
						Logger.d(TAG, "storageVolume : " + storageVolume);
						getAudioManager().setStreamVolume(
								AudioManager.STREAM_ALARM, storageVolume, 0);
						ismute = false;
						mPlayer.reset();
						AssetFileDescriptor afd = context.getResources()
								.openRawResourceFd(R.raw.cancel_mute);
						try {
							mPlayer.setDataSource(afd.getFileDescriptor(),
									afd.getStartOffset(), afd.getLength());
							mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
							mPlayer.prepare();
							mPlayer.start();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} else if (ACTION_REMOTE_VOICE_CONTROL.equals(action)) {
				if (accSwitch) {
					String keyword = intent.getStringExtra("content");
					keyword = StringUtil.clearSpecialCharacter(keyword);
					Logger.d("key", keyword);
					sendPTT();
					sendResponse("");
					sendProtocolEvent(
							SessionPreference.EVENT_NAME_RECORDING_CONTROL,
							GuiProtocolUtil.getRecordingControlParamProtocol(
											SessionPreference.PARAM_RECORDING_CONTROL_STOP,
											SessionPreference.DOMAIN_ROUTE));
					sendProtocolEvent(
							SessionPreference.EVENT_NAME_UPDATE_LOCALSEARCH_KEYWORD,
							GuiProtocolUtil.getChangeLocationParamProtocol(keyword));
				}
			} else if (Intent.ACTION_CONFIGURATION_CHANGED.equals(action)) {
				Locale locale = getResources().getConfiguration().locale;
				language = locale.getCountry();
			}
			// else if (ACTION_UPDATE_WAKEUP_WORD.equals(action)) {
			// Logger.d(TAG, "!--->mScreenReceiver--ACTION_UPDATE_WAKEUP_WORD");
			// String newText = intent.getStringExtra(EXTRA_KEY_WAKEUP_WORD);
			// String oldWakeupWord =
			// UserPerferenceUtil.getWakeupWord(mContext);
			// sendUpdateWakeupWordEvent(oldWakeupWord, newText);
			// }
		}

	};

	private void updateVolumeValue(int volumeValue) {
		Logger.d(TAG, "updateVolumeValue---" + volumeValue);
		try {
			Settings.System.putInt(mContext.getContentResolver(),
					KEY_VOLUME_VALUE, volumeValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendEventObject(String eventName) {
		JSONObject appJsonObject = new JSONObject();
		try {
			appJsonObject.put("type", "EVENT");
			JSONObject data = new JSONObject();
			data.put("moduleName", "GUI");
			data.put("eventName", eventName);
			appJsonObject.put("data", data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendMsg(appJsonObject.toString());
	}

	/**
	 * register mScreenReceiver zzj added 20150805
	 */
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		filter.addAction(ACTION_ACC_ON);
		filter.addAction(ACTION_ACC_OFF);
		filter.addAction(ACTION_OPEN_WAKEUP);
		filter.addAction(ACTION_CLOSE_WAKEUP);
		filter.addAction(ACTION_CONTACT_START);
		filter.addAction(PLAY_TTS);
		filter.addAction(GL_PLAY_TTS);
		filter.addAction(ACTION_CHANGE_VOLUME);
		filter.addAction(ACTION_CUSTOMFROMBT);
		filter.addAction(ACTION_REMOTE_VOICE_CONTROL);
		filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		// filter.addAction(ACTION_UPDATE_WAKEUP_WORD);

		registerReceiver(mScreenReceiver, filter);
	}

	/**
	 * unregister mScreenReceiver zzj added 20150805
	 */
	private void unregisterReceiver() {
		unregisterReceiver(mScreenReceiver);
	}

	private void startFloatMicChecker(long delay) {
		// Logger.d(TAG, "startFloatMicChecker: delay " + delay);
		mHandler.postDelayed(mRunnable, delay);
	}

	private void stopFloatMicChecker() {
		Logger.d(TAG, "stopFloatMicChecker");
		setFloatMicEnable(false);
		mHandler.removeCallbacks(mRunnable);
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			if (mEnableFloatMic) {
				// PackageUtil.isHome(WindowService.this, mLauncherPackage)
				if (PackageUtil.isNeedHideFloatView(WindowService.this)
						|| isNeedHideMicFloatView) {
					hideFloatView();
				} else {
					showFloatView();
				}
				startFloatMicChecker(800);
			} else {
				hideFloatView();
			}
		}
	};

	/**
	 * setNeedHideMicFloatView for some special case zzj added 20150811
	 * 
	 * @param isNeedHide
	 */
	public void setNeedHideMicFloatView(boolean isNeedHide) {
		Logger.d(TAG, "!--->setNeedHideMicFloatView---isNeedHide = "
				+ isNeedHide);
		isNeedHideMicFloatView = isNeedHide;
	}

	public void showFloatView() {
		if (!mScreeFloatView.isShown()) {
			Logger.d(TAG, "!--->showFloatView---");
			mScreeFloatView.show();
		}
	}

	public void hideFloatView() {
		if (mScreeFloatView.isShown()) {
			Logger.d(TAG, "!--->hideFloatView---");
			mScreeFloatView.hide();
		}
	}

	private void updateEnableFloatMic() {
		Logger.d(TAG, "updateEnableFloatMic---isShowFloatMicView = "
				+ UserPerferenceUtil.getFloatMicEnable(this));
		setFloatMicEnable(UserPerferenceUtil.getFloatMicEnable(this));
	}

	private void setFloatMicEnable(boolean enable) {
		Logger.d(TAG, "setFloatMicEnable: enable " + enable);
		mEnableFloatMic = enable;
	}

	/* zzj 20150805 added for Mic float View End > */

	/* < zzj 20150813 added for cancel button click Begin */
	/**
	 * 
	 */
	public void onCancelTalk() {
		mUIHandler.sendEmptyMessage(MSG_GUI_CANCEL_SESSION);
		mUIHandler.removeMessages(MSG_ON_RECORDING_PREPARED);
	}

	/**
	 * zzj 20150813 added
	 */
	public void onWaittingSessionCancel() {
		mUIHandler.sendEmptyMessage(MSG_GUI_CANCEL_WAITTING_SESSION);
	}

	/* zzj 20150813 added for for cancel button click End > */

	/**
	 * Add default Location 20150909 从assets/config.mg中读取位置信息，若没有配置，缺省使用上海公司位置信息
	 * 应jason要求,一定要加一个缺省的位置信息，规避第一次定位就失败的情况。
	 */
	/*
	 * private void setDefaultLocation() { JSONObject mLocationJsonObject =
	 * JsonTool .parseToJSONObject(PrivatePreference.mDeufaultLocation);
	 * mLocationInfo = new LocationInfo();
	 * mLocationInfo.setCityCode(JsonTool.getJsonValue(mLocationJsonObject,
	 * "cityCode", "021"));
	 * mLocationInfo.setLatitude(JsonTool.getJsonValue(mLocationJsonObject,
	 * "lat", 31.177598));
	 * mLocationInfo.setLongitude(JsonTool.getJsonValue(mLocationJsonObject,
	 * "lng", 121.401098));
	 * mLocationInfo.setType(JsonTool.getJsonValue(mLocationJsonObject, "type",
	 * 5)); mLocationInfo.setAddress(JsonTool.getJsonValue(mLocationJsonObject,
	 * "address", "上海市 徐汇区 钦州北路 靠近电信恒联"));
	 * mLocationInfo.setDistrict(JsonTool.getJsonValue(mLocationJsonObject,
	 * "destrict", "徐汇区"));
	 * mLocationInfo.setCity(JsonTool.getJsonValue(mLocationJsonObject, "city",
	 * "上海")); }
	 */
	public void playTTs(String ttsContent, int type) {
		switch (type) {
		case RomCustomerProcessing.TTS_END_WAKEUP_START:
			sendProtocolEvent(SessionPreference.EVENT_NAME_PLAY_TTS,
					GuiProtocolUtil.getPlayTTSEventParam(ttsContent, "WAKEUP"));
			break;
		case RomCustomerProcessing.TTS_END_RECOGNIER_START:
			sendProtocolEvent(SessionPreference.EVENT_NAME_PLAY_TTS,
					GuiProtocolUtil.getPlayTTSEventParam(ttsContent,
							"RECOGNIZER"));
			break;
		default:
			break;
		}
	}

	/* < zzj 20151015 added for location change Begin */

	private MicrophoneViewClickListener mMicrophoneViewClickListener = new MicrophoneViewClickListener() {

		@Override
		public void onWakeupWordClick() {
			Logger.d(TAG, "onWakeupWordClick----");
			// showChangeTextPopWindow(POP_TYPE_EDIT_WAKEUP_WORD);
			showEditWakeupwordPopWindow(mContext);
		}
	};

	/**
	 * show Edit Wakeupword PopWindow
	 * 
	 * @author zzj
	 * @date 2015-11-01
	 * @param context
	 */
	private void showEditWakeupwordPopWindow(Context context) {
		Logger.d(TAG, "showEditWakeupwordPopWindow----");
		sendProtocolEvent(
				SessionPreference.EVENT_NAME_UPDATE_WAKEUP_WORD_TIMEOUT_SWITCH,
				GuiProtocolUtil
						.getTimeOutParamProtocol(SessionPreference.EVENT_PARAM_SWITCH_CLOSE));
		EditWakeupWordPopWindow pop = new EditWakeupWordPopWindow(context);
		pop.setOnEditWakeupwordPopListener(mOnEditWakeupwordPopListener);
		pop.showPopWindow(mSessionContainer);
	}

	/**
	 * mOnEditWakeupwordPopListener
	 * 
	 * @author zzj
	 * @date 2015-11-01
	 */
	private OnEditWakeupwordPopListener mOnEditWakeupwordPopListener = new OnEditWakeupwordPopListener() {
		@Override
		public void onOkClick(String wakeupWord) {
			Logger.d(TAG,
					"mOnEditWakeupwordPopListener-onOkClick-wakeupWord = "
							+ wakeupWord);
			String oldWakeupWord = UserPerferenceUtil.getWakeupWord(mContext);
			sendUpdateWakeupWordEvent(oldWakeupWord, wakeupWord);
		}

		@Override
		public void onCancelClick() {
			Logger.d(TAG, "mOnEditWakeupwordPopListener - onCancelClick");
			sendProtocolEvent(
					SessionPreference.EVENT_NAME_UPDATE_WAKEUP_WORD_TIMEOUT_SWITCH,
					GuiProtocolUtil
							.getTimeOutParamProtocol(SessionPreference.EVENT_PARAM_SWITCH_OPEN));
		}
	};

	/**
	 * show Edit Location PopWindow
	 * 
	 * @author zzj
	 * @date 2015-11-18
	 * @param context
	 */
	public void showEditLocationPopWindow(Context context) {
		Logger.d(TAG, "showEditLocationPopWindow----");
		sendProtocolEvent(
				SessionPreference.EVENT_NAME_UPDATE_POI_TIMEOUT_SWITCH,
				GuiProtocolUtil
						.getTimeOutParamProtocol(SessionPreference.EVENT_PARAM_SWITCH_CLOSE));
		EditLocationPopWindow pop = new EditLocationPopWindow(context);
		pop.setOnEditWakeupwordPopListener(mOnEditLocationPopListener);
		pop.showPopWindow(mSessionContainer);
	}

	/**
	 * mOnEditLocationPopListener
	 * 
	 * @author zzj
	 * @date 2015-11-18
	 */
	private OnEditLocationPopListener mOnEditLocationPopListener = new OnEditLocationPopListener() {
		@Override
		public void onOkClick(String location) {
			Logger.d(TAG, "mOnEditLocationPopListener-onOkClick-location = "
					+ location);
			if (!TextUtils.isEmpty(location)) {
				sendkeywordEvent(
						SessionPreference.EVENT_NAME_UPDATE_POI_KEYWORD,
						location);
			}
		}

		@Override
		public void onCancelClick() {
			Logger.d(TAG, "mOnEditLocationPopListener-onCancelClick");
			sendProtocolEvent(
					SessionPreference.EVENT_NAME_UPDATE_POI_TIMEOUT_SWITCH,
					SessionPreference.EVENT_PARAM_SWITCH_OPEN);
		}
	};

	/* < zzj 20151015 added for location change End */

	private void initAudioFoucus() {
		mAudioFocusHelper = new AudioFocusHelper(mContext);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void initServiceForeground() {
		try {
			mStartForeground = WindowService.class.getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = WindowService.class.getMethod("stopForeground",
					mStopForegroundSignature);
		} catch (NoSuchMethodException e) {
			mStartForeground = mStopForeground = null;
		}

		try {
			mSetForeground = getClass().getMethod("setForeground",
					mSetForegroundSignature);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(
					"OS doesn't have Service.startForeground OR Service.setForeground!");
		}

		Notification.Builder builder = new Notification.Builder(mContext);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle("uniDrive");
		builder.setContentText("uniDrive Service");
		Notification notification = builder.build();
		PendingIntent pendingintent = PendingIntent.getActivity(this, 0,
				new Intent(this, GUIMainActivity.class), 0);
		notification.setLatestEventInfo(this, "uniCarSolution",
				"uniDrive Service", pendingintent);

		startForegroundCompat(NOTIFICATION_ID, notification);
	}

	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
			Logger.e(TAG, "Unable to invoke method : " + e);
		} catch (IllegalAccessException e) {
			// Should not happen.
			Logger.e(TAG, "Unable to invoke method : " + e);
		}
	}

	/**
	 * startForeground
	 */
	private void startForegroundCompat(int id, Notification notification) {
		if (mReflectFlg) {
			if (mStartForeground != null) {
				mStartForegroundArgs[0] = Integer.valueOf(id);
				mStartForegroundArgs[1] = notification;
				invokeMethod(mStartForeground, mStartForegroundArgs);
				return;
			}

			mSetForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mSetForeground, mSetForegroundArgs);
			mNM.notify(id, notification);
		} else {
			if (VERSION.SDK_INT >= 5) {
				startForeground(id, notification);
			} else {
				mSetForegroundArgs[0] = Boolean.TRUE;
				invokeMethod(mSetForeground, mSetForegroundArgs);
				mNM.notify(id, notification);
			}
		}
	}

	/**
	 * stopForeground
	 */
	private void stopForegroundCompat(int id) {
		if (mReflectFlg) {
			if (mStopForeground != null) {
				mStopForegroundArgs[0] = Boolean.TRUE;
				invokeMethod(mStopForeground, mStopForegroundArgs);
				return;
			}
			mNM.cancel(id);
			mSetForegroundArgs[0] = Boolean.FALSE;
			invokeMethod(mSetForeground, mSetForegroundArgs);
		} else {
			if (VERSION.SDK_INT >= 5) {
				stopForeground(true);
			} else {
				mNM.cancel(id);
				mSetForegroundArgs[0] = Boolean.FALSE;
				invokeMethod(mSetForeground, mSetForegroundArgs);
			}
		}
	}

	private void initPinyinConverter() {
		// 由于采用自定义的获取联系人方式，并没有初始化拼音模块，所以在服务创建时初始化好
		// 规避在收到同步完联系人广播后再去初始化，避免耗时导致生成cop文件中全拼字段异常
		// update by LP at 2015-11-24
		try {
			Logger.d(TAG, "initPinyinConverter");
			PinyinConverter.init(mContext.getAssets().open("un2py.mg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
					UserPreference userPreference = new UserPreference(
							WindowService.this);
					userPreference.putFloat("lat", 0f);
					userPreference.putFloat("lng", 0f);
					userPreference.putString("address", "");
					mAlertDialog.dismiss();
				}
				break;
			}

		}
	};

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
		((TextView) v.findViewById(R.id.textView1)).setText(getString(
				R.string.prepnavi_endpoi, mUserPreference.getString("address",
						getString(R.string.prepnavi_address))));
		builder.setView(v);
		Intent intent = new Intent(PLAY_TTS);
		intent.putExtra("content", getString(R.string.receive_prepnavi_command));
		context.sendBroadcast(intent);
		mAlertDialog = builder.create();
		mAlertDialog.setCanceledOnTouchOutside(false);
		mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);// 显示
		mAlertDialog.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
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
		int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
		switch (mapIndex) {
		case 1:
			Gps gcj02 = PositionUtil.bd09_To_Gcj02(
					mUserPreference.getFloat("lat", 0f),
					mUserPreference.getFloat("lng", 0f));
			GaodeUriApi.startNavi(mContext, gcj02.getWgLat(), gcj02.getWgLon(),
					mUserPreference.getString("address", ""), 2, 0);
			break;
		case 3:
			Gps gcj = PositionUtil.bd09_To_Gps84(
					mUserPreference.getFloat("lat", 0f),
					mUserPreference.getFloat("lng", 0f));
			KLDUriApi.startNavi(mContext, gcj.getWgLat(), gcj.getWgLon(),
					mUserPreference.getString("address", ""));
			break;
		case 4:
			Gps gps = PositionUtil.bd09_To_Gps84(
					mUserPreference.getFloat("lat", 0f),
					mUserPreference.getFloat("lng", 0f));
			GoogleUriApi.showRoute(mContext, gps.getWgLat(), gps.getWgLon());
			break;
		case 5:
			Gps gd = PositionUtil.bd09_To_Gcj02(
					mUserPreference.getFloat("lat", 0f),
					mUserPreference.getFloat("lng", 0f));
			Intent i = new Intent("com.mxnavi.mxnavi.PTT_DEST_ACTION");
			String dest_string = "(TND,2,0, " + gd.getWgLat() + ","
					+ gd.getWgLon() + "," + mUserPreference.getString("address", "") + ")";
			i.putExtra("data", dest_string);
			i.setFlags(32);
			mContext.sendBroadcast(i);
			break;
		default:
			Intent intent = new Intent();
			intent.setData(Uri.parse("bdnavi://plan?dest="
					+ mUserPreference.getFloat("lat", 0f) + ","
					+ mUserPreference.getFloat("lng", 0f) + ","
					+ mUserPreference.getString("address", "") + ")"));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mContext.startActivity(intent);
			break;
		}
		mUserPreference.putFloat("lat", 0f);
		mUserPreference.putFloat("lng", 0f);
		mUserPreference.putString("address", "");
		mAlertDialog.dismiss();
	}

	/**
	 * by ZZJ
	 */
	private Runnable mRecoverRunnable = new Runnable() {
		@Override
		public void run() {
			if (!mViewRoot.isShown())
				recover();
		}
	};
	private WakeLock mWakeLock;
	private PowerManager mPowerManager;

	/**
	 * by ZZJ
	 */
	private void recover() {
		int v = getAudioManager().getStreamVolume(AudioManager.STREAM_ALARM);
		getAudioManager().setStreamVolume(AudioManager.STREAM_RING, v, 0);
		if (v > 12) {
			v = 12;
		}
		getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
	}

	private void setVolumn(int volumn) {
		getAudioManager().setStreamVolume(AudioManager.STREAM_RING,volumn, 0);
		getAudioManager().setStreamVolume(AudioManager.STREAM_ALARM, volumn, 0);
		if(volumn > 12){
			volumn=12;
		}
		getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, volumn, 0);
	}

	/**
	 * by ZZJ
	 */
	private AudioManager getAudioManager() {
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		}
		return mAudioManager;
	}

	public void requestAudioFocus() {

		Logger.d(TAG, "requestAudioFocus");
		if (mAudioFocusHelper != null) {
			mAudioFocusHelper.requestAudioFocus();
		}

	}

	public void abandonAudioFocus() {

		Logger.d(TAG, "abandonAudioFocus");
		if (mAudioFocusHelper != null) {
			mAudioFocusHelper.abandonAudioFocus();
		}

	}

	/**
	 * by ZZJ
	 */
	private void updateMapConfig() {
		int map_index = Settings.System.getInt(getContentResolver(),
				SettingMapViewPagerActivity.MAP_INDEX,
				UserPerferenceUtil.VALUE_MAP_DEFAULT);
		Logger.d(TAG, "updateMapConfig: " + map_index);
		if (map_index == 0) {
			UserPerferenceUtil.setMapChoose(mContext,
					UserPerferenceUtil.VALUE_MAP_BAIDU);
			try {
				SettingSession.forceStopPackage(
						SettingSession.AUTONAVI_MINIMAP, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				SettingSession.forceStopPackage(SettingSession.KLD_MAP, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (map_index == 2) {
			UserPerferenceUtil.setMapChoose(mContext,
					UserPerferenceUtil.VALUE_MAP_KLD);
			try {
				SettingSession.forceStopPackage(
						SettingSession.AUTONAVI_MINIMAP, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				SettingSession
						.forceStopPackage(SettingSession.BAIDU_NAVI, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (map_index == 3) {
			UserPerferenceUtil.setMapChoose(mContext,
					UserPerferenceUtil.VALUE_MAP_MEIX);
		} else if (map_index == 4) {
			UserPerferenceUtil.setMapChoose(mContext,
					UserPerferenceUtil.VALUE_MAP_TUBA);
			try {
				SettingSession.forceStopPackage(
						SettingSession.AUTONAVI_MINIMAP, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				SettingSession
						.forceStopPackage(SettingSession.BAIDU_NAVI, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			UserPerferenceUtil.setMapChoose(mContext,
					UserPerferenceUtil.VALUE_MAP_AMAP);
			try {
				SettingSession
						.forceStopPackage(SettingSession.BAIDU_NAVI, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				SettingSession.forceStopPackage(SettingSession.KLD_MAP, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// String poiVendor =
		// cn.yunzhisheng.preference.PrivatePreference.getValue("poi_vendor",UserPreference.MAP_VALUE_AMAP);

	}

	/**
	 * add by zzj 20150918
	 */
	private void acquireWakeLock(int flags, String tag) {
		if (mWakeLock == null || !mWakeLock.isHeld()) {
			if (mPowerManager == null) {
				mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			}
			mWakeLock = mPowerManager.newWakeLock(flags, tag);
			mWakeLock.acquire();
		}
	}

	// 释放设备电源锁i
	private void releaseWakeLock() {
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}
	
	public boolean isCameraActivity() {
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		// 应用程序位于堆栈的顶层
		if (tasksInfo != null) {
			ComponentName name = tasksInfo.get(0).topActivity;
			if(name!=null){
				String className = name.getClassName();
				if(CAMERA_ACTIVITY.equals(className)){
					return true;
				}
			}
		}
		return false;
	}
	
	void goHomePage(){
		Intent mHomeIntent = new Intent(Intent.ACTION_MAIN, null);
		mHomeIntent.addCategory(Intent.CATEGORY_HOME);
		mHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(mHomeIntent);
	}
}
