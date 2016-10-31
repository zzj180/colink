package com.colink.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.PowerManager.WakeLock;
import android.provider.CallLog;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android_serialport_api.SerialPort;

import com.colink.application.Application;
import com.colink.bluetoolthe.R;
import com.colink.bluetoothe.CallLogActivity;
import com.colink.bluetoothe.CallLogBean;
import com.colink.bluetoothe.ContactActivity;
import com.colink.bluetoothe.MainActivity;
import com.colink.bluetoothe.SettingActivity;
import com.colink.database.BlueTootheDB;
import com.colink.database.BlueTootheState;
import com.colink.database.CallLogProvider;
import com.colink.util.APPUtil;
import com.colink.util.Constact;
import com.colink.util.Contact;
import com.colink.util.SystemPropertiesProxy;

public class TelphoneService extends Service implements Constact {

	private static final String ACCDRIVER_ACCDRIVER = "/sys/class/accdriver_cls/accdriver/accdriver";

	private static final String ACTION_OPEN_WAKEUP = "android.intent.action.OPEN_WAKEUP";

	private static final String ACTION_CLOSE_WAKEUP = "android.intent.action.CLOSE_WAKEUP";

	public static final String ACTION_ECAR_NAVI = "com.android.ecar.send";
	public static final String ACTION_ECAR_send = "com.android.ecar.recv";
	 private static final String BLUETOOTH_PHONE_STATE = "com.coogo.BLUETOOTH_PHONE_STATE";
	public static String _KEYS_ = "keySet";
	public static String _TYPE_STANDCMD_ = "standCMD";
	public static String _CMD_ = "ecarSendKey";
	public static String _TYPE_ = "cmdType";
	public static final String KEY_PLATFORM = "ro.os.version";
	private static final String STARTDOWN = "开始导入通讯录";

	// private static final String DOWNVOER = "通讯录导入结束";

	private static final String INCOMING = "来电";

	private static final String UNKNOW_INCOMMING = "陌生人来电";

	private static final String SECTION = Phone.NUMBER + "=";

	private static final String CONTACT_NUMBER = "number";

	private static final String ACTION_BLUETOOTH_PHONEBOOK = "android.intent.action.BLUETOOTH_PHONEBOOK";
	
	private static final String ACTION_CALLLOG_RECEIVE = "android.intent.action.BLUETOOTH_CALLLOG";
	private static final String ACTION_DIALPAD_RECEIVE = "android.intent.action.BLUETOOTH_DIALPAD";
	private static final String ACTION_SYNCCONTACT_RECEIVE = "android.intent.action.PHONEBOOK_SYNC";
	private static final String ACTION_OPEN_ACTION = "android.intent.action.MATCH_BLUETOOTH";
	private static final String ACTION_CLOSE_ACTION = "android.intent.action.CLOSE_BLUETOOTH";
	private static final String ACTION_REDIAL_RECEIVE = "com.colink.service.TelphoneService.TelephoneReDialReceive";
	private static final String ACTION_ANSWER_RECEIVE = "com.colink.service.TelphoneService.TelephoneAnswerReceive";
	private static final String ACTION_CALLOUT_RECEIVE = "com.colink.service.TelphoneService.TelephoneReceive";
	private static final String ACTION_HANDUP_RECEIVE = "com.colink.service.TelphoneService.TelephoneHandupReceive";
	private static final String ACTION_BLUETOOTH_PHONE_STATE = "android.intent.action.BLUETOOTH_PHONE_STATE";
	private static final String ACTION_CLOSE_FMAUDIO = "android.intent.action.CLOSE_FMAUDIO";
	private static final String ACTION_OPEN_FMAUDIO = "android.intent.action.OPEN_FMAUDIO";
	private static final String ACTION_ACC_OFF = "android.intent.action.ACC_OFF_KEYEVENT";


	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
	private Point mWindowSize = new Point();
	private ImageButton handup;
	public ImageButton change, answer;
	public TextView phone_name;
	public TextView phone_view;
	private View view;
	public ImageView phone_state;
	public Chronometer phone_time;
	int notifiactionVolumn;
	private WakeLock wl;

	// private static final String ACTION_INIT_DATA =
	// "cn.yunzhisheng.intent.action.INIT_DATA";

	// private static final String BLUETOOTH_SYNC_SUCCESS_ACTION =
	// "android.intent.action.BLUETOOTH_SYNC_SUCCESS";
	public final static Uri RAW_CONTENT_URI = Uri
			.parse("content://com.android.contacts/raw_contacts");
	/*
	 * private final static Uri DATA_CONTENT_URI = Uri
	 * .parse("content://com.android.contacts/data");
	 */
	private static final String TAG = "TelphoneService";

	protected Application mApplication;

	protected SerialPort mSerialPort;

	protected static OutputStream mOutputStream;

	private InputStream mInputStream;

	private HandlerThread mReadThread;

//	private HandlerThread insertThread;

//	private Handler insertHander;

	private int count;

	private int mType;

	private String mPhoneNumber;

	private String mName;

	private HandlerThread mHandlerThread;

	private Handler mWriteHander, mReadHander;

	private CallLogBean calllog;

	public static boolean isDownPhone;
	private String ACC_STATE = "acc_state";
	// add aios contact
	// private List<Contact> list = new ArrayList<Contact>();
	private IBinder binder = new localBinder();
	private ArrayList<Contact> list;
	private Runnable read = new Runnable() {
		@Override
		public void run() {
			if (mInputStream != null) {
				try {
					PutChar((byte) mInputStream.read());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			mReadHander.post(read);
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler mManagerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CALL_STATUS_INCOMING:
				inComming();
				break;
			case CALL_STATUS_OUTCALL:
				outcall();
				break;
			case CALL_STATUS_OUTCALLING:
				outgoing();
				break;
			case BLUE_NOT_CONNECTED:
				blueDisConnent();
				break;
			case CALL_STATUS_HAND_UP:
				dismiss();
				break;
			case RESUME_VOICE:
			/*	if (notifiactionVolumn > 0) {
					getAudioManager().setStreamVolume(
							AudioManager.STREAM_MUSIC,
							notifiactionVolumn * 2 + 1, 0);
				} else {
					getAudioManager().setStreamVolume(
							AudioManager.STREAM_MUSIC, 0, 0);
				}*/
				
				String platform = SystemPropertiesProxy.get(TelphoneService.this, KEY_PLATFORM);
				if(TextUtils.isEmpty(platform)){
					getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC,notifiactionVolumn * 2, 0);
				}else{
					//mtk
					getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC,notifiactionVolumn, 0);
				}
				getAudioManager().setStreamVolume(AudioManager.STREAM_NOTIFICATION, notifiactionVolumn,0);
				getAudioManager().setStreamVolume(AudioManager.STREAM_ALARM, notifiactionVolumn,0);
				getAudioManager().setStreamVolume(AudioManager.STREAM_SYSTEM, notifiactionVolumn,0);
				break;
			default:
				break;
			}

		}
	};

	private void outcall() {
		if (!TextUtils.isEmpty(mPhoneNumber)) {
			SpannableString spannableString = new SpannableString(
					mPhoneNumber.trim());
			for (int i = 0; i < mPhoneNumber.trim().length(); i++) {
				Drawable drawable = getResources().getDrawable(
						R.drawable.p0 + (int) (mPhoneNumber.charAt(i) - 48));
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				spannableString.setSpan(new ImageSpan(drawable), i, i + 1,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			phone_view.setText(spannableString);
			phone_state.setImageResource(R.drawable.qudian);
			phone_name.setText(mName);
			change.setVisibility(View.VISIBLE);
			answer.setVisibility(View.GONE);
			phone_time.setVisibility(View.INVISIBLE);
			show();
			// 翼卡在线 delete by zzj 20150915
		}
	}

	private void inComming() {
		if (view != null && view.isShown()) {
			/*Toast.makeText(this, mPhoneNumber + "来电", Toast.LENGTH_SHORT)
					.show();*/
		} else {
			try {
				mName = getContactNameByNumber(mPhoneNumber.trim());
			} catch (Exception e1) {
				mName = null;
			}
			// delete by zzj 20150915
			Intent ecar = new Intent(CALL_RECEIVE_ACTION);
			ecar.putExtra(ECAR_RECEICVER_CMD, CALL_STATUS_INCOMING);
			sendStickyBroadcast(ecar);
			writeQn802x(1);

			if (!TextUtils.isEmpty(mPhoneNumber)) {
				SpannableString spannableString = new SpannableString(
						mPhoneNumber.trim());
				for (int i = 0; i < mPhoneNumber.trim().length(); i++) {
					Drawable drawable = getResources()
							.getDrawable(R.drawable.p0 + (int) (mPhoneNumber.charAt(i) - 48));
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight());
					spannableString.setSpan(new ImageSpan(drawable), i, i + 1,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				phone_view.setText(spannableString);
				phone_state.setImageResource(R.drawable.laidian);
				change.setVisibility(View.GONE);
				answer.setVisibility(View.VISIBLE);
				phone_time.setVisibility(View.INVISIBLE);
				phone_name.setText(mName);
				show();
				if (Application.auto_call) {
					if (TextUtils.isEmpty(mName)) {
						playTTS(UNKNOW_INCOMMING);
					} else {
						playTTS(mName + INCOMING);
					}
					sendCommand(CALL_ANSWER_COMMAND, 5000);
				} else {
					Intent vui = new Intent(ACTION_BLUETOOTH_PHONE_STATE);
					vui.putExtra("state", TelephonyManager.CALL_STATE_RINGING);
					vui.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER,
							mPhoneNumber.trim());
					sendBroadcast(vui);
				}
			}
			/*
			 * Intent in = new Intent(TelphoneService.this, DialActivity.class);
			 * in.putExtra("state", INCOMING_CALL);
			 * in.putExtra(OUTGOING_FEEDBACK_IR, mName);
			 * in.putExtra(CONTACT_NUMBER, mPhoneNumber); // sendBroadcast(i);
			 * in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(in);
			 */

		}
	}

	private void blueDisConnent() {
		if (view != null && view.isShown())
			dismiss();
		ContentValues con = new ContentValues();
		con.put(BlueTootheDB.DEVICENAME, Application.device_address);
		con.put(BlueTootheDB.ONLINE, 0);
		// getContentResolver().insert(BlueTootheState.CONTENT_URI, con);
		getContentResolver().update(BlueTootheState.CONTENT_URI, con, null,null);
		
		Intent coff = new Intent(ACTION_BLUE_STATE);
		sendBroadcast(coff);
		Intent setdis = new Intent(ACTION_DEVICE_STATE);
		sendBroadcast(setdis);

		// delete by zzj 20150915
		Intent ecar = new Intent(CALL_RECEIVE_ACTION);
		ecar.putExtra(ECAR_RECEICVER_CMD, BLUE_NOT_CONNECTED);
		sendStickyBroadcast(ecar);
	}

	private void outgoing() {
		if (view != null && view.isShown()) {
			time = System.currentTimeMillis();
			Intent intnet = new Intent(ACTION_CLOSE_WAKEUP);
			sendBroadcast(intnet);
			/*getAudioManager().setStreamVolume(AudioManager.STREAM_NOTIFICATION,
					0, AudioManager.FLAG_ALLOW_RINGER_MODES);
			getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, 0,
					AudioManager.FLAG_ALLOW_RINGER_MODES);
			getAudioManager().setStreamVolume(AudioManager.STREAM_SYSTEM, 0,
					AudioManager.FLAG_ALLOW_RINGER_MODES);
			getAudioManager().setStreamVolume(AudioManager.STREAM_ALARM, 0,
					AudioManager.FLAG_ALLOW_RINGER_MODES);*/
			setMute(true);
			phone_state.setImageResource(R.drawable.tonghuazhong);
			phone_time.setVisibility(View.VISIBLE);
			phone_time.setBase(SystemClock.elapsedRealtime());
			phone_time.start();
			change.setVisibility(View.VISIBLE);
			answer.setVisibility(View.GONE);
		}
		Intent ecar = new Intent(CALL_RECEIVE_ACTION);
		ecar.putExtra(ECAR_RECEICVER_CMD, CALL_STATUS_OUTCALLING);
		sendStickyBroadcast(ecar);
		if (Application.state == INCOMING_CALL) {
			mType = CONNECTED;
		}
		
		Intent phone_offhook = new Intent(ACTION_BLUETOOTH_PHONE_STATE);
		phone_offhook.putExtra("state", TelephonyManager.CALL_STATE_OFFHOOK);
		sendBroadcast(phone_offhook);
		 
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class localBinder extends Binder {
		public TelphoneService getService() {
			return TelphoneService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = (Application) getApplication();
		try {
			mSerialPort = mApplication.getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// Create a receiving thread
		// mReadThread = new ReadThread();
		mReadThread = new HandlerThread("readThreaed");
		mReadThread.start();
		mReadHander = new Handler(mReadThread.getLooper());
		mReadHander.post(read);
		mHandlerThread = new HandlerThread("writeThread");
		mHandlerThread.start();
		mWriteHander = new Handler(mHandlerThread.getLooper()){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 3:
					
					Intent intent = new Intent(TelphoneService.this, PhoneBookService.class);
					intent.putExtra(CONTACTS_KEY, list);
					intent.putExtra(ISOVER_KEY, true);
					startService(intent);
					if(list!=null)
					list.clear();
					break;
				default:
					break;
				}
			}
		};
		sendCommand(QUERY_AUTO_COMMAND, 7500);
		// sendCommand(QUERY_A2DP_SWITCH, 7800);
		String customer = SystemPropertiesProxy.get(this,"ro.inet.consumer.code");
		String bName = SystemPropertiesProxy.get(this,"ro.coogo.bluetooth.name");
		if (!getSharedPreferences(BLUETOOTH, MODE_PRIVATE).getBoolean(SP_UNICAR_KEY_IS_FIRST_START, false)) {
			if (!TextUtils.isEmpty(bName)) {
				sendCommand("AT#MM" + bName + "\r\n", 8000);
			} else {
				sendCommand("AT#MMCOOGO" + (int) (Math.random() * 9000 + 1000) + "\r\n", 8000);
			}
			if ("006".equals(customer)) {
				insertPhone("九目服务热线", "4006585802");
			}/* else if ("003".equals(customer)) {
				sendCommand(CHANGE_AUDIO_COMMAND, 6500);
			}*/
			sendCommand(AT_MM, 8500);
		}
		if("174".equals(customer)){
			sendCommand(A2DP_CLOSE,4500);
		}
		getContentResolver().registerContentObserver(
				Settings.System.getUriFor(ACC_STATE), true,
				new ContentObserver(mWriteHander) {
					@Override
					public void onChange(boolean selfChange) {
						boolean acc_state = Settings.System.getInt(getContentResolver(), ACC_STATE, 1) == 1;
						Log.d(TAG, "acc_state = " + acc_state);
						if (!acc_state) {
							mManagerHandler.sendEmptyMessage(CALL_STATUS_HAND_UP);
							/*if(mReadThread!=null)
							mReadThread.quit();
							if(mHandlerThread!=null)
							mHandlerThread.quit();
							try {
								if(mInputStream!=null)
								mInputStream.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							try {
								if(mOutputStream!=null)
								mOutputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							Application.getInstance().closeSerialPort();*/
				//			Process.killProcess(Process.myPid());
						}else{
							setMute(false);
							/*try {
								mSerialPort = mApplication.getSerialPort();
								mOutputStream = mSerialPort.getOutputStream();
								mInputStream = mSerialPort.getInputStream();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							mReadThread = new HandlerThread("readThreaed");
							mReadThread.start();
							mReadHander = new Handler(mReadThread.getLooper());
							mReadHander.post(read);
							mHandlerThread = new HandlerThread("writeThread");
							mHandlerThread.start();
							mWriteHander = new Handler(mHandlerThread.getLooper()){
								@Override
								public void handleMessage(Message msg) {
									switch (msg.what) {
									case 3:
										
										Intent intent = new Intent(TelphoneService.this, PhoneBookService.class);
										intent.putExtra(CONTACTS_KEY, list);
										intent.putExtra(ISOVER_KEY, true);
										startService(intent);
										list.clear();
										break;
									default:
										break;
									}
								}
							};*/
						}
					}
				});
		if (TextUtils.isEmpty(getSharedPreferences(BLUETOOTH, MODE_PRIVATE).getString(VERSION_KEY, null))) {
			sendCommand(QUERY_VERSION_COMMAND, 10000);
		}
		registerReceiver();
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		view = View.inflate(this, R.layout.incalling, null);
		findViews();
		initWindowParams();
		setMute(false);
	/*	String id = SystemPropertiesProxy.get(this, "ro.build.display.id");
		if (id.contains("CV86")) {
			sendCommand(CHANGE_AUDIO_COMMAND, 6500);
		}*/
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {
			case R.id.answer:
				sendCommand(CALL_ANSWER_COMMAND);
				break;
			case R.id.hangup:
				sendCommand(HANDUP_COMMAND);
				dismiss();
				break;
			case R.id.incalling:
				sendCommand(CHANGE_COMMAND);
				break;
			default:
				break;
			}
		}
	};

	private void findViews() {
		phone_view = (TextView) view.findViewById(R.id.text_number);
		phone_name = (TextView) view.findViewById(R.id.text_name);
		phone_state = (ImageView) view.findViewById(R.id.text_state);
		phone_time = (Chronometer) view.findViewById(R.id.calltime);
		change = (ImageButton) view.findViewById(R.id.incalling);
		answer = (ImageButton) view.findViewById(R.id.answer);
		handup = (ImageButton) view.findViewById(R.id.hangup);
		answer.setOnClickListener(mOnClickListener);
		handup.setOnClickListener(mOnClickListener);
		change.setOnClickListener(mOnClickListener);
	}

	public void show() {
		if (view == null || view.isShown()) {
			return;
		}
		
		SendCallState(this, 5);
		Intent intent = new Intent(CALL_RECEIVE_ACTION);
		intent.putExtra(ECAR_RECEICVER_CMD, CALL_STATUS_OUTCALL);
		sendStickyBroadcast(intent);
		if (ECAR_NUM2.equals(mPhoneNumber.trim())
				|| ECAR_NUM1.equals(mPhoneNumber.trim())
				|| ECAR_NUM3.equals(mPhoneNumber)
				|| ECAR_NUM4.equals(mPhoneNumber)) {

			if (exitsApk(ECAR_NEW_APP)) {
				ComponentName componetName = new ComponentName(ECAR_NEW_APP,ECAR_NEW_SERVICE);
				Intent ecar = new Intent();
				ecar.setComponent(componetName);
				try {
					stopService(ecar);
					startService(ecar);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				ComponentName componetName = new ComponentName(ECAR_APP,ECAR_SERVICE);
				Intent ecar = new Intent();
				ecar.setComponent(componetName);
				try {
					stopService(ecar);
					startService(ecar);
				} catch (Exception e) {
					e.printStackTrace();
				}
				yicar = true;
				sendCommand(AT_YS, 300);
			}
		}
	//	getAudioManager().setStreamMute(AudioManager.STREAM_MUSIC, true);
		getAudioManager().requestAudioFocus(null, AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		notifiactionVolumn = getAudioManager().getStreamVolume(AudioManager.STREAM_ALARM);
		sendBroadcast(new Intent(ACTION_CLOSE_FMAUDIO));
		try {
			mWindowManager.addView(view, mWindowParams);
			getWakeLock().acquire();
			getWakeLock().release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dismiss() {
		SendCallState(this, 3);
		Intent phone_idle = new Intent(ACTION_BLUETOOTH_PHONE_STATE);
		phone_idle.putExtra("state", TelephonyManager.CALL_STATE_IDLE);
		sendBroadcast(phone_idle);
		if (view == null || !view.isShown()) {
			return;
		}
		Log.d(TAG, "notifiactionVolumn=" + notifiactionVolumn);
	//	mManagerHandler.sendEmptyMessageDelayed(RESUME_VOICE, 2000);
		setMute(false);
		/*getAudioManager().setStreamVolume(AudioManager.STREAM_ALARM,
				notifiactionVolumn, AudioManager.FLAG_ALLOW_RINGER_MODES);*/
		sendBroadcast(new Intent(ACTION_OPEN_FMAUDIO));
		Intent ecar = new Intent(CALL_RECEIVE_ACTION);
		ecar.putExtra(ECAR_RECEICVER_CMD, CALL_STATUS_HAND_UP);
		sendStickyBroadcast(ecar);
		Intent intnet = new Intent(ACTION_OPEN_WAKEUP);
		sendBroadcast(intnet);
		getAudioManager().abandonAudioFocus(null);
		try {
			mWindowManager.removeViewImmediate(view);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		}
		writeQn802x(0);
	}

	private void initWindowParams() {
		mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		mWindowParams.format = PixelFormat.RGBA_8888; // 效果为背景透明
		// resetWindowParamsFlags();
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
		/*
		 * mWindowParams.flags = //
		 * WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL // |
		 * WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
		 * WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		 */
		mWindowParams.gravity = Gravity.CENTER;
		// mWindowParams.windowAnimations = R.anim.slide_right_in;
		int width = LayoutParams.MATCH_PARENT;
		int height = LayoutParams.MATCH_PARENT;
		mWindowParams.width = width;
		mWindowParams.height = height;
		updateDisplaySize();

	}

	@SuppressLint("NewApi")
	private void updateDisplaySize() {
		Display display = mWindowManager.getDefaultDisplay();
		display.getSize(mWindowSize);
	}

	private void resetWindowParamsFlags() {
		mWindowParams.flags &= ~(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_CALLLOG_RECEIVE);
		filter.addAction(ACTION_DIALPAD_RECEIVE);
		filter.addAction(ACTION_SYNCCONTACT_RECEIVE);
		filter.addAction(ACTION_OPEN_ACTION);
		filter.addAction(ACTION_CLOSE_ACTION);
		filter.addAction(ACTION_REDIAL_RECEIVE);
		filter.addAction(ACTION_ANSWER_RECEIVE);
		filter.addAction(ACTION_CALLOUT_RECEIVE);
		filter.addAction(ACTION_HANDUP_RECEIVE);
		filter.addAction(ACTION_BLUETOOTH_PHONEBOOK);
		filter.addAction(ACTION_ACC_OFF);
		filter.addAction(Constact.ECAR_SEND_ACTION);
		filter.addAction(ACTION_ECAR_NAVI);
		registerReceiver(telephoneReceive, filter);
	}

	private void playTTS(String content) {
		Intent intent = new Intent(PLAY_TTS);
		intent.putExtra(TTS_KEY, content);
		sendBroadcast(intent);
	}

	private synchronized void writeQn802x(final int n) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int retry = 2;
				FileOutputStream fos = null;
				do {
					try {
						retry--;
						fos = new FileOutputStream(DEVICE_FILE);
						byte[] wBuf = new byte[1];
						wBuf[0] = (byte) (n);
						fos.write(wBuf, 0, 1);
						fos.flush();
					} catch (Exception e) {
					} finally {
						if (fos != null) {
							try {
								fos.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					
					FileOutputStream fs;
					try {
						fs = new FileOutputStream(DEVICE_MTK_FILE);
						byte[] wBuf = new byte[3];
						wBuf[0] = (byte) 0xFF;
						wBuf[1] = (byte) (n);
						wBuf[2] = (byte) 0xFC;
						fs.write(wBuf, 0, 5);
						fs.flush();
						fs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				} while (retry != 0);
			}
		}).start();
	}

	public void sendContact(String action) {
		Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	/**
	 * 往数据库中新增通话记录
	 **/
	public void AddCallLogs(CallLogBean calllog) {

		ContentValues values = new ContentValues();

		values.put(CallLog.Calls.CACHED_NAME, calllog.getName());

		values.put(CallLog.Calls.NUMBER, calllog.getNumber());

		values.put(CallLog.Calls.TYPE, calllog.getType());

		values.put(CallLog.Calls.DATE, calllog.getDate());

		values.put(CallLog.Calls.DURATION, calllog.getDuration());

		getContentResolver().insert(CallLogProvider.CONTENT_URI, values);
	}

	/*
	 * private void insertContact(String name, String number) { Cursor cursor =
	 * getContentResolver().query(RAW_CONTENT_URI, new String[] {
	 * RawContacts.CONTACT_ID }, null, null, null); int id = 1; if
	 * (cursor!=null&&cursor.moveToLast()) { id = cursor.getInt(0); } int
	 * contactID = id + 1; ContentValues values = new ContentValues();
	 * values.put(RawContacts.CONTACT_ID, contactID);
	 * getContentResolver().insert(RAW_CONTENT_URI, values);
	 * insertPhone(contactID, name, number); }
	 * 
	 * private void insertPhone(int contactId, String name, String number) {
	 * ContentValues nameValue = new ContentValues();
	 * nameValue.put(Data.RAW_CONTACT_ID, contactId);
	 * nameValue.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
	 * nameValue.put(StructuredName.DISPLAY_NAME, name);
	 * getContentResolver().insert(DATA_CONTENT_URI, nameValue); //
	 * values.put(RAW_NUMBER, number); ContentValues phoneValue = new
	 * ContentValues(); phoneValue.put(Data.RAW_CONTACT_ID, contactId);
	 * phoneValue.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
	 * phoneValue.put(Phone.NUMBER, number);
	 * getContentResolver().insert(DATA_CONTENT_URI, phoneValue);
	 * 
	 * ContentValues value = new ContentValues(); value.put("phonename", name);
	 * value.put("phonenumber", number);
	 * getContentResolver().insert(EcarConatactsProvider.CONTENT_URI, value);
	 * 
	 * }
	 */
	@SuppressWarnings("deprecation")
	private void insertPhone(String name, String number) {
		ContentValues values = new ContentValues();
		values.put(People.NAME, name);
		Uri uri = getContentResolver().insert(People.CONTENT_URI, values);
		if (uri == null) {
			return;
		}
		Uri numberUri = Uri.withAppendedPath(uri,
				People.Phones.CONTENT_DIRECTORY);
		values.clear();
		values.put(People.Phones.TYPE, People.Phones.TYPE_MOBILE);
		values.put(People.NUMBER, number);
		if (numberUri == null) {
			return;
		}
		getContentResolver().insert(numberUri, values);

	}

	public synchronized void testInsertBatch(List<Contact> cs, boolean isOver) {
		ContentProviderClient client = getContentResolver()
				.acquireContentProviderClient(ContactsContract.AUTHORITY);
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = 0;
		try {
			for (Contact contact : cs) {
				rawContactInsertIndex = ops.size();
				ops.add(ContentProviderOperation
						.newInsert(RawContacts.CONTENT_URI)
						.withValue(RawContacts.ACCOUNT_TYPE, null)
						.withValue(RawContacts.ACCOUNT_NAME, null)
						.withYieldAllowed(true).build());

				// 添加姓名
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(Data.MIMETYPE,
								StructuredName.CONTENT_ITEM_TYPE)
						.withValue(StructuredName.DISPLAY_NAME,
								contact.getName()).withYieldAllowed(true)
						.build());
				// 添加号码
				ops.add(ContentProviderOperation
						.newInsert(
								android.provider.ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
						.withValue(Phone.NUMBER, contact.getPhone())
						.withValue(Phone.TYPE, Phone.TYPE_MOBILE)
						.withYieldAllowed(true).build());
			}
			client.applyBatch(ops);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.release();
			if (isOver) {
				isDownPhone = false;
				Intent contactDone = new Intent(ACTION_CONTACT_DONE);
				contactDone.putExtra("uri", ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
				sendBroadcast(contactDone);
				playTTS(OVERDOWN);
			}

		}
		// 在事务中对多个操作批量执行
		// resolver.applyBatch(ContactsContract.AUTHORITY, ops);
	}

	private boolean getContactInfo() {
		ContentResolver resolver = getContentResolver();
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		Cursor query = resolver.query(uri, null, null, null, null);
		if (query != null) {
			int j = query.getCount();
			query.close();
			return j > 0;
		} else {
			return true;
		}
	}

	private String getContactNameByNumber(String number) {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(uri,new String[] { Phone.DISPLAY_NAME }, SECTION + number, null,null);
		String name = null;
		if (cursor.moveToFirst()) {
			name = cursor.getString(0);
		}
		cursor.close();
		return name;
	}

	String str;
	String contactName = "";
	long time;
	boolean yicar = false;

	private synchronized void onDataReceived(final byte[] buffer, int size) {
		str = new String(buffer, 0, size);
		Log.d(TAG, size + "," + str);
		if (size < 2) {
			return;
		}
		switch (str.substring(0, 2)) {
		case OUTGOING_FEEDBACK_IR: // 拨打电话
			Application.state = OUTGOING_CALL;
			mType = OUTGOING_CALL;
			mPhoneNumber = new String(buffer, 2, size - 2);
			mName = null;
			try {
				mName = getContactNameByNumber(mPhoneNumber);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			mManagerHandler.sendEmptyMessage(CALL_STATUS_OUTCALL);
			break;
		case INCOMING_FEEDBACK_ID: // 来电
			// sendCommand("AT#MK0\r\n");
			Application.state = INCOMING_CALL;
			mType = INCOMING_CALL;
			mPhoneNumber = new String(buffer, 2, size - 2);
			mManagerHandler.sendEmptyMessage(CALL_STATUS_INCOMING);
			break;
		case DEVICE_FEEDBACK: // 配对设备信息
			pairDeivice(buffer, size);
			break;
		/*
		 * case VOICE_FEEDBACK: writeQn802x(1); break; case VOICECLOSE_FEEDBACK:
		 * writeQn802x(0); break;
		 */
		case PHONEBOOK_COMPLETE_FEEDBACK: // 电话本下载完成
			mWriteHander.removeMessages(3);
			mWriteHander.sendEmptyMessageDelayed(3, 5000);
			count = 0;
			// delete by zzj 20150916
			// playTTS(DOWNVOER);
			break;
		case PHONEBOOK_PREPARE_FEEDBACK: // 准备下载电话本
			count = 0;
			break;
		case PHONEBOOK_DOWNLOAD_FEEDBACK: // 下载电话中
			downPhoneBook(buffer, size);
			break;
		case OUTGOING_FEEDBACK_IC:
			writeQn802x(1);
			break;
		case DEVICENAME_FEEDBACK: // 蓝牙名
			Application.blueTooth_name = new String(buffer, 2, size - 2);
			getSharedPreferences(BLUETOOTH, MODE_PRIVATE).edit().putString(KEY_PRE, Application.blueTooth_name).putBoolean(SP_UNICAR_KEY_IS_FIRST_START, true).commit();
			Intent nameBlue = new Intent(ACTION_BLUE_NAME);
			sendBroadcast(nameBlue);
			Intent devicename = new Intent(ACTION_DEVICE_NAME);
			sendBroadcast(devicename);
			ContentValues cn = new ContentValues();
			cn.put(BlueTootheDB.SUPPORT, 1);
			// getContentResolver().insert(BlueTootheState.CONTENT_URI,con);
			getContentResolver().update(BlueTootheState.CONTENT_URI, cn,null, null);
			break;
		case HFPUNCONNECT_FEEDBACK: // 连接断开
			// isDownPhone = false;
			Application.device_address = null;
			Application.state = DISCONNECTED;
			mManagerHandler.sendEmptyMessage(BLUE_NOT_CONNECTED);
			SendCallState(this, 1);
			// getSharedPreferences("blueToothe",
			// Context.MODE_WORLD_READABLE).edit().putBoolean("online", false).commit();
			break;
		case HFPUNCONNECTTING_FEEDBACK: // 正在连接
			Application.device_address = null;
			Application.state = DISCONNECTED;
			break;
		case HFPCONNECT_FEEDBACK: // 蓝牙连接
			Application.state = CONNECTED;
			Intent co = new Intent(ACTION_BLUE_STATE);
			sendBroadcast(co);
			ContentValues con1 = new ContentValues();
			con1.put(BlueTootheDB.DEVICENAME, Application.device_address);
			con1.put(BlueTootheDB.ONLINE, 1);
			con1.put(BlueTootheDB.SUPPORT, 1);
			// getContentResolver().insert(BlueTootheState.CONTENT_URI,con);
			getContentResolver().update(BlueTootheState.CONTENT_URI, con1,null, null);
			SendCallState(this, 2);
			// getSharedPreferences("blueToothe",Context.MODE_WORLD_READABLE).edit().putBoolean("online",true).commit();
			break;
		case ANSWER_FEEDBACK: // 电话接起
			// sendCommand(CHANGE_AUDIO_COMMAND);
		/*	if (!getSharedPreferences(BLUETOOTH, MODE_PRIVATE).getBoolean(
					SP_UNICAR_KEY_IS_FIRST_VOICE, false)) {
				if ("003".equals(SystemPropertiesProxy.get(this,
						"ro.inet.consumer.code"))) {
					sendCommand(CHANGE_AUDIO_COMMAND);
				}
				getSharedPreferences(BLUETOOTH, MODE_PRIVATE).edit()
						.putBoolean(SP_UNICAR_KEY_IS_FIRST_VOICE, true).apply();
			}*/
			mManagerHandler.sendEmptyMessage(CALL_STATUS_OUTCALLING);
			if (yicar) {
				try {
					sendCommand(AT_YS);
					yicar = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			/*
			 * if(Application.blueMic){ Application.blueMic=false; }else{
			 * sendCommand(CHANGE_COMMAND); }
			 */
			break;
		// //delete by zzj 20150915
		case OUTGOING_FEEDBACK_IM: // 翼卡双DTMF解码
			Intent intent = new Intent(CALL_RECEIVE_ACTION);
			intent.putExtra(ECAR_RECEICVER_CMD, GOC_SEND_DTMF);
			intent.putExtra(ECAR_REC_DTMF, new String(buffer, 2, size - 2));
			sendStickyBroadcast(intent);
			break;
		case HANDUP_FEEDBACK: // 电话挂断
			mManagerHandler.sendEmptyMessage(CALL_STATUS_HAND_UP);
			calllog = new CallLogBean();
			if (time != 0) {
				time = System.currentTimeMillis() - time;
				calllog.setDuration(time);
			}

			writeQn802x(0);
			calllog.setType(mType);
			calllog.setName(mName);
			calllog.setNumber(mPhoneNumber);
			calllog.setDate(System.currentTimeMillis());
			if (!TextUtils.isEmpty(mPhoneNumber))
				AddCallLogs(calllog);
			time = 0;
			// sendCommand(CHANGE_AUDIO_COMMAND);
			break;
		case VERSION:
			String value = new String(buffer, 2, size - 2);
			getSharedPreferences(BLUETOOTH, MODE_PRIVATE).edit()
					.putString(VERSION_KEY, "版本号:" + value).commit();
			ContentValues con2 = new ContentValues();
			con2.put(BlueTootheDB.SUPPORT, 1);
			// getContentResolver().insert(BlueTootheState.CONTENT_URI,con);
			getContentResolver().update(BlueTootheState.CONTENT_URI, con2,null, null);
			break;
		case PIN_FEEDBACK: // pin码
			Application.pin_value = new String(buffer, 2, size - 2);
			Intent setpin = new Intent(ACTION_DEVICE_PIN);
			sendBroadcast(setpin);
			break;
		case AUTO_FEEDBACK: // 自动接听自动连接

			Application.auto_call = (buffer[2] == 49);

			Application.auto_conn = (buffer[3] == 49);
			ContentValues su = new ContentValues();
			su.put(BlueTootheDB.SUPPORT, 1);
			// getContentResolver().insert(BlueTootheState.CONTENT_URI,con);
			getContentResolver().update(BlueTootheState.CONTENT_URI, su,null, null);
			Intent auto = new Intent(ACTION_DEVICE_AUTO);
			sendBroadcast(auto);
			break;
		case A2DP_START: // 蓝牙音乐播放
			break;
		case A2DP_STOP: // 蓝牙音乐暂停
			break;

		case A2DP_FEEDBACK_IM:

			if (buffer[2] == 48) {
				getSharedPreferences(BLUETOOTH, MODE_PRIVATE).edit()
						.putBoolean(A2DP_SWITCH, false).commit();
			} else {
				getSharedPreferences(BLUETOOTH, MODE_PRIVATE).edit()
						.putBoolean(A2DP_SWITCH, true).commit();
			}
		default:
			break;
		}
	}
	int phoneLength;
	int nameLength;
	String contact_name;
	String contact_phone;
	private void downPhoneBook(final byte[] buffer, int size) {
		if (count == 0) {
			if (list == null)
				list = new ArrayList<Contact>();
			list.clear();
			isDownPhone = true;
			contactName = "";
			Intent contactStart = new Intent(ACTION_CONTACT_START);
			sendBroadcast(contactStart);
			playTTS(STARTDOWN);
		}
		mWriteHander.removeMessages(3);
		mWriteHander.sendEmptyMessageDelayed(3, 10000);
		count++;
		try {
			phoneLength = Integer.parseInt(new String(buffer, 4, 2));
			nameLength = Integer.parseInt(new String(buffer, 2, 2));
			if (nameLength + phoneLength + 6 <= size) {
				contact_name = new String(buffer, 6, nameLength);
				contact_phone = new String(buffer, 6 + nameLength, phoneLength);
				list.add(new Contact(contact_name, contact_phone));
				if (count % 100 == 0) {
					Intent intent = new Intent(TelphoneService.this, PhoneBookService.class);
					intent.putExtra(CONTACTS_KEY, list);
					intent.putExtra(ISOVER_KEY, false);
					startService(intent);
					list.clear();
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void pairDeivice(final byte[] buffer, int size) {
		Application.state = CONNECTED;
		Intent conn = new Intent(ACTION_BLUE_STATE);
		sendBroadcast(conn);
		if (size > 15) {
			Application.device_address = new String(buffer, 15, size - 15);
			Intent device = new Intent(ACTION_DEVICE_ADDRESS);
			sendBroadcast(device);
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (!getContactInfo()) {
						new Handler(getMainLooper()).post(new Runnable() {
							@Override
							public void run() {
								CreateDialog(TelphoneService.this);
							}
						});
					}
				}
			}).start();
		}
	}

	public void sendCommand(String command) {
		sendCommand(command, 0);
	}

	public void sendCommand(final String command, long delay) {
		if (mWriteHander != null)
			mWriteHander.postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						mOutputStream.write(command.getBytes());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, delay);
	}

	private static final int OVER = 10;

	byte buffer[] = new byte[128];
	int size = 0;

	public synchronized void PutChar(byte c) {
		// Log.e("serial_port", Byte.toString(c));
		if (size == 128)
			return;
		buffer[size] = c;
		if (c == OVER) {
			onDataReceived(buffer, size - 1);
			size = 0;
		} else {
			size++;
		}
		notifyAll();
	}

	@Override
	public void onDestroy() {
		setMute(false);
		if (mReadThread != null) {
			mReadThread.quit();
			mReadThread = null;
		}

		if (mHandlerThread != null) {
			mHandlerThread.quit();
			mHandlerThread = null;
		}

		try {
			if (mInputStream != null)
				mInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		unregisterReceiver(telephoneReceive);

		Application.getInstance().closeSerialPort();
		// startService(new Intent(this, TelphoneService.class));
		super.onDestroy();

	}

	BroadcastReceiver telephoneReceive = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			Log.d(TAG, action);
			if (ACTION_HANDUP_RECEIVE.equals(action)) {
				sendCommand(HANDUP_COMMAND);
			} else if (ACTION_CALLOUT_RECEIVE.equals(action)) {
				String number = intent.getStringExtra(CONTACT_NUMBER);
				if (Application.state != DISCONNECTED) {
					try {
						if (number.startsWith("86")) {
							number = "+" + number;
						}
						sendCommand("AT#CW" + number + "\r\n");
					} catch (InvalidParameterException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
				} else {
					int navi = Settings.System.getInt(getContentResolver(),"ONE_NAVI", 0);
					if(navi == 1 && APPUtil.getInstance().isInstalled("com.coagent.ecar")){
						Intent tmpIntent=new Intent("com.android.ecar.recv");
						tmpIntent.putExtra("ecarSendKey", "VoipMakeCall");
						tmpIntent.putExtra("cmdType", "standCMD");
						tmpIntent.putExtra("keySet", "name,number");
						tmpIntent.putExtra("number", number);
						context.sendBroadcast(tmpIntent);
					}else{
						Intent phone_offhook = new Intent(ACTION_BLUETOOTH_PHONE_STATE);
						phone_offhook.putExtra("state", TelephonyManager.CALL_STATE_IDLE);
						sendBroadcast(phone_offhook);
						createsetDialog(context);
					}
				}

			} else if (ACTION_ANSWER_RECEIVE.equals(action)) {
				sendCommand(CALL_ANSWER_COMMAND);
			} else if (ACTION_REDIAL_RECEIVE.equals(action)) {
				sendCommand(REDIAL_COMMAND);
			} else if (ACTION_ACC_OFF.equals(action)) {
				if (!readAccFile() && mManagerHandler!=null) {
					mManagerHandler.sendEmptyMessage(CALL_STATUS_HAND_UP);
				/*	dismiss();
					context.stopService(new Intent(
							BootReceiver.TELPHONE_SERVICE));
					Process.killProcess(android.os.Process.myPid());*/
				}
			} else if (ACTION_CLOSE_ACTION.equals(action)) {
				// 关闭蓝牙
				sendCommand(DISCONNECT_COMMAND);

			} else if (ACTION_OPEN_ACTION.equals(action)) {
				// 连接蓝牙
				sendCommand(CONNECT_COMMAND);
			} else if (ACTION_BLUETOOTH_PHONEBOOK.equals(action)) {
				// 打开通讯录
				Intent contact = new Intent(context, ContactActivity.class);
				contact.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(contact);
			} else if (ACTION_SYNCCONTACT_RECEIVE.equals(action)) {
				// 同步通讯录
				sendCommand(PHONEBOOK_SYN_COMMAND);
			} else if (ACTION_DIALPAD_RECEIVE.equals(action)) {
				// 打开拨号界面
				Intent dial = new Intent(context, MainActivity.class);
				dial.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(dial);
			} else if (ACTION_CALLLOG_RECEIVE.equals(action)) {
				Intent calllog = new Intent(context, CallLogActivity.class);
				calllog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(calllog);
			} else if (ACTION_ECAR_NAVI.equals(action)) {
				String cmd = intent.getStringExtra(_CMD_);
				// Log.e("TelphoneService", cmd);
				if ("BluetoothQueryState".equals(cmd)) {
					SendCallState(context,Application.state == DISCONNECTED ? 1 : 2);
				} else if ("BluetoothMakeCall".equals(cmd)) {
					if (Application.state != DISCONNECTED) {
						try {
							String number = intent.getStringExtra("number");
							if (number.trim().length() == 13) {
								number = "+" + number;
							}
							sendCommand("AT#CW" + number + "\r\n");
						} catch (InvalidParameterException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						}
					} else {
						createsetDialog(context);
					}
				} else if ("BluetoothEndCall".equals(cmd)) { // 挂断
					sendCommand(HANDUP_COMMAND);
				} else if ("HideCallUI".equals(cmd)) {
					String oper = intent.getStringExtra("oper");
					if ("hide".equals(oper)) {
						if (view != null) {
							view.setVisibility(View.GONE);
						}
					} else {
						if (view != null) {
							view.setVisibility(View.VISIBLE);
							dismiss();
						}
					}
				} else if ("TTSSpeak".equals(cmd)) {
					String text = intent.getStringExtra("text");
					Intent speck = new Intent("com.glsx.tts.speaktext");
					speck.putExtra("ttsText", text);
					context.sendBroadcast(speck);
				}

			}

			// delete by zzj 20150915
			else if (Constact.ECAR_SEND_ACTION.equals(action)) {
				int ecar_cmd = intent.getIntExtra(Constact.ECAR_SENDCMD, -1);
				switch (ecar_cmd) {
				case Constact.HAND_UP_CMD:
					sendCommand(HANDUP_COMMAND);
					break;
				case Constact.MAKE_CALL_CMD:
					String num = intent.getStringExtra(Constact.ECAR_CALL_NUMBER);
					String name = intent.getStringExtra(Constact.ECAR_CALL_NAME);
					if (Application.state == DISCONNECTED) {
						createsetDialog(context);
					} else {
						startCountDownTimer(context, 5000, name, num);
					}
					break;
				case Constact.SPEAKER_DISABLE_CMD:
					new Thread(new Runnable() {
						@Override
						public void run() {
							int retry = 2;
							do {
								try {
									FileOutputStream fos = new FileOutputStream(DEVICE_FILE);
									byte[] wBuf = new byte[1];
									wBuf[0] = (byte) 8;
									fos.write(wBuf, 0, 1);
									fos.flush();
									fos.close();
									retry--;
								} catch (FileNotFoundException e) {
								} catch (IOException e) {
								}
							} while (retry != 0);
						}
					}).start();
					break;
				case Constact.SPEAKER_ENABLE_CMD:

					break;
				case Constact.MAKE_CALL_OTHER_CMD:
					String number = intent.getStringExtra(Constact.ECAR_CALL_NUMBER);
					char[] cs = number.toCharArray();
					for (int i = 0; i < cs.length; i++) {
						try {
							sendCommand(Constact.DTMF_DIAL_PREFIX + cs[i]);
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} catch (InvalidParameterException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						}
					}
					break;
				case 17:
					sendCommand(HANDUP_COMMAND);
					break;
				default:
					break;
				}
			}

		}
	};

	@SuppressWarnings("finally")
	public static boolean readAccFile() {

		FileInputStream fis = null;
		byte[] rBuf = new byte[10];
		boolean accOn = true;
		try {
			fis = new FileInputStream(ACCDRIVER_ACCDRIVER);
			fis.read(rBuf);
			if (rBuf[0] == (byte) 0) {
				accOn = false;
			} else if (rBuf[0] == (byte) 1) {
				accOn = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return accOn;
		}
	}

	AlertDialog mAlertDialog;
	Button button1, button2;

	public void createsetDialog(final Context context) {
		if (mAlertDialog != null) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_state, null);
		button2 = (Button) v.findViewById(R.id.button2);
		Builder builder = new AlertDialog.Builder(context);
		((TextView) v.findViewById(R.id.blueName)).setText("蓝牙名称  : " + Application.blueTooth_name);
		((TextView) v.findViewById(R.id.pinMa)).setText("PIN 码  : " + Application.pin_value);
		playTTS("蓝牙未连接");
		builder.setView(v);
		mAlertDialog = builder.create();
		mAlertDialog.setCanceledOnTouchOutside(false);
		mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);// 显示
		mAlertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		v.findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent setting = new Intent(context, SettingActivity.class);
				setting.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(setting);
				mAlertDialog.dismiss();
			}
		});

		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAlertDialog.dismiss();
			}
		});
		CountDown_Cancel();
		;
		mAlertDialog.show();

		mAlertDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				mAlertDialog = null;
				if (mCountDownTimer != null) {
					mCountDownTimer.cancel();
					mCountDownTimer = null;
				}
			}
		});
	}

	CountDownTimer mCountDownTimer;

	private void CountDown_Cancel() {
		if (mCountDownTimer != null) {
			return;
		}
		mCountDownTimer = new CountDownTimer(10000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				button2.setText("取消(" + millisUntilFinished / 1000 + "s)");
			}

			@Override
			public void onFinish() {
				if (mAlertDialog != null) {
					mAlertDialog.dismiss();
				}
			}
		};
		mCountDownTimer.start();
	}

	ProgressBar progressBar;
	AlertDialog alertDialog;


	public void startCountDownTimer(Context context,
			final long countDownMillis, String name, final String number) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.call_content_view, null);
		progressBar = (ProgressBar) v.findViewById(R.id.progressBarWaiting);
		((TextView) v.findViewById(R.id.callDialogNumber)).setText(number);
		if (name == null) {
			if (number.equals(ECAR_NUM1) || number.equals(ECAR_NUM2)
					|| number.equals(ECAR_NUM3) || number.equals(ECAR_NUM4)) {
				((TextView) v.findViewById(R.id.callDialogName)).setText("翼卡在线");
				playTTS("吾秒后为您接通翼卡在线电话");
			} else {
				playTTS("吾秒后为您接通" + number + "电话");
			}
		} else {
			playTTS("吾秒后为您接通" + name + "电话");
		}
		Builder builder = new AlertDialog.Builder(context);
		builder.setView(v);
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);// 显示
		alertDialog.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		alertDialog.show();
		alertDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				mCountDownTimer.cancel();
			}
		});

		mCountDownTimer = new CountDownTimer(countDownMillis, 100) {

			public void onTick(long millisUntilFinished) {
				int progress = (int) ((countDownMillis - millisUntilFinished)
						/ (float) countDownMillis * progressBar.getMax());
				progressBar.setProgress(progressBar.getMax() - progress);
			}

			public void onFinish() {
				progressBar.setProgress(0);
				try {
					Application.getInstance().getSerialPort().getOutputStream()
							.write(("AT#CW" + number + "\r\n").getBytes());
				} catch (InvalidParameterException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				alertDialog.dismiss();
			}
		}.start();
	}

	public String getNumberFromString(String a) {

		String regEx = "[^0-9]";

		Pattern p = Pattern.compile(regEx);

		Matcher m = p.matcher(a);

		return m.replaceAll("").trim();

	}

	OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				if (mAlertDialog != null) {
					sendCommand(PHONEBOOK_SYN_COMMAND);
					/*
					 * Intent intent = new
					 * Intent(TelphoneService.this,ContactActivity.class);
					 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 * TelphoneService.this.startActivity(intent);
					 */
					mAlertDialog.dismiss();
				}
				break;
			default:
				if (mAlertDialog != null) {
					mAlertDialog.dismiss();
				}
				break;
			}

		}
	};


//导入电话弹框
	public void CreateDialog(Context context) {
		if (mAlertDialog != null) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.bluetoothe_alert, null);
		button1 = (Button) v.findViewById(R.id.button1);
		button2 = (Button) v.findViewById(R.id.button2);
		Builder builder = new AlertDialog.Builder(context);
		button1.setOnClickListener(l);
		button2.setOnClickListener(l);
		builder.setView(v);
		playTTS(getString(R.string.noPhoneBook));
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
				if (mCountDownTimer != null) {
					mCountDownTimer.cancel();
					mCountDownTimer = null;
				}
			}
		});
	}
	
	private void setMute(boolean state){
		AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamMute(1, state);
		audioManager.setStreamMute(2, state);
		audioManager.setStreamMute(3, state);
		audioManager.setStreamMute(4, state);
		audioManager.setStreamMute(5, state);
		audioManager.setStreamMute(6, state);
		audioManager.setStreamMute(7, state);
		audioManager.setStreamMute(8, state);
		audioManager.setStreamMute(9, state);
		Intent intent = new Intent(BLUETOOTH_PHONE_STATE);
		intent.putExtra("heedset", !state);
		sendBroadcast(intent);
	}

//导入电话弹框倒计时
	private void CountDown() {
		if (mCountDownTimer != null) {
			return;
		}
		mCountDownTimer = new CountDownTimer(10000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				button1.setText("导入(" + millisUntilFinished / 1000 + "s)");
			}

			@Override
			public void onFinish() {
				if (mAlertDialog != null) {
					sendCommand(PHONEBOOK_SYN_COMMAND);
					/*
					 * Intent intent = new
					 * Intent(TelphoneService.this,ContactActivity.class);
					 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 * TelphoneService.this.startActivity(intent);
					 */
					mAlertDialog.dismiss();
				}
			}
		};
		mCountDownTimer.start();
	}

	private AudioManager getAudioManager() {
		return (AudioManager) getSystemService(Service.AUDIO_SERVICE);
	}

//通知翼卡蓝牙状态
	public void SendCallState(Context iContext, int iCallState) {
		if (iContext != null) {
			String tmpState = String.valueOf(iCallState);
			Intent tmpIntent = new Intent(ACTION_ECAR_send);
			tmpIntent.putExtra(_CMD_, "CallState");
			tmpIntent.putExtra(_TYPE_, _TYPE_STANDCMD_);
			tmpIntent.putExtra(_KEYS_, "state");
			tmpIntent.putExtra("state", tmpState);
			iContext.sendBroadcast(tmpIntent);
		}
	}

	private boolean exitsApk(String packageName) {
		PackageManager pm = getPackageManager();
		List<PackageInfo> packlist = pm.getInstalledPackages(0);
		for (int i = 0; i < packlist.size(); i++) {
			PackageInfo pak = (PackageInfo) packlist.get(i);

			if (pak.packageName.equals(packageName)) {
				return true;
			}

		}
		return false;

	}
	
	public WakeLock getWakeLock() {
		if (wl == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP,getPackageName());
		}
		return wl;
	}

}
