 package com.colink.bluetoothe;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.colink.application.Application;
import com.colink.bluetoolthe.R;
import com.colink.service.BootReceiver;
import com.colink.service.TelphoneService;
import com.colink.service.TelphoneService.localBinder;
import com.colink.util.Constact;

public class MainActivity extends Activity implements OnClickListener, Constact {

	private static final String GLSX_AUTONAVI = "com.glsx.autonavi";
	private final static String ONE_NAVI= "ONE_NAVI";

	private TextView phone_view;

	public TextView blueName, blueState;
	private SoundPool soundPool;
	String blue_name;
	private TelphoneService mService;
	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			mService = ((localBinder) service).getService();

			blue_name = getSharedPreferences(BLUETOOTH, MODE_PRIVATE).getString(KEY_PRE, DEFAULT_NAME);

			blueName.setText(blue_name);

			// mService.sendCommand(DEVICE_QUERY_COMMAND, 1050);

			if (Application.state == CONNECTED) {

				blueState.setText(getString(R.string.connected));
			}

			mService.sendCommand(QUERY_PIN_COMMAND, 2000);

		}

	};
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(ACTION_BLUE_STATE.equals(action)){
				if(Application.state==CONNECTED)
					blueState.setText(getString(R.string.connected));
				else
					blueState.setText(getString(R.string.unconnect));
			}else if(ACTION_BLUE_NAME.equals(action)){
				blueName.setText(Application.blueTooth_name);
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

		setContentView(R.layout.activity_main);
		phone_view = (TextView) findViewById(R.id.show);

		blueName = (TextView) findViewById(R.id.bluename);

		blueState = (TextView) findViewById(R.id.bluestate);
		
		IntentFilter filter=new IntentFilter();
		filter.addAction(ACTION_BLUE_STATE);
		filter.addAction(ACTION_BLUE_NAME);
		registerReceiver(receiver, filter);
		
		Intent service = new Intent(this, TelphoneService.class);
		startService(service);

		bindService(service, conn, BIND_AUTO_CREATE);

		ImageButton del = (ImageButton) findViewById(R.id.del);

		del.setOnClickListener(this);

		findViewById(R.id.dial).setOnClickListener(this);

		findViewById(R.id.home).setOnClickListener(this);

		findViewById(R.id.contact).setOnClickListener(this);

		findViewById(R.id.music).setOnClickListener(this);

		findViewById(R.id.setting).setOnClickListener(this);

		findViewById(R.id.change).setOnClickListener(this);

		findViewById(R.id.log).setOnClickListener(this);

		del.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				phone_view.setText(null);

				return true;
			}

		});

		for (int i = 0; i < 12; i++) {

			View v = findViewById(R.id.num1 + i);

			v.setOnClickListener(this);

		}

		initSound();
		/*
		 * Display display = getWindowManager().getDefaultDisplay();
		 * DisplayMetrics displayMetrics = new DisplayMetrics();
		 * display.getMetrics(displayMetrics); float density =
		 * displayMetrics.density; //得到密度 float width =
		 * displayMetrics.widthPixels;//得到宽度 float height =
		 * displayMetrics.heightPixels;//得到高度 Log.e("ss",
		 * density+","+width+","+height);
		 */

	}

	public void initSound() {
		soundPool = new SoundPool(12, AudioManager.STREAM_DTMF, 2);
		soundPool.load(this, R.raw.dtmf_0, 1);
		soundPool.load(this, R.raw.dtmf_1, 1);
		soundPool.load(this, R.raw.dtmf_2, 1);
		soundPool.load(this, R.raw.dtmf_3, 1);
		soundPool.load(this, R.raw.dtmf_4, 1);
		soundPool.load(this, R.raw.dtmf_5, 1);
		soundPool.load(this, R.raw.dtmf_6, 1);
		soundPool.load(this, R.raw.dtmf_7, 1);
		soundPool.load(this, R.raw.dtmf_8, 1);
		soundPool.load(this, R.raw.dtmf_9, 1);
		soundPool.load(this, R.raw.dtmf_pound, 1);
		soundPool.load(this, R.raw.dtmf_star, 1);
	}

	public void call(String phone) {

		if (mService != null)
			if(phone.trim().length()==13){
				phone="+"+phone;
			}
			mService.sendCommand("AT#CW" + phone + "\r\n");
		// Application.blueMic=true;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
		unregisterReceiver(receiver);
		if(soundPool!=null)
			soundPool.release();

	}

	private void input(String str) {
		String p = phone_view.getText().toString();
		phone_view.setText(p + str);
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.num0:
			if(soundPool!=null)
			soundPool.play(1,1, 1, 0, 0, 1);
		//	TTSController.getInstance(this).playText(NUMBER_0);
			input(NUMBER_0);
			mService.sendCommand(DTMF_DIAL_PREFIX + NUMBER_0 + COMMAND_FUFFIX);
			break;

		case R.id.num1:
			if(soundPool!=null)
			soundPool.play(2,1, 1, 0, 0, 1);
		//	TTSController.getInstance(this).playText(NUMBER_1);
			input(NUMBER_1);
			mService.sendCommand(DTMF_DIAL_PREFIX + NUMBER_1 + COMMAND_FUFFIX);
			break;

		case R.id.num2:
			if(soundPool!=null)
			soundPool.play(3,1, 1, 0, 0, 1);
		//	TTSController.getInstance(this).playText(NUMBER_2);
			input(NUMBER_2);
			mService.sendCommand(DTMF_DIAL_PREFIX + NUMBER_2 + COMMAND_FUFFIX);
			break;

		case R.id.num3:
			if(soundPool!=null)
			soundPool.play(4,1, 1, 0, 0, 1);
		//	TTSController.getInstance(this).playText(NUMBER_3);
			input(NUMBER_3);
			mService.sendCommand(DTMF_DIAL_PREFIX + NUMBER_3 + COMMAND_FUFFIX);
			break;

		case R.id.num4:
			if(soundPool!=null)
			soundPool.play(5,1, 1, 0, 0, 1);
		//	TTSController.getInstance(this).playText(NUMBER_4);
			input(NUMBER_4);
			mService.sendCommand(DTMF_DIAL_PREFIX + NUMBER_4 + COMMAND_FUFFIX);
			break;

		case R.id.num5:
			if(soundPool!=null)
			soundPool.play(6,1, 1, 0, 0, 1);
		//	TTSController.getInstance(this).playText(NUMBER_5);
			input(NUMBER_5);
			mService.sendCommand(DTMF_DIAL_PREFIX + NUMBER_5 + COMMAND_FUFFIX);
			break;

		case R.id.num6:
			if(soundPool!=null)
			soundPool.play(7,1, 1, 0, 0, 1);
		//	TTSController.getInstance(this).playText(NUMBER_6);
			input(NUMBER_6);
			mService.sendCommand(DTMF_DIAL_PREFIX + NUMBER_6 + COMMAND_FUFFIX);
			break;

		case R.id.num7:
			if(soundPool!=null)
			soundPool.play(8,1, 1, 0, 0, 1);
		//	TTSController.getInstance(this).playText(NUMBER_7);
			input(NUMBER_7);
			mService.sendCommand(DTMF_DIAL_PREFIX + NUMBER_7 + COMMAND_FUFFIX);
			break;

		case R.id.num8:
			if(soundPool!=null)
			soundPool.play(9,1, 1, 0, 0, 1);
		//	TTSController.getInstance(this).playText(NUMBER_8);
			input(NUMBER_8);
			mService.sendCommand(DTMF_DIAL_PREFIX + NUMBER_8 + COMMAND_FUFFIX);
			break;

		case R.id.num9:
			if(soundPool!=null)
			soundPool.play(10,1, 1, 0, 0, 1);
		//	TTSController.getInstance(this).playText(NUMBER_9);
			input(NUMBER_9);
			mService.sendCommand(DTMF_DIAL_PREFIX + NUMBER_9 + COMMAND_FUFFIX);
			break;

		case R.id.xin:
			if(soundPool!=null)
			soundPool.play(12,1, 1, 0, 0, 1);
		//	TTSController.getInstance(this).playText("星");
			input("*");
			mService.sendCommand(DTMF_DIAL_PREFIX + "*" + COMMAND_FUFFIX);
			break;

		case R.id.numjin:
			if(soundPool!=null)
			soundPool.play(11,1, 1, 0, 0, 1);
		//	TTSController.getInstance(this).playText("井");
			input("#");
			mService.sendCommand(DTMF_DIAL_PREFIX + "#" + COMMAND_FUFFIX);
			break;
		case R.id.del:

			delete();

			break;
		case R.id.dial:
				if (!TextUtils.isEmpty(phone_view.getText()))
					call(phone_view.getText().toString());

			break;

		case R.id.contact:

			startActivity(new Intent(this, ContactActivity.class));
	//		overridePendingTransition(R.anim.fade, R.anim.hold);

			break;

		case R.id.change:

			if (mService != null)
				mService.sendCommand(CHANGE_COMMAND);
			break;

		case R.id.music:
			int navi = Settings.System.getInt(getContentResolver(), ONE_NAVI, 0);
			switch (navi) {
			case 1:
				ComponentName componetName1 = new ComponentName("com.coagent.app","com.coagent.activity.SettingActivity");
				Intent intent1 = new Intent();
				intent1.setComponent(componetName1);
				intent1.setAction("android.intent.action.view");
				intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				try {
					startActivity(intent1);
				} catch (Exception e) {
					Toast.makeText(this, "未找到翼卡服务", Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				doStartApplicationWithPackageName(GLSX_AUTONAVI);
				break;
			}
			/*if(getSharedPreferences(BLUETOOTH, MODE_PRIVATE).getBoolean(A2DP_SWITCH, false)){
				startActivity(new Intent(this, MusicActivity.class));
				overridePendingTransition(R.anim.fade, R.anim.hold);
			}else{
				TTSController.getInstance(this).playText(show_info);
			}*/
			break;

		case R.id.setting:

			startActivity(new Intent(this, SettingActivity.class));
	//		overridePendingTransition(R.anim.fade, R.anim.hold);

			break;
		case R.id.log:

			startActivity(new Intent(this, CallLogActivity.class));
	//		overridePendingTransition(R.anim.fade, R.anim.hold);

			break;
		case R.id.home:
			
			break;
		default:
			break;
		}
	}

	private void doStartApplicationWithPackageName(String packagename) {  
		  
	    // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等  
	    PackageInfo packageinfo = null;  
	    try {  
	        packageinfo = getPackageManager().getPackageInfo(packagename, 0);  
	    } catch (NameNotFoundException e) {  
	        e.printStackTrace();  
	    }  
	    if (packageinfo == null) {  
	        return;  
	    }  
	  
	    // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent  
	    Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);  
	    resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
	    resolveIntent.setPackage(packageinfo.packageName);  
	  
	    // 通过getPackageManager()的queryIntentActivities方法遍历  
	    List<ResolveInfo> resolveinfoList = getPackageManager()  
	            .queryIntentActivities(resolveIntent, 0);  
	  
	    ResolveInfo resolveinfo = resolveinfoList.iterator().next();  
	    if (resolveinfo != null) {  
	        // packagename = 参数packname  
	        String packageName = resolveinfo.activityInfo.packageName;  
	        // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]  
	        String className = resolveinfo.activityInfo.name;  
	        // LAUNCHER Intent  
	        Intent intent = new Intent(Intent.ACTION_MAIN);  
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);  
	  
	        // 设置ComponentName参数1:packagename参数2:MainActivity路径  
	        ComponentName cn = new ComponentName(packageName, className);  
	  
	        intent.setComponent(cn);  
	        startActivity(intent);  
	    }
	}
	
	private void delete() {
		String p = phone_view.getText().toString();
		if (p.length() > 0) {
			phone_view.setText(p.substring(0, p.length() - 1));
		}
	}

}
