package cn.coogo.hardware.service.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.coogo.hardware.service.R;
/**
 * 
 * @author lzl
 *
 */
@SuppressLint("HandlerLeak")
public class ModelSelectActivity extends Activity implements OnClickListener{
	
	private static final String TAG = "ModelSelectActivity";
	public static final String RECORD_SERVICE_CLASS_NAME = "com.android.camera.RecordService";
    public static final String ACTION_ACC_ON_KEYEVENT = "android.intent.action.ACC_ON_KEYEVENT";
    public static final String ACTION_ACC_OFF_KEYEVENT = "android.intent.action.ACC_OFF_KEYEVENT";
    public static final String ACTION_SEC_ON_KEYEVENT = "android.intent.action.SEC_ON_KEYEVENT";
    public static final String ACTION_ENTER_MONITOR_MODEL = "action.clink.monitor_model";
    public static final String ACTION_ENTER_SHUTDOWN = "action.clink.shutdown";
    public static final String ACTION_FINISH_ACT = "action.finish.activity";
	private static final int MSG_SECOND = 0x01;
	private static final int FOR_RESULT_CODE = 0x02;
	//30s
	private int countdown = 30;
	private View mMonitor;
	private View mShutDown;
	private TextView mTimer;
	private Timer timer;
	private PowerManager mPowerManager; 
	private PowerManager.WakeLock mWakeLock; 
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SECOND:
				if(countdown-- < 0){
				    sendFinishCameraActivityBroadcast();
					timerCancel();
					screenOff();
					finish();
					//lockScreen();
				}else{
					mTimer.setText(String.valueOf(countdown < 0 ? 0 : countdown));
				}
				
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // No Titlebar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().getDecorView().setSystemUiVisibility(

                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_select);
		registerScreenOffReceiver();
		initViews();
		initEvents();
		startCountdown();
		initWakeLock();
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    //startAlarm(this);
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    timerCancel();
	    unregisterScreenOffReceiver();
	    releaseWakeLock();
	}


    private void initViews() {
		mMonitor = (TextView)findViewById(R.id.mMonitor);
		mShutDown = (TextView)findViewById(R.id.mShutDown);
		mTimer = (TextView)findViewById(R.id.mTimer);
	}
	
    private void initEvents() {
        mMonitor.setOnClickListener(this);
        mShutDown.setOnClickListener(this);
    }

    private void initWakeLock() {
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.acquire();
    }
    
    private void releaseWakeLock() {
        if(mWakeLock != null){
            mWakeLock.release();
            mWakeLock = null;
        }
    }
    
    private void screenOff(){
        if(mPowerManager != null && !isServiceRunning(this,RECORD_SERVICE_CLASS_NAME)){
            mPowerManager.goToSleep(SystemClock.uptimeMillis());
        }
    }
    
    private void registerScreenOffReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ACTION_ACC_ON_KEYEVENT);
        registerReceiver(mScreenOffReceiver, filter);
    }
    
    private void unregisterScreenOffReceiver() {
        unregisterReceiver(mScreenOffReceiver);
    }
    
    // close activity when screen turns off
    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "receiver : " + intent.getAction());
            String action = intent.getAction();
            if(TextUtils.equals(action, Intent.ACTION_SCREEN_OFF)) {
                sendFinishCameraActivityBroadcast();
                finish();
            }else if(TextUtils.equals(action,ACTION_ACC_ON_KEYEVENT)){
                finish();
            }
        }
    };
    

	@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mMonitor:
                if (isServiceRunning(this, RECORD_SERVICE_CLASS_NAME)) {
                    return;
                }
                sendFinishCameraActivityBroadcast();
                timerCancel();
                screenOff();
                finish();
                break;
            case R.id.mShutDown:
                if (isServiceRunning(this, RECORD_SERVICE_CLASS_NAME)) {
                    return;
                }
                sendShutDownBroadcast();
                break;
            default:
                break;
        }
    }

	private void sendShutDownBroadcast() {
		Log.i(TAG, "send ShutDown Broadcast...");
		Intent intent = new Intent(ACTION_ENTER_SHUTDOWN);
		sendBroadcast(intent);
	}

	private void sendMonitorBroadcast() {
		Log.i(TAG, "send Monitor Broadcast...");
		Intent intent = new Intent(ACTION_ENTER_MONITOR_MODEL);
		sendBroadcast(intent);
	}
	
    private void sendFinishCameraActivityBroadcast() {
        Log.i(TAG, "send finish activity Broadcast...");
        Intent intent = new Intent(ACTION_FINISH_ACT);
        sendBroadcast(intent);
    }
	
	
	private void startCountdown(){
		timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				mHandler.sendEmptyMessage(MSG_SECOND);
			}
		};
		timer.schedule(task, 1000, 1000);
	}
	
	private void timerCancel() {
		if(timer != null)
			timer.cancel();
	}
	
    public  static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        Log.i(TAG," isRunning : " + isRunning);
        return isRunning;
    }
	
}
