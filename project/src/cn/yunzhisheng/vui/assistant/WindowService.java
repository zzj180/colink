/**
 * Copyright (c) 2012-2013 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : WindowService.java
 * @ProjectName : Visored
 * @PakageName : cn.yunzhisheng.voizard.service
 * @Author : Bran
 * @CreateDate : 2013-5-27
 * @ModifiedBy : ZZJ
 */
package cn.yunzhisheng.vui.assistant;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
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

import com.android.kwmusic.KWMusicService;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.framework.service.IMessageRouterCallback;
import com.unisound.unicar.framework.service.IMessageRouterService;
import com.unisound.unicar.gui.data.interfaces.IBaseListener;
import com.unisound.unicar.gui.data.operation.AppsDataModel;
import com.unisound.unicar.gui.data.operation.ContactDataModel;
import com.unisound.unicar.gui.data.operation.MediaDataModel;
import com.unisound.unicar.gui.database.WakeUpDB;
import com.unisound.unicar.gui.database.WakeUpSwitch;
import com.unisound.unicar.gui.location.interfaces.ILocationListener;
import com.unisound.unicar.gui.location.operation.LocationModelProxy;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.msg.ISystemCallTransitionListener;
import com.unisound.unicar.gui.msg.SystemCallTransition;
import com.unisound.unicar.gui.oem.RomCustomerProcessing;
import com.unisound.unicar.gui.oem.RomSystemSetting;
import com.unisound.unicar.gui.preference.CommandPreference;
import com.unisound.unicar.gui.preference.Constant;
import com.unisound.unicar.gui.preference.PrivatePreference;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.preference.UserPreference;
import com.unisound.unicar.gui.route.operation.GaodeUriApi;
import com.unisound.unicar.gui.route.operation.KLDUriApi;
import com.unisound.unicar.gui.session.GUISessionManager;
import com.unisound.unicar.gui.session.SettingSession;
import com.unisound.unicar.gui.ui.MessageReceiver;
import com.unisound.unicar.gui.ui.MessageSender;
import com.unisound.unicar.gui.ui.SettingMapViewPagerActivity;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.ErrorUtil;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Gps;
import com.unisound.unicar.gui.utils.GuiProtocolUtil;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.PackageUtil;
import com.unisound.unicar.gui.utils.PositionUtil;
import com.unisound.unicar.gui.utils.TTSController;
import com.unisound.unicar.gui.view.MicrophoneControl;
import com.unisound.unicar.gui.view.MicrophoneControlDoresoView;
import com.unisound.unicar.gui.view.SessionContainer;
import com.unisound.unicar.gui.view.SessionLinearLayout;
import com.unisound.unicar.gui.view.SessionLinearLayout.DispatchKeyEventListener;
import com.unisound.unicar.gui.view.SessionLinearLayout.OnTouchEventListener;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2013-5-27
 * @ModifiedBy : ZZJ
 * @Modified: 2015-9-25: 实现基本功能
 */
@SuppressLint("InlinedApi")
public class WindowService extends Service {
	private static final String GANK = "bright";
	private static final String COM_GLSX_BOOT_ACCON = "com.glsx.boot.ACCON";
	private static final String COM_GLSX_BOOT_ACCOFF = "com.glsx.boot.ACCOFF";
	private static final String TAG = "WindowService";
	public static final String ACTION_SET_WAKEUP = "com.unisound.unicar.gui.ACTION.SET_WAKEUP";
	public static final String ACTION_SET_TTSSPEED = "com.unisound.unicar.gui.ACTION.SET_TTSSPEED";
	public static final String KEY_SET_TTSSPEED = "SET_TTSSPEED";
	public static final String ACTION_START_REQUEST_MAKE_FINISHED = "com.unisound.unicar.gui.ACTION.REQUEST_MAKE_FINISHED"; // XD
																															// added
	public static final String ACTION_ACC_ON = "android.intent.action.ACC_ON_KEYEVENT";
	public static final String ACTION_ACC_OFF = "android.intent.action.ACC_OFF_KEYEVENT";
	public static final String ACTION_CLOSE_WAKEUP = "android.intent.action.CLOSE_WAKEUP";
	public static final String ACTION_OPEN_WAKEUP = "android.intent.action.OPEN_WAKEUP";
	public static final String ACTION_START_TALK = "cn.yunzhisheng.intent.action.START_TALK";
	private static final String PLAY_TTS = "com.wanma.action.PLAY_TTS";
	private static final String GL_PLAY_TTS = "com.glsx.tts.speaktext";

	public static boolean accSwitch = true;
	private boolean playTTs = true;

	public static final String EXTRA_KEY_DISMISS_FLOAT_WINDOW = "DISMISS_FLOAT_WINDOW";
	public static final int MSG_SET_STATE = 2001;
	public static final int MSG_ON_RECORDING_START = 2002;
	public static final int MSG_ON_RECORDING_STOP = 2003;
	public static final int MSG_ON_SESSION_PROTOCAL = 2004;
	public static final int MSG_IS_ASR_COMPILE_DONE = 2005;
	public static final int MSG_ON_TTS_PLAY_END = 2007;
	public static final int MSG_ON_RECORDING_PREPARED = 2008;
	public static final int MSG_ON_RECORDING_RESULT = 2009;
	public static final int MSG_ON_RECORDING_EXCEPTION = 2010; // XD 20150807
																// added
	public static final int MSG_ON_RECOGNIZER_TIMEOUT = 2011;
	public static final int MSG_ON_CTT_CANCEL = 2012;
	public static final int MSG_ON_ACC_ON = 1988;
	public static final int MSG_ON_ACC_OFF= 1989;

	/* < XD 20150813 added Begin */
	public static final int MSG_GUI_CANCEL_SESSION = 3100;
	public static final int MSG_GUI_CANCEL_WAITTING_SESSION = 3101;
	
	/* < XD 20150914 added ZZJ */
//	private String CONSUMER_CODE = "ro.inet.consumer.code";
	
	/* < XD 20150916 added ZZJ */
	public static final String ACTION_CONTACT_DONE="com.colink.zzj.contact.donedial";
	public static final String ACTION_CONTACT_START="com.colink.zzj.contact.start";
	private static final String DOWNVOER = "通讯录导入结束";
	
	/* XD 20150813 added End > */

	private Point mWindowSize = new Point();
	private WindowManager mWindowManager;
	private SessionLinearLayout mViewRoot;
	private SessionContainer mSessionContainer;
	private MicrophoneControl mMicrophoneControl;
	private MicrophoneControlDoresoView mMicrophoneDoresoControl; //XD added 20150828
	private GUISessionManager mSessionManager = null;
	private WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
	private Context mContext;
	private IMessageRouterService mMessageRouterService;
	private SystemCallTransition messageTransCenter = null;
	// private boolean isNeedRequestIsASRCompileFinished = false;
	/* < XD 20150805 added for Mic float View Begin */
	private List<String> mLauncherPackage;
	// private FloatMicView mScreeFloatView;
	private boolean mPendingStartMicChecker;
//	private Handler mHandler = new Handler();
	/** is Need Hide MicFloatView for some special case */
	/* XD 20150805 added for Mic float View End > */

	/*< XD 20150828 added for Music Doreso Begin */
	/**
	 * wake up success type
	 * SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS
	 * SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO
	 */
	private int mWakeupSuccessType = SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS;
	
	public int getWakeupSuccessType() {
		return mWakeupSuccessType;
	}

	// xd added 20150707
	private static ArrayList<String> mOnlineSupportList = null;
	private static ArrayList<String> mOfflineSupportList = null;
	private String mSystemCallJson = "";
	String playText = null ;  //add by ZZJ 20150909
	private ContactDataModel contactDataModel = null;

	private MediaDataModel mediaDataModel = null;

	private AppsDataModel appsDataModel = null;

	private AudioManager mAudioManager;
	AlertDialog mAlertDialog;
	Button button1, button2;
	UserPreference mUserPreference;
	private boolean startDownPhone;

	private DispatchKeyEventListener mDispatchKeyEventListener = new DispatchKeyEventListener() {

		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			Logger.d(TAG,"!--->dispatchKeyEvent()---keyCode = " + event.getKeyCode());
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				mSessionManager.cancelTalk();
				return true;
			}
			return false;
		}
	};

	private OnTouchEventListener mOnTouchEventListener = new OnTouchEventListener() {

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			Logger.d(TAG, "!--->onTouchEvent()---Action = " + event.getAction());
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			if ((event.getAction() == MotionEvent.ACTION_DOWN) && ((x < 0) || (x >= mViewRoot.getWidth()) || (y < 0) || (y >= mViewRoot.getHeight()))) {
				mSessionManager.cancelTalk();
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
				mSessionManager.cancelTalk();
				return true;
			}
			return false;
		}

	};

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		Logger.d(TAG, "!--->onCreate");
		super.onCreate();
		mContext = this;
		PrivatePreference.init(mContext);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mUserPreference = new UserPreference(this);
		Constant.setFirstStart(this, true);// XD 20150820 added
		TTSController.getInstance(this).init();
		 Point point = new Point();  
		 mWindowManager.getDefaultDisplay().getRealSize(point);
		 if(point.x >= 1200){
			 UserPerferenceUtil.VALUE_MAP_DEFAULT=0;
		 }else{
			 UserPerferenceUtil.VALUE_MAP_DEFAULT=1; 
		 }
		 Logger.d(TAG, "!--->VALUE_MAP_DEFAULT " + UserPerferenceUtil.VALUE_MAP_DEFAULT);
		/* < xd added 20150706 begin */
		// if (PackageUtil.isRunningForeground(WindowService.this)) {
		// mSessionManager.showWelcomeView(true);
		// }
		// startWelcomeActivity();// xd added 20150706
		/* xd added 20150706 end > */

		bindMessageRouterService();
		updateMapConfig();
		startKWMusicService();

		mViewRoot = (SessionLinearLayout) View.inflate(this,R.layout.window_service_main, null);
	/*	if (DeviceTool.getDeviceSDKVersion() > 13) {
			mViewRoot.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);// added
		} else {
			Logger.d(TAG, "!--->DeviceSDKVersion <= 13");
		}*/
		findViews();
		mSessionManager = new GUISessionManager(this, mSessionContainer,mMicrophoneControl);// init framework & show prepare view

		/* < XD 20150805 added for Mic float View Begin */
		// mScreeFloatView = new FloatMicView(this);
		mLauncherPackage = PackageUtil.getLauncherPackages(this);
		/* XD 20150805 added for Mic float View End > */
		initWindowParams();
		registerListener();
		/* < XD 20150805 added for Mic float View Begin */
		registerReceiver();
		mPendingStartMicChecker = true;
		/* XD 20150805 added for Mic float View End > */
		/* < xd delete 20150706 Begin */
		// if (PackageUtil.isRunningForeground(WindowService.this)) {
		// mSessionManager.showInitView(true);
		// }
		/* xd delete 20150706 End > */
		// Intent killNotificationIntent = new Intent(this,
		// KillNotificationService.class);
		// startService(killNotificationIntent);
		messageTransCenter = new SystemCallTransition();
		messageTransCenter.setMessageTransListener(mMessageTransListener);

		/* < XD 20150825 added for 4.4 default Sms App begin */
		if (GUIConfig.isAllowGUIRequestAsDefaultSmsApp) {
			DeviceTool.changeGUIToDefaultSmsApp(mContext);
		}
		/* XD 20150825 added for 4.4 default Sms App End > */

	/*	IntentFilter filter = new IntentFilter(GUIConfig.ACTION_SMS_SEND_SUCCESS);
		filter.addAction(GUIConfig.ACTION_SM  S_SEND_FAIL);
		registerReceiver(mSmsSendStatusReceiver, filter);*/
//		writeContactsInfo();

		getContentResolver().registerContentObserver(Settings.System.getUriFor(SettingMapViewPagerActivity.MAP_INDEX),false, new ContentObserver(mUIHandler) {
							@Override
							public void onChange(boolean selfChange) {
								updateMapConfig();
							}
						});

	}

	@SuppressLint("NewApi")
	private void findViews() {
		mSessionContainer = (SessionContainer) mViewRoot.findViewById(R.id.sessionContainer);
		mMicrophoneControl = (MicrophoneControl) mViewRoot.findViewById(R.id.microphoneControl);
		mMicrophoneDoresoControl = (MicrophoneControlDoresoView) mViewRoot.findViewById(R.id.microphoneControl_doreso); //XD added 20150828
	}

	@SuppressLint("NewApi")
	private void updateDisplaySize() {
	/*	Display display = mWindowManager.getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			mWindowSize.y = display.getHeight();
			mWindowSize.x = display.getWidth();
		} else {
			display.getSize(mWindowSize);
		}*/
		Logger.d(TAG, "!--->updateDisplaySize:x " + mWindowSize.x + ",y "+ mWindowSize.y);
	}

	/**
	 * when WindowService onCreate should show wake up status Toast XD 20150819
	 * added
	 */
	private void showWakeupStatusToast() {
		if (!Constant.isFirstStart(mContext)) {
			return;
		}
		/*
		 * Logger.d(TAG, "!--->showWakeupStatusToast----"); if
		 * (UserPerferenceUtil.isWakeupEnable(mContext)) {
		 * Toast.makeText(mContext,
		 * R.string.toast_wakeup_open,Toast.LENGTH_LONG).show(); } else {
		 * Toast.makeText(mContext,
		 * R.string.toast_wakeup_closed,Toast.LENGTH_SHORT).show(); }
		 */
	}

	/**
	 * start WelcomeActivity xd added 20150706
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
		/* < XD 20150805 added for float mic view Begin */
		// mScreeFloatView.setOnClickListener(mOnClickListener);
		// UserPerferenceUtil.registerOnSharedPreferenceChangeListener(this,
		// mPreferenceChangeListener);
		/* XD 20150805 added for float mic view End > */
	}

	private void unregisterListener() {
		mViewRoot.setDispatchKeyEventListener(null);
		/* < XD 20150805 added for float mic view Begin */
		// mScreeFloatView.setOnClickListener(null);
		// UserPerferenceUtil.unregisterOnSharedPreferenceChangeListener(this,
		// mPreferenceChangeListener);
		/* XD 20150805 added for float mic view End > */
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void resetWindowParamsFlags() {
		Log.d(TAG, "!--->------resetWindowParamsFlags()------");
		/*mWindowParams.flags &= ~(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN // xiaodong
		);*/
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
	}

	private void initWindowParams() {
		Log.d(TAG, "!--->initWindowParams()------");
		mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		mWindowParams.format = PixelFormat.RGBA_8888;
		resetWindowParamsFlags();
	//	mWindowParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		mWindowParams.gravity = Gravity.CENTER;
		updateDisplaySize();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.d(TAG, "!--->onStartCommand: intent " + intent);

		boolean isUnicarServiceInstalled = DeviceTool.isUnicarServiceInstalled(getApplicationContext());
		if (!isUnicarServiceInstalled) {
			Logger.e(TAG,"!--->onStartCommand()---UnicarService not installed!");
			mSessionManager.showInitFailedView(getString(R.string.error_init_msg_vui_service_not_install));
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
					startTalk();
					
				}
			} else if(ACTION_START_TALK.equals(action)){
				if (mViewRoot.isShown()) {
					if (mSessionManager != null) {
						mSessionManager.cancelTalk();
					}
				} else {
					startTalk();
					
				}
			}else if (ACTION_START_REQUEST_MAKE_FINISHED.equals(action)) {
				requestIsASRCompileFinished();
			} else if (MessageReceiver.ACTION_START_CALL_OUT.equals(action)) {
				requestCallOut();
			} else if (MessageReceiver.ACTION_START_NAVIGATION.equals(action)) {
				requestNavication();
			} else if (MessageReceiver.ACTION_START_MUSIC.equals(action)) {
				requestMusic();
			} else if (MessageReceiver.ACTION_START_LOCAL_SEARCH.equals(action)) {
				requestLocalSearch();
			} else if (ACTION_SET_WAKEUP.equals(action)) {
				if (UserPerferenceUtil.isWakeupEnable(mContext)) {
					sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_START);
					ContentValues con = new ContentValues();
					con.put(WakeUpDB.SWITCH,1);
					getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null,null);
				} else {
					sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_STOP);
					ContentValues con = new ContentValues();
					con.put(WakeUpDB.SWITCH, 0);
					getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null,null);
				}
			} else if (ACTION_SET_TTSSPEED.equals(action)) {
				int speed = UserPerferenceUtil.getTTSSpeed(mContext);
				if (UserPerferenceUtil.VALUE_TTS_SPEED_SLOWLY == speed) {
					sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_TTS_SPEED,SessionPreference.EVENT_PROTOCAL_SWITCH_TTS_SPEED_SLOWLY);
				} else if (UserPerferenceUtil.VALUE_TTS_SPEED_STANDARD == speed) {
					sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_TTS_SPEED,SessionPreference.EVENT_PROTOCAL_SWITCH_TTS_SPEED_STANDARD);
				} else if (UserPerferenceUtil.VALUE_TTS_SPEED_FAST == speed) {
					sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_TTS_SPEED,SessionPreference.EVENT_PROTOCAL_SWITCH_TTS_SPEED_FAST);
				}
			} else if (MessageReceiver.ACTION_START_NAVI.equals(action)) {
				CreateDialog(this);
			}
		}
		return START_STICKY;
	}

	// 与Framework同步唤醒和TTS播报速度
	private void syncConfigure() {
		// send switch WakeUp
		/*if (UserPerferenceUtil.isWakeupEnable(mContext)) {
			Logger.d(TAG, "isWakeupEnable="+true);
			sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_START);
			ContentValues con = new ContentValues();
			con.put(WakeUpDB.SWITCH, 1);
			getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null,null);
		} else {
			Logger.d(TAG, "isWakeupEnable="+false);
			sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_STOP);
			ContentValues con = new ContentValues();
			con.put(WakeUpDB.SWITCH, 0);
			getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null,null);
		}*/
		// send switch TTSSpeed
		int speed = UserPerferenceUtil.getTTSSpeed(mContext);
		if (UserPerferenceUtil.VALUE_TTS_SPEED_SLOWLY == speed) {
			sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_TTS_SPEED,SessionPreference.EVENT_PROTOCAL_SWITCH_TTS_SPEED_SLOWLY);
		} else if (UserPerferenceUtil.VALUE_TTS_SPEED_STANDARD == speed) {
			sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_TTS_SPEED,SessionPreference.EVENT_PROTOCAL_SWITCH_TTS_SPEED_STANDARD);
		} else if (UserPerferenceUtil.VALUE_TTS_SPEED_FAST == speed) {
			sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_TTS_SPEED,SessionPreference.EVENT_PROTOCAL_SWITCH_TTS_SPEED_FAST);
		}
	}

	private void startTalk() {
		mSessionManager.startTalk();
		//XD 20150827 added 
		mWakeupSuccessType = SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS;
		updateMicControlView(SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS);
	}

	public void onReset() {
		Logger.d(TAG, "onReset");
		dismiss();
	}

	private void show(View view) {
		if (view == null || view.isShown()) {
			return;
		}
		// 弹出框强制横屏
		if (mWindowParams.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
			mWindowParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
		}

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
				WindowManager.LayoutParams.TYPE_PHONE,
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
		RomSystemSetting.setMute(mContext);
		if (mViewRoot.isShown()) {
			return;
		}
		acquireWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, GANK);
		Logger.d(TAG, "show");
		playTTs = false;
		/* < xiaodong 20150805 added for Float Mic View Begin */
		mPendingStartMicChecker = true;
		/* xiaodong 20150805 added for Float Mic View End > */

		show(mViewRoot);

	}

	public void dismiss() {
		releaseWakeLock();
		playTTs = true;
		if(playText!=null){
			TTSController.getInstance(mContext).playText(playText);
			playText=null;
		}
		if (!mViewRoot.isShown()) {
			return;
		}
		Logger.d(TAG, "dismiss");
		mWindowManager.removeViewImmediate(mViewRoot);

		/* < xiaodong 20150805 added for Float Mic View Begin */
		if (mPendingStartMicChecker) {
			mPendingStartMicChecker = false;
		}
		mUIHandler.postDelayed(mRecoverRunnable, 200);
		/* xiaodong 20150805 added for Float Mic View End > */
	}

	public void dimissView(View view) {
		if (!view.isShown()) {
			return;
		}
		Logger.d(TAG, "dimissView---prepare view dismiss");
		mWindowManager.removeViewImmediate(view);

		/* < xiaodong 20150805 added for Float Mic View Begin */
		if (mPendingStartMicChecker) {
			mPendingStartMicChecker = false;
		}
		/* xiaodong 20150805 added for Float Mic View End > */
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
		/*mWindowParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		if (view == null || !canTextInput(view)) {
			mWindowParams.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		}*/
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN; //add by zzj
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
		releaseWakeLock();
		Logger.d(TAG, "onLowMemory");
	}

	@Override
	public void onDestroy() {
		Logger.d(TAG, "onDestroy");
		super.onDestroy();
		dismiss();
		unregisterListener();
		unBindService(mContext); // xd added 20150706
		releaseWakeLock();
//		unregisterReceiver(mSmsSendStatusReceiver);// xd added 20150709

		/* < xiaodong 20150805 added for Float Mic View  */
		unregisterReceiver();
		stopKWMusicService();
		mLauncherPackage.clear();
		mLauncherPackage = null;
		/* xiaodong 20150805 added for Float Mic View End > */
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

		if (appsDataModel != null) {
			appsDataModel.release();
		}
	}

	public void bindMessageRouterService() {
		try {
			Logger.d(TAG, "!--->bindMessageRouterService");
			Intent intent = new Intent("com.unisound.unicar.messagerouter.start");
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

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,IBinder service) {
			mMessageRouterService = IMessageRouterService.Stub.asInterface(service);
			try {
				Logger.d(TAG, "!--->onServiceConnected()-----------");
				mMessageRouterService.registerCallback(mCallback);
				registSystemCall();
				/*if (MessageReceiver.readAccFile()) {
					syncConfigure();
				}else{
					sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_STOP);
				}*/
				syncConfigure();
				// if(isNeedRequestIsASRCompileFinished){
				requestIsASRCompileFinished();
				// }
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Logger.d(TAG, "onServiceDisconnected");
			mMessageRouterService = null;
			rebindMessageRouterService();
		}
	};

	private IMessageRouterCallback mCallback = new IMessageRouterCallback.Stub() {

		@Override
		public void onCallBack(String callBackJson) throws RemoteException {
			Logger.d(TAG, "!--->onCallBack : " + callBackJson);
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
	 * @param callBackJson
	 */
	private void checkIfNeedSendRespImmediately(String callBackJson){
		Logger.d(TAG, "!--->checkIfNeedSendRespImmediately----");
		JSONObject obj = JsonTool.parseToJSONObject(callBackJson);
		if (obj != null) {
			JSONObject jobj = JsonTool.parseToJSONObject(callBackJson);
			JSONObject data = JsonTool.getJSONObject(jobj, SessionPreference.KEY_DATA);
			String functionName = JsonTool.getJsonValue(data, SessionPreference.KEY_FUNCTION_NAME, "");
			JSONObject dataParams = JsonTool.getJSONObject(data, SessionPreference.KEY_PARAMS);

			if (SessionPreference.VALUE_FUNCTION_NAME_ON_TALK_PROTOCOL.equals(functionName)) {
				JSONObject typeObj = JsonTool.getJSONObject(dataParams, SessionPreference.KEY_TYPE);
				String type = JsonTool.getJsonValue(typeObj, SessionPreference.KEY_TYPE);
				String originType = JsonTool.getJsonValue(typeObj, SessionPreference.KEY_ORIGIN_TYPE);
				if (dataParams != null && SessionPreference.VALUE_TYPE_WAITING.equals(type)
						&& (SessionPreference.DOMAIN_ROUTE.equals(originType)
						   || SessionPreference.DOMAIN_NEARBY_SEARCH.equals(originType)
						   )) {
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
	 * TODO:if type in "data" is "CALL" need response to VUI XD modify 20150807
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
	public void sendResponse(String params, String state){
		Logger.d(TAG, "sendResponse params : " + params+"; state : " + state);
		Logger.d(TAG, "sendResponse mSystemCallJson : " + mSystemCallJson);
		JSONObject jobj = JsonTool.parseToJSONObject(mSystemCallJson);
		JSONObject data = JsonTool.getJSONObject(jobj,SessionPreference.KEY_DATA);
		String callName = JsonTool.getJsonValue(data,SessionPreference.KEY_FUNCTION_NAME);
		String callID = JsonTool.getJsonValue(jobj,SessionPreference.KEY_CALL_ID);
		String param = "";
		String domain = "";
		if (params != null && params.length() > 0) {
			param = params;
			//add tyz 0714
			JSONObject params_ =  JsonTool.getJSONObject(data,"params");
			JSONObject type = JsonTool.getJSONObject(params_,"type");
			domain = JsonTool.getJsonValue(type,"origin_type");
			Logger.d(TAG, "sendResponse domain : " + domain);
			//add tyz 0714
		} else {
			JSONObject dataParams = JsonTool.getJSONObject(data,SessionPreference.KEY_PARAMS);
			if (dataParams != null) {
				param = dataParams.toString();
			}
		}

		messageTransCenter.sendResponse(callID, callName, state, domain, param);
	}

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
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_RECORDING_EXCEPTION); // XD
																					// 20150807
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_TALK_RESULT);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_FETCH_UPDATE_CONTACT_DONE);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_FETCH_UPDATE_MEDIA_DONE);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_FETCH_UPDATE_APP_DONE);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_RECOGNIZER_TIMEOUT);
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_ON_CTT_CANCEL);// add
		namelist.add(SessionPreference.VALUE_FUNCTION_NAME_FETCH_WAKEUP_WORDS_DONE);//zzj add																	// tyz
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
	 * background, send "WAKEUP" event
	 */
	public void sendPTT() {
		Logger.d(TAG, "sendPTT");
		sendEvent(SessionPreference.EVENT_NAME_PTT);
	}

	/*< XD 20150828 added for Music Doreso Begin */
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
	public void sendPTTByWakeupSuccessType(){
		Logger.d(TAG, "!--->sendPTTByWakeupSuccessType--mWakeupSuccessType = "+mWakeupSuccessType);
		if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS == mWakeupSuccessType) {
			sendPTT();
		} else if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO == mWakeupSuccessType) {
			sendPTTDoreso();
		}
	}
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
	 * called on mUIHandler MSG_GUI_CANCEL_WAITTING_SESSION XD 20150813 modify
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
	
	public void sendFetchWakeUpEvent() {
		Logger.d(TAG, "Fetch wakeup");
		sendEvent(SessionPreference.EVENT_NAME_FETCH_WAKEUP_WORD);
	}

	private void sendEvent(String eventName) {
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":" + eventName + "}}";
		Logger.d(TAG, "-sendEvent- eventMsg : " + eventMsg);
		sendMsg(eventMsg);
	}

	/**
	 * 
	 * xd added 20150702
	 * 
	 * @param eventName
	 *            : ON_CONFIRM_OK, ON_CONFIRM_CANCEL, SELECT_ITEM
	 * @param protol
	 */
	public void sendProtocolEvent(String eventName, String protol) {
		Logger.d(TAG, "sendProtocolEvent()----eventName =" + eventName + "; protol = " + protol);
		if (SessionPreference.EVENT_NAME_ON_CONFIRM_TIME_UP.equals(eventName)) {
			protol = SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_TIME_UP;
		}
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\"" + eventName + "\"},\"param\":" + protol + "}";
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
	 * requestSupportDomainList xd added 20150702
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
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\"MAIN_CALL_OUT\"}}";
		sendMsg(eventMsg);
	}

	public void requestNavication() {
		Logger.d(TAG, "!--->requestNavication()----");
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\"MAIN_NAVIGATION\"}}";
		sendMsg(eventMsg);
	}

	public void requestMusic() {
		Logger.d(TAG, "!--->requestMusic()----");
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\"MAIN_MUSIC\"}}";
		sendMsg(eventMsg);
	}

	/**
	 * request Local Search XD added 20150713
	 */
	public void requestLocalSearch() {
		Logger.d(TAG, "!--->requestLocalSearch()----");
		String eventMsg = "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\"MAIN_LOCAL_SEARCH\"}}";
		sendMsg(eventMsg);
	}

	/**
	 * write Contacts Info
	 */
	private void writeContactsInfo() {
		Logger.d(TAG, "!--->writeContactsInfo()-----");
		/* < XD added 20150706 begin */
		if (PackageUtil.isRunningForeground(WindowService.this)) {
			mSessionManager.showInitView(true);
		}
		if (!DeviceTool.isSdCardExist()) {
			Logger.e(TAG, "!--->No SDCard mounted!");
			mSessionManager.showInitFailedView(mContext.getString(R.string.error_init_msg_no_sdcard));
			// Toast.makeText(mContext,
			// mContext.getString(R.string.error_init_msg_no_sdcard),
			// Toast.LENGTH_LONG).show();
			return;
		}
		/* XD added 20150706 end > */
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
				if(startDownPhone){
					TTSController.getInstance(WindowService.this).playText(DOWNVOER);
					sendBroadcast(new Intent(ACTION_CONTACT_DONE));
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
				if(startDownPhone){
					TTSController.getInstance(WindowService.this).playText(DOWNVOER);
					sendBroadcast(new Intent(ACTION_CONTACT_DONE));
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
			Logger.d(TAG, "!--->setState()----type = " + type);
			Message msg = new Message();
			msg.what = MSG_SET_STATE;
			msg.obj = type;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void onTalkRecordingPrepared() {
			playTTs = false;
			Logger.d(TAG, "!--->---onTalkRecordingPrepared---");
			Message msg = new Message();
			msg.what = MSG_ON_RECORDING_PREPARED;
			mUIHandler.sendMessage(msg);
			// mSessionManager.onTalkRecordingStart();
		}

		@Override
		public void onTalkRecordingException() {
			mUIHandler.sendEmptyMessage(MSG_ON_RECORDING_EXCEPTION);
		}

		@Override
		public void onTalkRecordingStart() {
			playTTs = false;
			Logger.d(TAG, "!--->---onTalkRecordingStart---");
			Message msg = new Message();
			msg.what = MSG_ON_RECORDING_START;
			mUIHandler.sendMessage(msg);
			// mSessionManager.onTalkRecordingStart();
		}

		@Override
		public void onTalkRecordingStop() {
			playTTs = true;
			Logger.d(TAG, "!--->---onTalkRecordingStop---");
			Message msg = new Message();
			msg.what = MSG_ON_RECORDING_STOP;
			mUIHandler.sendMessage(msg);
			// mSessionManager.onTalkRecordingStop();
		}

		@Override
		public void onTalkResult(String result) {
			Logger.d(TAG, "!--->onTalkResult---result = " + result);
			Message msg = new Message();
			msg.what = MSG_ON_RECORDING_RESULT;
			msg.obj = result;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void onSessionProtocal(String protocol) {
			Message msg = new Message();
			msg.what = MSG_ON_SESSION_PROTOCAL;
			msg.obj = protocol;
			mUIHandler.sendMessage(msg);
			// mSessionManager.onSessionProtocal(protocol);
		}

		@Override
		public void onSendMsg(String msg) {
			sendMsg(msg);
		}

		@Override 
		public void onPlayEnd() {
			playTTs = true;
		//	getAudioManager().abandonAudioFocus(null);
			if(playText!=null){
				TTSController.getInstance(mContext).playText(playText);
				playText=null;
			}
			Message msg = new Message();
			msg.what = MSG_ON_TTS_PLAY_END;
			mUIHandler.sendMessage(msg);
			// mSessionManager.onPlayEnd();
		}

		@Override
		public void onUpdateVolume(int volume) {
			// xd added 20150714
		//	 Logger.d(TAG, "!--->onUpdateVolume---volume = "+volume);
			if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS == mWakeupSuccessType) {
				mMicrophoneControl.setVoiceLevel(volume);
			} else if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO == mWakeupSuccessType) {
				mMicrophoneDoresoControl.setVoiceLevel(volume);
			}
		}

		@Override
		public void onRecognizerTimeout() {
			Message msg = new Message();
			msg.what = MSG_ON_RECOGNIZER_TIMEOUT;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void onCTTCancel() {
			Message msg = new Message();
			msg.what = MSG_ON_CTT_CANCEL;
			mUIHandler.sendMessage(msg);
		}

		@Override
		public void getWAKEUPWORDS(String text) {
			if(!TextUtils.isEmpty(text))
			 PrivatePreference.setValue(UserPreference.Key_WAKEUP_WORD, text);
		}

	};
	private void updateMicControlView(int type){
		Logger.d(TAG, "!--->updateMicControlView---type = " + type);
		if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS == type) {
			mMicrophoneControl.setVisibility(View.VISIBLE);
			mMicrophoneDoresoControl.setVisibility(View.GONE);
			mSessionManager.setMicrophoneControl(mMicrophoneControl); //XD 20150827 added
		} else if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO == type){
			mMicrophoneControl.setVisibility(View.GONE);
			mMicrophoneDoresoControl.setVisibility(View.VISIBLE);
			mSessionManager.setMicrophoneControl(mMicrophoneDoresoControl); //XD 20150827 added
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
					// 唤醒已经打开 已经初始化完成，可以说命令词了
					mSessionManager.onWakeUpInitDone();
					sendFetchWakeUpEvent();
					break;
				case SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS:
					// 唤醒成功 准备Mic状态 mic_prepare
					mWakeupSuccessType = SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS;
					updateMicControlView(mWakeupSuccessType);
					mSessionManager.onWakeUpSuccess();
					break;
				case SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO:
					// 我想哼歌 唤醒成功
					mWakeupSuccessType = SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO;
					updateMicControlView(mWakeupSuccessType);
					
					mSessionManager.onWakeUpSuccessDoreso();
					break;
				case SessionPreference.VALUE_SET_STATE_TYPE_ASR_COMPILE_FINISH:
					Logger.d(TAG,"!--->VALUE_SET_STATE_TYPE_ASR_COMPILE_FINISH");
					// 语法数据编译完成
					mSessionManager.onTalkDataDone();
					showWakeupStatusToast(); // XD 20150819 added
					Constant.setFirstStart(mContext, false); // first start has
																// finished.
					requestSupportDomainList(); // xd added 20150702
					sendFetchWakeUpEvent();
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
			case MSG_ON_RECORDING_PREPARED:
				Logger.d(TAG, "!--->MSG_ON_RECORDING_PREPARED----");
				getGeneralGPS();
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
				Logger.d(TAG, "!--->MSG_ON_RECORDING_RESULT----");
				String result = (String) msg.obj;
				/*< XD 20150906 modify for ASR partial result Begin */
				Logger.d(TAG, "!--->MSG_ON_RECORDING_RESULT----result = "+result);
				// {"result":"打电话","resultType":"full"}
				JSONObject resultObj = JsonTool.parseToJSONObject(result);
				String resultType = JsonTool.getJsonValue(resultObj, "resultType", "full"); //"partial"
				String text = JsonTool.getJsonValue(resultObj, "result");
				boolean isPartial = false;
				if("partial".equals(resultType)){
					isPartial = true;
				}
				mSessionManager.onTalkRecordingResult(text, isPartial);
				/* XD 20150906 modify for ASR partial result End >*/
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
				releaseWakeLock();
				sendCancelEvent();
				// mSessionManager.dismissWindowService(); //DELET do this on
				// onCTTCancel() system call
				break;
			case MSG_GUI_CANCEL_WAITTING_SESSION:
				Logger.d(TAG,"!--->MSG_GUI_CANCEL_WAITTING_SESSION--sendWaittingCancelEvent--");
				sendWaittingCancelEvent();
				break;
			case MSG_ON_CTT_CANCEL:
				Logger.d(TAG, "!--->MSG_ON_CTT_CANCEL----");
				releaseWakeLock();
				mSessionManager.onCTTCancel();
				break;
			case MSG_ON_ACC_ON:
				if(MessageReceiver.readAccFile()){
					if (UserPerferenceUtil.isWakeupEnable(mContext)) {
						sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_START_ACC);
						ContentValues con = new ContentValues();
						con.put(WakeUpDB.SWITCH, 1);
						getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null,null);
					} else {
				//		sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_STOP);
						ContentValues con = new ContentValues();
						con.put(WakeUpDB.SWITCH, 0);
						getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null,null);
					}
				}
				break;
			case MSG_ON_ACC_OFF:
				if (UserPerferenceUtil.isWakeupEnable(mContext)){
					sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_STOP_ACC);
					ContentValues con = new ContentValues();
					con.put(WakeUpDB.SWITCH, 0);
					getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null,null);
				}
				
				break;
			default:
				break;
			}
		};
	};

	/**
	 * Sms Send Status Receiver xd added 20150709
	 */
/*	private BroadcastReceiver mSmsSendStatusReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.d(TAG,
					"!--->mSmsSendStatusReceiver Action:" + intent.getAction());
			if (GUIConfig.ACTION_SMS_SEND_SUCCESS.equals(intent.getAction())) {
				mSessionManager.showSmsSendStatusView(GUIConfig.SMS_STATUS_SEND_SUCCESS);
			} else if (GUIConfig.ACTION_SMS_SEND_FAIL.equals(intent.getAction())) {
				mSessionManager.showSmsSendStatusView(GUIConfig.SMS_STATUS_SEND_FAIL);
			}
		}
	};*/

	/**
	 * TODO: xd added 20150702
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
	 * xd added 20150702
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
		mOnlineSupportList.add(SessionPreference.DOMAIN_SMS);
		mOnlineSupportList.add(SessionPreference.DOMAIN_MUSIC);
		mOnlineSupportList.add(SessionPreference.DOMAIN_BROADCAST);
		mOnlineSupportList.add(SessionPreference.DOMAIN_ROUTE);
		mOnlineSupportList.add(SessionPreference.DOMAIN_WEATHER);
		mOnlineSupportList.add(SessionPreference.DOMAIN_SETTING);
		mOnlineSupportList.add(SessionPreference.DOMAIN_STOCK);
		mOnlineSupportList.add(SessionPreference.DOMAIN_LOCAL);
		mOnlineSupportList.add(SessionPreference.DOMAIN_TRAFFIC);
		mOnlineSupportList.add(SessionPreference.DOMAIN_LIMIT);

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
			LocationModelProxy locationModel = LocationModelProxy.getInstance(mContext);
			locationModel.setLocationListener(new ILocationListener() {
				
				@Override
				public void onLocationResult(List<LocationInfo> infos,ErrorUtil code) {
				}

				@Override
				public void onLocationChanged(LocationInfo locationInfo,ErrorUtil errorUtil) {
					if (locationInfo != null) {
						Logger.d(TAG, "getGeneralGPS end Latitude : "+ locationInfo.getLatitude() + ";Longitude : "+ locationInfo.getLongitude());
						mLocateTime = now;
						mLocationInfo = locationInfo;
						if (mLocationInfo != null) {
							sendProtocolEvent(SessionPreference.EVENT_NAME_GENERAL_GPS,"{\"gpsInfo\":" + "\"" + mLocationInfo.getLatitude() + ","+ mLocationInfo.getLongitude()+ "\"}");
						}
					}
				}
			});
			locationModel.startLocate();
		}

		if (mLocationInfo != null) {
			sendProtocolEvent(SessionPreference.EVENT_NAME_GENERAL_GPS,"{\"gpsInfo\":" + "\"" + mLocationInfo.getLatitude() + "," + mLocationInfo.getLongitude() + "\"}");
		}
	}

	/* < xiaodong 20150805 added for Mic float View Begin */
	/*
	 * private OnClickListener mOnClickListener = new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { if (v ==
	 * mScreeFloatView.getFloatMicInstance()) { Logger.d(TAG,
	 * "!--->onClick mScreeFloatView"); startTalk(); } } };
	 */

	/*
	 * private OnSharedPreferenceChangeListener mPreferenceChangeListener = new
	 * OnSharedPreferenceChangeListener() {
	 * 
	 * @Override public void onSharedPreferenceChanged( SharedPreferences
	 * sharedPreferences, String key) { Logger.d(TAG,
	 * "!--->onSharedPreferenceChanged: key " + key); if
	 * (UserPerferenceUtil.KEY_ENABLE_FLOAT_MIC.equals(key)) {
	 * updateEnableFloatMic(); if (mEnableFloatMic) { startFloatMicChecker(0); }
	 * } } };
	 */

	/**
	 * ZZJ 20150828 added for RECEIVE mScreenReceiver
	 */
	private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.d(TAG, "!--->mScreenReceiver--onReceive:intent " + intent);
			String action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				mSessionManager.onResume();
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				mSessionManager.onPause();
			} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
				mSessionManager.onResume();
			} else if (ACTION_ACC_OFF.equals(action)) {
				if (!MessageReceiver.readAccFile()) {
					context.sendBroadcast(new Intent(COM_GLSX_BOOT_ACCOFF));
					accSwitch = false;
					if(mViewRoot.isShown()){
						mSessionManager.cancelTalk();
					}
					mUIHandler.sendEmptyMessageDelayed(MSG_ON_ACC_OFF, 2000);
					MessageSender messageSender = new MessageSender(context);
					Intent stop = new Intent(CommandPreference.SERVICECMD);
					stop.putExtra(CommandPreference.CMDNAME,CommandPreference.CMDSTOP);
					messageSender.sendOrderedMessage(stop, null);
				}

			} else if (ACTION_ACC_ON.equals(action)) {
				accSwitch = true;
				mUIHandler.sendEmptyMessageDelayed(MSG_ON_ACC_ON, 7000);
				if (mUserPreference.getFloat("lat", 0f) != 0f && mUserPreference.getFloat("lng", 0f) != 0f) {
					CreateDialog(context);
				}
				// 广联一键通
				context.sendBroadcast(new Intent(COM_GLSX_BOOT_ACCON));

			} else if (ACTION_CLOSE_WAKEUP.equals(action)) {

				mSessionManager.cancelSession();
				mSessionManager.onPause();
				if (UserPerferenceUtil.isWakeupEnable(mContext)){
					sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_STOP_ACC);
					ContentValues con = new ContentValues();
					con.put(WakeUpDB.SWITCH, 0);
					getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null,null);
				}

			} else if (ACTION_OPEN_WAKEUP.equals(action)) {

				if (UserPerferenceUtil.isWakeupEnable(mContext)) {
					sendProtocolEvent(SessionPreference.EVENT_NAME_SWITCH_WAKEUP,SessionPreference.EVENT_PROTOCAL_SWITCH_WAKEUP_START_ACC);
					ContentValues con = new ContentValues();
					con.put(WakeUpDB.SWITCH, 1);
					getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null,null);
				} else {
					ContentValues con = new ContentValues();
					con.put(WakeUpDB.SWITCH, 0);
					getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null,null);
				}
			} else if (PLAY_TTS.equals(action)) {
				playText = intent.getStringExtra("content");
				Logger.d(TAG, "!--->playText= " + playText+"--->playTTs="+playTTs);
				if (playTTs) {
					TTSController.getInstance(context).playText(playText);
					playText = null;
				}
			} else if(GL_PLAY_TTS.equals(action)){
				playText = intent.getStringExtra("ttsText");
				if (playTTs) {
					TTSController.getInstance(context).playText(playText);
					playText = null;
				}
			} else if(ACTION_CONTACT_START.equals(action)){
				startDownPhone=true;
			}else if (ACTION_CUSTOMFROMBT.equals(action)) {
				
				sendEventObject(SessionPreference.EVENT_NAME_SAVE_CONTACTS_DONE);
			}

		}

	};
	
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
	
	public  void playTTs(String ttsContent,int type){
		switch (type) {
		case RomCustomerProcessing.TTS_END_WAKEUP_START:
			sendProtocolEvent(SessionPreference.EVENT_NAME_PLAY_TTS, GuiProtocolUtil.getPlayTTSEventParam(ttsContent,"wakeup"));
			break;
		case RomCustomerProcessing.TTS_END_RECOGNIER_START:
			sendProtocolEvent(SessionPreference.EVENT_NAME_PLAY_TTS, GuiProtocolUtil.getPlayTTSEventParam(ttsContent,"recognizer"));
			break;

		default:
			break;
		}
		
	}
	
    public static final String ACTION_CUSTOMFROMBT = "com.unisound.unicar.customFromBt";

	/**
	 * register mScreenReceiver ZZJ added 20150828
	 */
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(ACTION_ACC_ON);
		filter.addAction(ACTION_ACC_OFF);
		filter.addAction(ACTION_OPEN_WAKEUP);
		filter.addAction(ACTION_CLOSE_WAKEUP);
		filter.addAction(ACTION_CONTACT_START);
		filter.addAction(PLAY_TTS);
		filter.addAction(GL_PLAY_TTS);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		filter.addAction(ACTION_CUSTOMFROMBT);
		registerReceiver(mScreenReceiver, filter);
	}

	/**
	 * unregister mScreenReceiver XD added 20150805
	 */
	private void unregisterReceiver() {
		unregisterReceiver(mScreenReceiver);
	}

	/* < xiaodong 20150813 added for cancel button click Begin */
	/**
	 * 
	 */
	public void onCancelTalk() {
		mUIHandler.sendEmptyMessage(MSG_GUI_CANCEL_SESSION);
		mUIHandler.removeMessages(MSG_ON_RECORDING_PREPARED);
	}

	/**
	 * XD 20150813 added
	 */
	public void onWaittingSessionCancel() {
		mUIHandler.sendEmptyMessage(MSG_GUI_CANCEL_WAITTING_SESSION);
	}

	/* xiaodong 20150813 added for for cancel button click End > */

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
					UserPreference userPreference = new UserPreference(WindowService.this);
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
		((TextView) v.findViewById(R.id.textView1)).setText("导航到"+ mUserPreference.getString("address",UserPreference.PREPARE_NAVI_ADDRESS));
		builder.setView(v);
		Intent intent = new Intent(PLAY_TTS);
		intent.putExtra("content", "收到一条预约导航指令");
		context.sendBroadcast(intent);
		mAlertDialog = builder.create();
		mAlertDialog.setCanceledOnTouchOutside(false);
		mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);// 显示
		mAlertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
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
				button1.setText("开始(" + millisUntilFinished / 1000 + "s)");
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
			Gps gcj02 = PositionUtil.bd09_To_Gcj02(mUserPreference.getFloat("lat", 0f), mUserPreference.getFloat("lng", 0f));
			GaodeUriApi.startNavi(mContext,
					gcj02.getWgLat(),
					gcj02.getWgLon(),
					mUserPreference.getString("address", ""), 2, 0);
			break;
		case 3:
			Gps gcj = PositionUtil.bd09_To_Gcj02(mUserPreference.getFloat("lat", 0f), mUserPreference.getFloat("lng", 0f));
			KLDUriApi.startNavi(mContext,gcj.getWgLat(),
					gcj.getWgLon(),
					mUserPreference.getString("address", ""));
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
			recover();
		}
	};
	private WakeLock mWakeLock;
	private PowerManager mPowerManager;

	/**
	 * by ZZJ
	 */
	private void recover() {
		int volume = getAudioManager().getStreamVolume(AudioManager.STREAM_ALARM);
		if (volume < 1) {
			getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, 0,AudioManager.FLAG_ALLOW_RINGER_MODES);
		} else {
			getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC,volume * 2 + 1, AudioManager.FLAG_ALLOW_RINGER_MODES);
		}
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
	/**
	 * Add default Location 20150909
	 * 从assets/config.mg中读取位置信息，若没有配置，缺省使用上海公司位置信息
	 * 应jason要求,一定要加一个缺省的位置信息，规避第一次定位就失败的情况。
	 */
	public void setDefaultLocation(){
		JSONObject mLocationJsonObject = JsonTool.parseToJSONObject(PrivatePreference.mDeufaultLocation);
		mLocationInfo = new LocationInfo();
		mLocationInfo.setCityCode(JsonTool.getJsonValue(mLocationJsonObject, "cityCode", "021"));
		mLocationInfo.setLatitude(JsonTool.getJsonValue(mLocationJsonObject, "lat",  31.177598));
		mLocationInfo.setLongitude(JsonTool.getJsonValue(mLocationJsonObject, "lng", 121.401098));
		mLocationInfo.setType(JsonTool.getJsonValue(mLocationJsonObject, "type", 5));
		mLocationInfo.setAddress(JsonTool.getJsonValue(mLocationJsonObject, "address", "上海市 徐汇区 钦州北路 靠近电信恒联"));
		mLocationInfo.setDistrict(JsonTool.getJsonValue(mLocationJsonObject, "destrict", "徐汇区"));
		mLocationInfo.setCity(JsonTool.getJsonValue(mLocationJsonObject, "city", "上海"));
	}

	/**
	 * by ZZJ
	 */
	private void updateMapConfig() {
		int map_index = Settings.System.getInt(getContentResolver(),SettingMapViewPagerActivity.MAP_INDEX, UserPerferenceUtil.VALUE_MAP_DEFAULT);
		Logger.d(TAG, "updateMapConfig: " + map_index);
		if (map_index == 0) {
			UserPerferenceUtil.setMapChoose(mContext,UserPerferenceUtil.VALUE_MAP_BAIDU);
			try {
				SettingSession.forceStopPackage(SettingSession.AUTONAVI_MINIMAP, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				SettingSession.forceStopPackage(SettingSession.KLD_MAP, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(map_index==2){
			UserPerferenceUtil.setMapChoose(mContext,UserPerferenceUtil.VALUE_MAP_KLD);
			try {
				SettingSession.forceStopPackage(SettingSession.AUTONAVI_MINIMAP, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				SettingSession.forceStopPackage(SettingSession.BAIDU_NAVI, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(map_index==4){
			UserPerferenceUtil.setMapChoose(mContext,UserPerferenceUtil.VALUE_MAP_TUBA);
			try {
				SettingSession.forceStopPackage(SettingSession.AUTONAVI_MINIMAP, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				SettingSession.forceStopPackage(SettingSession.BAIDU_NAVI, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			UserPerferenceUtil.setMapChoose(mContext,UserPerferenceUtil.VALUE_MAP_AMAP);
			try {
				SettingSession.forceStopPackage(SettingSession.BAIDU_NAVI, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				SettingSession.forceStopPackage(SettingSession.KLD_MAP, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// String poiVendor = cn.yunzhisheng.preference.PrivatePreference.getValue("poi_vendor",UserPreference.MAP_VALUE_AMAP);

	}
	
	/**
	 * add by zzj 20150918
	 */
	private void acquireWakeLock(int flags, String tag) {
		if (mWakeLock == null || !mWakeLock.isHeld()) {
			if(mPowerManager == null) {
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
}
