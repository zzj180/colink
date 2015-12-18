package com.colink.bluetoothe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.colink.application.Application;
import com.colink.bluetoolthe.R;
import com.colink.service.TelphoneService;
import com.colink.service.TelphoneService.localBinder;
import com.colink.util.Constact;

public class DialActivity extends Activity implements OnClickListener, Constact {
	private ImageButton handup;
	public ImageButton change, answer;
	public TextView phone_name;
	public TextView phone_view;
	public ImageView phone_state;
	public Chronometer phone_time;
	PowerManager powerManager = null;
	WakeLock wakeLock = null;
	public TelphoneService mService;
	private final static String NO_DIDTURB = "no_disturb";
	private static final String ACTION_BLUETOOTH_PHONE_STATE = "android.intent.action.BLUETOOTH_PHONE_STATE";
	private static final String ACTION_CLOSE_FMAUDIO="android.intent.action.CLOSE_FMAUDIO";
	private static final String ACTION_OPEN_FMAUDIO="android.intent.action.OPEN_FMAUDIO";
	int musicVolumn;
	int notifiactionVolumn;
	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((localBinder) service).getService();
		}
	};
	private int time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	/*	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

		// No Titlebar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incalling);
		getAudioManager().requestAudioFocus(null, AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		Intent service = new Intent(this, TelphoneService.class);
		bindService(service, conn, BIND_AUTO_CREATE);
		phone_view = (TextView) findViewById(R.id.text_number);
		phone_name = (TextView) findViewById(R.id.text_name);
		phone_state = (ImageView) findViewById(R.id.text_state);
		phone_time = (Chronometer) findViewById(R.id.calltime);
		change = (ImageButton) findViewById(R.id.incalling);
		answer = (ImageButton) findViewById(R.id.answer);
		musicVolumn = getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC);
		notifiactionVolumn = getAudioManager().getStreamVolume(AudioManager.STREAM_NOTIFICATION);
		time = Settings.System.getInt(getContentResolver(), NO_DIDTURB, -1);
		if (time != -1) {
			Settings.System.putInt(getContentResolver(), NO_DIDTURB, -1);
		}
		sendBroadcast(new Intent(ACTION_CLOSE_FMAUDIO));
		Intent intent = getIntent();
		if (intent != null) {
			String name = intent.getStringExtra(OUTGOING_FEEDBACK_IR);
			phone_name.setText(name);
			String mPhoneNumber = intent.getStringExtra("number");
			if ("4008015170".equals(mPhoneNumber)|| "075787807160".equals(mPhoneNumber)) {
				ComponentName componetName = new ComponentName(Constact.ECAR_APP, Constact.ECAR_SERVICE);
				Intent ecar = new Intent();
				ecar.setComponent(componetName);
				try {
					stopService(ecar);
					startService(ecar);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			int type = intent.getIntExtra("state", 0);
			if (type == OUTGOING_CALL) {
				phone_state.setImageResource(R.drawable.qudian);
				change.setVisibility(View.VISIBLE);
				answer.setVisibility(View.GONE);
				getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, 0,
						AudioManager.FLAG_ALLOW_RINGER_MODES);
				getAudioManager().setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0,
						AudioManager.FLAG_ALLOW_RINGER_MODES);
				getAudioManager().setStreamVolume(AudioManager.STREAM_ALARM, 0,
						AudioManager.FLAG_ALLOW_RINGER_MODES);
			} else if (type == INCOMING_CALL) {
				phone_state.setImageResource(R.drawable.laidian);
				change.setVisibility(View.GONE);
				answer.setVisibility(View.VISIBLE);
				answer.setOnClickListener(this);
				if(!Application.auto_call){
					Intent vui = new Intent(ACTION_BLUETOOTH_PHONE_STATE);
					vui.putExtra("state", TelephonyManager.CALL_STATE_RINGING);
					vui.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, mPhoneNumber.trim());
					sendBroadcast(vui);
				}
			}else{
				phone_state.setImageResource(R.drawable.tonghuazhong);
				phone_time.setVisibility(View.VISIBLE);
				phone_time.setBase(SystemClock.elapsedRealtime());
				phone_time.start();
				change.setVisibility(View.VISIBLE);
				answer.setVisibility(View.GONE);
				phone_view.setText("蓝牙电话");
				getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_ALLOW_RINGER_MODES);
				getAudioManager().setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_ALLOW_RINGER_MODES);
				getAudioManager().setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_ALLOW_RINGER_MODES);
			}
			if(!TextUtils.isEmpty(mPhoneNumber)){
				SpannableString spannableString = new SpannableString(mPhoneNumber.trim());
				for (int i = 0; i < mPhoneNumber.trim().length(); i++) {
					Drawable drawable = getResources().getDrawable(R.drawable.p0 + (int) (mPhoneNumber.charAt(i) - 48));
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
					spannableString.setSpan(new ImageSpan(drawable), i, i + 1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				phone_view.setText(spannableString);
			}
		}
		handup = (ImageButton) findViewById(R.id.hangup);
		handup.setOnClickListener(this);
		change.setOnClickListener(this);
		
	}

	private AudioManager getAudioManager() {
		return (AudioManager) getSystemService(Service.AUDIO_SERVICE);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		if (wakeLock == null) {
			powerManager = (PowerManager) getSystemService(POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP| PowerManager.SCREEN_DIM_WAKE_LOCK, getClass().getCanonicalName());
		}
		wakeLock.acquire();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {

		super.onPause();
		if (wakeLock == null) {
			powerManager = (PowerManager) getSystemService(POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, getClass().getCanonicalName());
		}
		wakeLock.setReferenceCounted(false);
		wakeLock.release();
		wakeLock = null;
	}

	@SuppressLint("Wakelock")
	@Override
	protected void onDestroy() {
		getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC,
				musicVolumn, AudioManager.FLAG_ALLOW_RINGER_MODES);
		getAudioManager().setStreamVolume(AudioManager.STREAM_NOTIFICATION,
				notifiactionVolumn, AudioManager.FLAG_ALLOW_RINGER_MODES);
		getAudioManager().setStreamVolume(AudioManager.STREAM_ALARM,
				notifiactionVolumn, AudioManager.FLAG_ALLOW_RINGER_MODES);
		sendBroadcast(new Intent(ACTION_OPEN_FMAUDIO));
		Intent intnet = new Intent("android.intent.action.OPEN_WAKEUP");  //打开语音唤醒
		sendBroadcast(intnet);
		getAudioManager().abandonAudioFocus(null);
		Intent phone_idle = new Intent(ACTION_BLUETOOTH_PHONE_STATE);		//通话结束
		phone_idle.putExtra("state", TelephonyManager.CALL_STATE_IDLE);
		sendBroadcast(phone_idle);
		if (time != -1) {
			Settings.System.putInt(getContentResolver(), NO_DIDTURB, time);
		}
		if (wakeLock != null) {
			wakeLock.setReferenceCounted(false);
			wakeLock.release();
			wakeLock = null;
		}
		unbindService(conn);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hangup:
			mService.sendCommand(HANDUP_COMMAND);
			finish();
			break;
		case R.id.answer:
			mService.sendCommand(CALL_ANSWER_COMMAND);
			break;
		default:
			mService.sendCommand(CHANGE_COMMAND);
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
